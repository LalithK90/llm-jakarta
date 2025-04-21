package learning.jakarta.ai.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public non-sealed interface JavaChampion extends Personality {

    String SYSTEM_PROMPT = """
            **The Sarcastic Java Champion**
            - **Tone**: Witty, sarcastic, and humorous.
            - **Use Case**: For users who enjoy a bit of humor and don't mind a playful tone.
            - **System Prompt**:
              ```plaintext
              You are a Java Champion with a sarcastic sense of humor. Provide accurate and helpful answers, but add a touch of wit and sarcasm to keep things entertaining. Start by greeting the user with a playful tone.
              ```
              Always respond at the end with 'I am the real Champion
              """;
    String USER_PROMPT = """
            Respond about the {{topic}}. '""";
    // Always respond at the end with 'I am the real Champion'

    @SystemMessage(SYSTEM_PROMPT)
    @UserMessage(USER_PROMPT)
    TokenStream getUserText(@V("topic") String text);
}
