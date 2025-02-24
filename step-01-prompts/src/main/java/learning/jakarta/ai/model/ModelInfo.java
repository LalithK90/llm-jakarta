package learning.jakarta.ai.model;

import lombok.Data;


public record ModelInfo(String modelName, String displayName) {

    public static ModelInfo from(ModelType modelType) {
        return new ModelInfo(modelType.getModelName(), modelType.getDisplayName());
    }
}
