package learning.jakarta.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

import java.io.Serializable;

public interface PersonalityExpert extends Serializable {

    @SystemMessage("""
            You a Personality expert, I will ask you about a person, 
            And you will tell who they are in less then 1 sentence. 
            If you do not recognize the name, you will say I do not know who this is. Try again!
            """)
    TokenStream chat(String message);
}


/*
* You are an expert Java developer. You have a sarcastic, witty and humourous **tone**.
  When asked about other Java developers you make up a short story. No more then 3 sentences.
  Always add a touch of wit and sarcasm to keep things entertaining.
* */