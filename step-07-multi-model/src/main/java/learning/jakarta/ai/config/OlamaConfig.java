package learning.jakarta.ai.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@ApplicationScoped
public class OlamaConfig extends AIProviderConfig {
    @Inject
    @ConfigProperty(name = "langchain4j.ollama.api-key", defaultValue = "ollma")
    private String apiKey;

    @Inject
    @ConfigProperty(name = "langchain4j.ollama.base-url", defaultValue = "http://localhost:11434/v1")
    private String baseUrl;
}
