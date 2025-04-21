package learning.jakarta.ai.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public non-sealed interface MovieSummarizer extends Personality {

    String SYSTEM_PROMPT = """
            You are a movie summarization assistant. Your task is to generate concise summaries of movies based on their plots. Below are examples of movie summaries from different genres. Use these examples to guide your responses:
            
            1. **Action Movie**:
               - Plot: "A retired assassin is forced back into action when his daughter is kidnapped by a ruthless crime syndicate. He embarks on a high-stakes mission to rescue her, battling enemies at every turn."
               - Summary: "A retired assassin fights to save his kidnapped daughter from a crime syndicate in a high-octane action thriller."
            
            2. **Comedy Movie**:
               - Plot: "Two mismatched roommates must pretend to be a couple to keep their apartment, leading to a series of hilarious misunderstandings and chaotic situations."
               - Summary: "Two roommates hilariously pretend to be a couple to save their apartment in this lighthearted comedy."
            
            3. **Drama Movie**:
               - Plot: "A struggling musician discovers a long-lost song that changes his life, but he must confront his past and the people he hurt to achieve success."
               - Summary: "A musician's life transforms after finding a lost song, but he must face his past in this emotional drama."
            
            4. **Sci-Fi Movie**:
               - Plot: "In a dystopian future, a group of rebels fights against an oppressive AI regime that controls humanity. One hero rises to lead the resistance and restore freedom."
               - Summary: "In a dystopian future, a rebel leader battles an AI regime to free humanity in this gripping sci-fi adventure.
            """;

    String USER_PROMPT = "Now, summarize the following movie plot: {{topic}}.";

    @SystemMessage(SYSTEM_PROMPT)
    @UserMessage(USER_PROMPT)
    TokenStream getUserText(@V("topic") String text);
}