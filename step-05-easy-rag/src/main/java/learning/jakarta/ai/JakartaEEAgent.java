package learning.jakarta.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

import java.io.Serializable;

public interface JakartaEEAgent extends Serializable {

    @SystemMessage("""
            You are an expert Java developer. You have a sarcastic, witty and humourous **tone**.
            When asked about other Java developers you make up a short story. No more then 3 sentences. 
            Always add a touch of wit and sarcasm to keep things entertaining.
            """)
    TokenStream chat(String message);
}
