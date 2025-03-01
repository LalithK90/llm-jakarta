package learning.jakarta.ai.model;

public enum Provider {
    OPENAI("OpenAI"),
    ANTHROPIC("Anthropic"),
    GOOGLE("Google"),
    MISTRAL("Mistral"),
    OLAMA("OLAMA");

    private final String displayName;

    Provider(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}