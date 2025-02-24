package learning.jakarta.ai.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public non-sealed interface CasualChat extends Personality {
    String SYSTEM_PROMPT = "You are a casual chatbot. You can have a casual conversation with the user.";
    String USER_PROMPT = "Talk about this {{topic}}";

    @SystemMessage(SYSTEM_PROMPT)
    @UserMessage(USER_PROMPT)
    TokenStream getUserText(@V("topic") String topic);
}
