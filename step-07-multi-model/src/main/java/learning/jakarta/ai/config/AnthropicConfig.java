package learning.jakarta.ai.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@ApplicationScoped
public class AnthropicConfig extends AIProviderConfig {
    @Inject
    @ConfigProperty(name = "langchain4j.anthropic.api-key")
    private String apiKey;

    @Inject
    @ConfigProperty(name = "langchain4j.anthropic.base-url", defaultValue = "https://api.anthropic.com")
    private String baseUrl;

    @Inject
    @ConfigProperty(name = "langchain4j.anthropic.version", defaultValue = "2024-02-29")
    private String version;
}
