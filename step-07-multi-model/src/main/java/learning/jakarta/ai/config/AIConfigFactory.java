package learning.jakarta.ai.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import learning.jakarta.ai.model.Provider;

@ApplicationScoped
public class AIConfigFactory {
    @Inject
    private OpenAIConfig openAIConfig;

    @Inject
    private AnthropicConfig anthropicConfig;

    @Inject
    private GoogleConfig googleConfig;

    @Inject
    private MistralConfig mistralConfig;

    @Inject
    private OlamaConfig olamaConfig;

    public AIProviderConfig getConfig(Provider provider) {
        return switch (provider) {
            case OPENAI -> openAIConfig;
            case ANTHROPIC -> anthropicConfig;
            case GOOGLE -> googleConfig;
            case MISTRAL -> mistralConfig;
            case OLAMA -> olamaConfig;
        };
    }
}