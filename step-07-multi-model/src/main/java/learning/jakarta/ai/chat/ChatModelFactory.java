package learning.jakarta.ai.chat;

import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import learning.jakarta.ai.config.*;
import learning.jakarta.ai.model.ModelType;
import learning.jakarta.ai.model.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ChatModelFactory {
    @Inject
    private AIConfigFactory configFactory;

    public StreamingChatLanguageModel createChatModel(ModelType modelType) {
        AIProviderConfig config = configFactory.getConfig(modelType.getProvider());

        return switch (modelType.getProvider()) {
            case OPENAI -> createOpenAIModel(modelType, (OpenAIConfig) config);
            case ANTHROPIC -> createAnthropicModel(modelType, (AnthropicConfig) config);
            case GOOGLE -> createGoogleModel(modelType, (GoogleConfig) config);
            case MISTRAL -> createMistralModel(modelType, (MistralConfig) config);
        };
    }

    private StreamingChatLanguageModel createOpenAIModel(ModelType modelType, OpenAIConfig config) {
        var builder = OpenAiStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(modelType.getModelName())
                .temperature(config.getTemperature())
                .timeout(config.getTimeout())
                .maxTokens(config.getMaxTokens())
                .frequencyPenalty(config.getFrequencyPenalty())
                .logRequests(config.isLogRequests())
                .logResponses(config.isLogResponses());

        String orgId = config.getOrganizationId();
        if (orgId != null && !orgId.isEmpty()) {
            builder = builder.organizationId(orgId);
        }

        return builder.build();
    }

    //Findings:
    // the frequency penalty is only supported by OPENAI, following any other provider will not have this feature

    private StreamingChatLanguageModel createAnthropicModel(ModelType modelType, AnthropicConfig config) {
        log.warn("Using OpenAI-compatible endpoint for Anthropic. Native support coming soon.");
        return AnthropicStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(modelType.getModelName())
                .temperature(config.getTemperature())
                .timeout(config.getTimeout())
                .maxTokens(config.getMaxTokens())
                .logRequests(config.isLogRequests())
                .logResponses(config.isLogResponses())
                .build();
    }

    private StreamingChatLanguageModel createGoogleModel(ModelType modelType, GoogleConfig config) {
        log.warn("Using OpenAI-compatible endpoint for Google. Native support coming soon.");
        return OpenAiStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(modelType.getModelName())
                .baseUrl(config.getBaseUrl())
                .temperature(config.getTemperature())
                .timeout(config.getTimeout())
                .maxTokens(config.getMaxTokens())
                .logRequests(config.isLogRequests())
                .logResponses(config.isLogResponses())
                .build();
    }

    private StreamingChatLanguageModel createMistralModel(ModelType modelType, MistralConfig config) {
        log.warn("Using OpenAI-compatible endpoint for Mistral. Native support coming soon.");
        return MistralAiStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(modelType.getModelName())
                .baseUrl(config.getBaseUrl())
                .temperature(config.getTemperature())
                .timeout(config.getTimeout())
                .maxTokens(config.getMaxTokens())
                .safePrompt(true)
                .logRequests(config.isLogRequests())
                .logResponses(config.isLogResponses())
                .build();
    }
}
