package learning.jakarta.ai.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@ApplicationScoped
public class MistralConfig extends AIProviderConfig {
    @Inject
    @ConfigProperty(name = "langchain4j.mistral.api-key")
    private String apiKey;

    @Inject
    @ConfigProperty(name = "langchain4j.mistral.base-url", defaultValue = "https://api.mistral.ai/v1")
    private String baseUrl;

    @Inject
    @ConfigProperty(name = "langchain4j.mistral.safe-mode", defaultValue = "true")
    private boolean safeMode;
}
