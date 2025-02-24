package learning.jakarta.ai.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@ApplicationScoped
public class OpenAIConfig extends AIProviderConfig {
    @Inject
    @ConfigProperty(name = "langchain4j.open-ai.api-key")
    private String apiKey;

    @Inject
    @ConfigProperty(name = "langchain4j.open-ai.organization-id", defaultValue = "NONE")
    private String organizationId;

    public String getOrganizationId() {
        return "NONE".equals(organizationId) ? null : organizationId;
    }

    @Inject
    @ConfigProperty(name = "langchain4j.open-ai.base-url", defaultValue = "https://api.openai.com/v1")
    private String baseUrl;
}
