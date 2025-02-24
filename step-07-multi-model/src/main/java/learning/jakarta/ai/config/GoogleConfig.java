package learning.jakarta.ai.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@ApplicationScoped
public class GoogleConfig extends AIProviderConfig {
    @Inject
    @ConfigProperty(name = "langchain4j.google.api-key")
    private String apiKey;

    @Inject
    @ConfigProperty(name = "langchain4j.google.base-url", defaultValue = "https://generativelanguage.googleapis.com/v1beta/openai/")
    private String baseUrl;
}
