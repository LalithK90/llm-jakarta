package learning.jakarta.ai.chat;

import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import learning.jakarta.ai.config.*;
import learning.jakarta.ai.model.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@ApplicationScoped
public class ChatModelFactory {

    @Inject
    private AIConfigFactory configFactory;

    public StreamingChatLanguageModel createChatModel(ModelType modelType) {
        AIProviderConfig config = configFactory.getConfig(modelType.getProvider());

        return switch (modelType.getProvider()) {
            case OPENAI -> {
                OpenAIConfig openAiConfig = (OpenAIConfig) config;
                yield buildOpenAiModel(
                        openAiConfig.getApiKey(),
                        null, // baseUrl is not used for native OpenAI
                        modelType.getModelName(),
                        openAiConfig.getTemperature(),
                        openAiConfig.getTimeout(),
                        openAiConfig.getMaxTokens(),
                        openAiConfig.getFrequencyPenalty(),
                        openAiConfig.isLogRequests(),
                        openAiConfig.isLogResponses(),
                        openAiConfig.getOrganizationId()
                );
            }
            case GOOGLE -> {
                GoogleConfig googleConfig = (GoogleConfig) config;
                log.warn("Using OpenAI-compatible endpoint for Google. Native support coming soon.");
                yield buildOpenAiModel(
                        googleConfig.getApiKey(),
                        googleConfig.getBaseUrl(),
                        modelType.getModelName(),
                        googleConfig.getTemperature(),
                        googleConfig.getTimeout(),
                        googleConfig.getMaxTokens(),
                        null, // frequencyPenalty not supported by Google endpoint
                        googleConfig.isLogRequests(),
                        googleConfig.isLogResponses(),
                        null // organizationId not applicable
                );
            }
            case OLAMA -> {
                OlamaConfig olamaConfig = (OlamaConfig) config;
                yield buildOpenAiModel(
                        olamaConfig.getApiKey(),
                        olamaConfig.getBaseUrl(),
                        modelType.getModelName(),
                        olamaConfig.getTemperature(),
                        olamaConfig.getTimeout(),
                        olamaConfig.getMaxTokens(),
                        olamaConfig.getFrequencyPenalty(),
                        olamaConfig.isLogRequests(),
                        olamaConfig.isLogResponses(),
                        null
                );
            }
            case ANTHROPIC -> {
                AnthropicConfig anthropicConfig = (AnthropicConfig) config;
                log.warn("Using OpenAI-compatible endpoint for Anthropic. ");
                yield AnthropicStreamingChatModel.builder()
                        .apiKey(anthropicConfig.getApiKey())
                        .modelName(modelType.getModelName())
                        .temperature(anthropicConfig.getTemperature())
                        .timeout(anthropicConfig.getTimeout())
                        .maxTokens(anthropicConfig.getMaxTokens())
                        .logRequests(anthropicConfig.isLogRequests())
                        .logResponses(anthropicConfig.isLogResponses())
                        .build();
            }
            case MISTRAL -> {
                MistralConfig mistralConfig = (MistralConfig) config;
                log.warn("Using OpenAI-compatible endpoint for Mistral.");
                yield MistralAiStreamingChatModel.builder()
                        .apiKey(mistralConfig.getApiKey())
                        .baseUrl(mistralConfig.getBaseUrl())
                        .modelName(modelType.getModelName())
                        .temperature(mistralConfig.getTemperature())
                        .timeout(mistralConfig.getTimeout())
                        .maxTokens(mistralConfig.getMaxTokens())
                        .safePrompt(true)
                        .logRequests(mistralConfig.isLogRequests())
                        .logResponses(mistralConfig.isLogResponses())
                        .build();
            }
        };
    }

    /**
     * Helper method to build models using the OpenAI-compatible builder.
     */
    private StreamingChatLanguageModel buildOpenAiModel(String apiKey,
                                                        String baseUrl,
                                                        String modelName,
                                                        double temperature,
                                                        Duration timeout,
                                                        int maxTokens,
                                                        Double frequencyPenalty,
                                                        boolean logRequests,
                                                        boolean logResponses,
                                                        String organizationId) {

        var builder = OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .timeout(timeout)
                .maxTokens(maxTokens)
                .logRequests(logRequests)
                .logResponses(logResponses);

        if (baseUrl != null && !baseUrl.isEmpty()) {
            builder.baseUrl(baseUrl);
        }
        if (frequencyPenalty != null) {
            builder.frequencyPenalty(frequencyPenalty);
        }
        if (organizationId != null && !organizationId.isEmpty()) {
            builder.organizationId(organizationId);
        }
        return builder.build();
    }
}
