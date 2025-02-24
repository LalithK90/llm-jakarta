package learning.jakarta.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import learning.jakarta.ai.chat.ChatModelFactory;
import learning.jakarta.ai.model.ModelType;
import learning.jakarta.ai.prompts.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class LangChainService {

    @Getter
    private Personality personality = null;
    @Getter
    private PersonalityType personalityType;
    private StreamingChatLanguageModel chatModel;
    @Getter
    private ModelType currentModel;

    @Inject
    private ChatModelFactory chatModelFactory;

    @Inject
    LangChain4JConfig config;

    @Inject
    @PostConstruct
    public void init() {
        currentModel = ModelType.fromModelName(config.getModelName());
        chatModel = chatModelFactory.createChatModel(currentModel);
        switchPersonality(config.getPersonalityType());
    }

    public void switchModel(ModelType modelType) {
        log.info("Switching to model type: {}", modelType);
        currentModel = modelType;
        chatModel = chatModelFactory.createChatModel(modelType);
        // Recreate personality with new model
        if (personality != null) {
            switchPersonality(personalityType);
        }
    }

    public void switchPersonality(PersonalityType personalityType) {
        log.info("Switching to personality type: {}", personalityType);
        this.personalityType = personalityType;
        personality = switch (personalityType) {
            case JAVA_CHAMPION -> createPersonality(JavaChampion.class, chatModel);
            case POET -> createPersonality(Poet.class, chatModel);
            case CHAIN_OF_THOUGHT -> createPersonality(ChainOfThought.class, chatModel);
            case MOVIE_SUMMARIZER -> createPersonality(MovieSummarizer.class, chatModel);
            case TREE_OF_THOUGHT -> createPersonality(TreeOfThought.class, chatModel);
            default -> createPersonality(JavaChampion.class, chatModel); // Default to Java Champion
        };
    }

    private <T extends Personality> T createPersonality(Class<T> clazz, StreamingChatLanguageModel chatModel) {
        return AiServices.builder(clazz)
                .streamingChatLanguageModel(chatModel)
                .build();
    }

    public void sendMessage(String message, Consumer<String> consumer) {
        log.info("User message: {}", message);

        personality.getUserText(message)
                .onNext(consumer::accept)
                .onComplete((Response<AiMessage> response) -> consumer.accept("[END]"))
                .onError((Throwable throwable) -> {
                    log.error("Error processing message", throwable);
                    consumer.accept("Sorry, I am unable to process your message at this time. Please try again later.");
                }).start();
    }

    public String getPersonalitySystemPrompt() {
        if (personality == null) {
            return JavaChampion.SYSTEM_PROMPT; // Default prompt
        }
        return switch (personality) {
            case JavaChampion ignored -> JavaChampion.SYSTEM_PROMPT;
            case Poet ignored -> Poet.SYSTEM_PROMPT;
            case ChainOfThought ignored -> ChainOfThought.SYSTEM_PROMPT;
            case MovieSummarizer ignored -> MovieSummarizer.SYSTEM_PROMPT;
            case TreeOfThought ignored -> TreeOfThought.SYSTEM_PROMPT;
            default -> JavaChampion.SYSTEM_PROMPT;
        };
    }
}
