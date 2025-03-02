package learning.jakarta.ai.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JavaCompilerTool {
    private static final Pattern javaClassPattern = Pattern.compile("\\bpublic\\s+(class|interface|enum)\\s+([A-Za-z0-9_]+)");

    public static void main(String[] args) {
        JavaCompilerTool tool = new JavaCompilerTool();
        String sourceCode = """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;

        // Compile the source code
        Output output = tool.javac(sourceCode);
        if (output.getBytecodeDirPath() != null) {
            // Run the compiled class
            String bytecodeDirPath = output.getBytecodeDirPath();
            String result = tool.run(bytecodeDirPath, output.getClassName());
            System.out.println("Output: " + result);

            // Disassemble the compiled class
            String disassembly = tool.javaP(bytecodeDirPath, output.className, "-c");
            System.out.println("Disassembly: " + disassembly);
        } else {
            System.err.println("Compilation failed with errors: " + output.getErrors());
        }
    }

    @Tool("Compiles the provided Java source code and returns the resulting bytecode. The source code should be valid Java code.")
    public Output javac(@P("sourceCode") String sourceCode) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return Output.builder()
                    .bytecodeDirPath(null)
                    .errors(List.of("No Java compiler found. Make sure you are using a JDK."))
                    .build();
        }

        // Match the class name from the source code
        Matcher matcher = javaClassPattern.matcher(sourceCode);
        String fileName = "UnknownClass";
        if (matcher.find()) {
            fileName = matcher.group(2); // Extract the public class name
        }

        try {
            // Create a temporary file with the correct name matching the public class
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File sourceFile = new File(tempDir, fileName + ".java");
            try (FileWriter writer = new FileWriter(sourceFile)) {
                writer.write(sourceCode);
            }

            // Compile the source file
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile));

            // Add options to suppress the annotation processing warning
            List<String> options = Arrays.asList("-proc:none", "-Xlint:-options");

            boolean success = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();

            if (success) {
                // Locate the compiled .class file
                String classFilePathDir = new File(tempDir, fileName + ".class").getParent();
                return Output.builder()
                        .bytecodeDirPath(classFilePathDir)
                        .className(fileName)
                        .errors(Collections.emptyList())
                        .build();
            } else {
                // Capture and return compilation errors
                List<String> errors = new ArrayList<>();
                for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                    errors.add(diagnostic.getMessage(Locale.getDefault()));
                }
                return Output.builder()
                        .bytecodeDirPath(null)
                        .errors(errors)
                        .build();
            }
        } catch (IOException e) {
            return Output.builder()
                    .bytecodeDirPath(null)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Tool("Executes the provided {{bytecodeDirPath}} and {{className}} , assuming it contains a class with a main method, and returns the output.")
    public String run(@P("bytecodeDirPath") String bytecodeDirPath, @P("className") String className) {
        log.info("Running class {} in directory {}", className, bytecodeDirPath);
        try {
            // Verify if the directory exists
            File dir = new File(bytecodeDirPath);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new IllegalArgumentException("Invalid bytecode directory: " + bytecodeDirPath);
            }

            // Create a URLClassLoader to load the compiled class
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{dir.toURI().toURL()});
            Class<?> cls = Class.forName(className, true, classLoader);
            Method mainMethod = cls.getMethod("main", String[].class);

            // Capture System.out
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
            PrintStream originalOut = System.out;

            try {
                System.setOut(printStream);

                // Invoke the main method of the compiled class
                mainMethod.invoke(null, (Object) new String[]{});

            } finally {
                // Restore the original System.out
                System.setOut(originalOut);
            }

            // Return the captured output as a string
            return outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new RuntimeException("Execution failed: " + e.getMessage(), e);
        }
    }

    @Tool("Disassembles the given bytecode filePath and fileName using javap. Example arguments: '-c' for disassembly, '-verbose' for detailed output.")
    public String javaP(String bytecodeFilePath, String fileName, String... args) {
        log.info("Running javap on class {} in directory {}", fileName, bytecodeFilePath);
        try {
            List<String> command = new ArrayList<>();
            command.add("javap");
            command.addAll(Arrays.asList(args));
            command.add(bytecodeFilePath + "/" + fileName + ".class");
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream error = new ByteArrayOutputStream();

            try (InputStream inputStream = process.getInputStream();
                 InputStream errorStream = process.getErrorStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                while ((length = errorStream.read(buffer)) != -1) {
                    error.write(buffer, 0, length);
                }
            } finally {
                outputStream.close();
                error.close();
            }

            return outputStream.toString(StandardCharsets.UTF_8) + " " + error.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error running javap", e);
            throw new UncheckedIOException(e);
        }
    }

    @Data
    @Builder
    public static class Output {
        private String className;
        private String bytecodeDirPath;
        private List<String> errors;
    }
}