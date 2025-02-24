package learning.jakarta.ai;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import learning.jakarta.ai.model.ModelInfo;
import learning.jakarta.ai.model.ModelType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Path("/models")
public class ModelResource {

    @Inject
    private LangChainService langChainService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ModelInfo> getAvailableModels() {
        log.info("Getting available models");
        List<ModelInfo> models = Arrays.stream(ModelType.values())
                .map(ModelInfo::from)
                .collect(Collectors.toList());
        log.info("Available models: {}", models);
        return models;
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public ModelInfo getCurrentModel() {
        ModelType currentModel = langChainService.getCurrentModel();
        log.info("Current model: {}", currentModel);
        return ModelInfo.from(currentModel);
    }

    @POST
    @Path("/switch/{modelName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response switchModel(@PathParam("modelName") String modelName) {
        log.info("Switching to model: {}", modelName);
        ModelType modelType = ModelType.fromModelName(modelName);
        langChainService.switchModel(modelType);
        ModelInfo modelInfo = ModelInfo.from(modelType);
        log.info("Switched to model: {}", modelInfo);
        return Response.ok(modelInfo).build();
    }
}
