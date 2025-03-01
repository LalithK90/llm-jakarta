package learning.jakarta.ai.model;

import lombok.Getter;

@Getter
public enum ModelType {
    // OpenAI Models
    GPT_4o("gpt-4o", "GPT-40", Provider.OPENAI),
    GPT_4("gpt-4", "GPT-4", Provider.OPENAI),
    GPT_3_5_TURBO("gpt-3.5-turbo", "GPT-3.5 Turbo", Provider.OPENAI),
    
    // Anthropic Models
    CLAUDE_3_OPUS("claude-3-opus-latest", "Claude-3 Opus", Provider.ANTHROPIC),
    CLAUDE_3_SONNET("claude-3-7-sonnet-latest", "Claude-3 Sonnet", Provider.ANTHROPIC),
    CLAUDE_3_HAIKU("claude-3-5-haiku-latest", "Claude-3 Haiku", Provider.ANTHROPIC),

    // Google Models
    //
    GEMINI_2_0_FLASH("gemini-2.0-flash", "Gemini 2.0 Flash", Provider.GOOGLE),
    GEMINI_2_0_FLASH_LITE("gemini-2.0-flash-lite", "Gemini 2.0 Flash-Lite", Provider.GOOGLE),
    GEMINI_1_5_PRO("gemini-1.5-pro", "Gemini 1.5 Pro", Provider.GOOGLE),
    
    // Mistral Models
    MISTRAL_SMALL("mistral-small-latest", "Mistral Small", Provider.MISTRAL),
    MISTRAL_PIXTRAL("pixtral-12b-2409", "Pixtral", Provider.MISTRAL),
    MISTRAL_NEMO("open-mistral-nemo", "Mistral Nemo", Provider.MISTRAL),

    // OLAMA Models
    LLAMA3_2("llama3.2", "Llama 3.2", Provider.OLAMA),
    DEEPSEEK_R1_32B("deepseek-r1:32b","Deepseek R1 32B",Provider.OLAMA);

    private final String modelName;
    private final String displayName;
    private final Provider provider;

    ModelType(String modelName, String displayName, Provider provider) {
        this.modelName = modelName;
        this.displayName = displayName;
        this.provider = provider;
    }

    public static ModelType fromModelName(String modelName) {
        for (ModelType type : values()) {
            if (type.modelName.equals(modelName)) {
                return type;
            }
        }
        return GPT_3_5_TURBO; // default model
    }
}