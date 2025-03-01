package learning.jakarta.ai.model;

public record ModelInfo(String modelName, String displayName, String provider) {

    public static ModelInfo from(ModelType modelType) {
        return new ModelInfo(
            modelType.getModelName(),
            modelType.getDisplayName(),
            modelType.getProvider().getDisplayName()
        );
    }
}