package learning.jakarta.ai;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import learning.jakarta.ai.model.PersonalityInfo;
import learning.jakarta.ai.prompts.Personality;
import learning.jakarta.ai.prompts.PersonalityType;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Path("/personalities")
public class PersonalityResource {

    @Inject
    private LangChainService langChainService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PersonalityInfo> getAvailablePersonalities() {
        log.info("Getting available personalities");
        List<PersonalityInfo> personalities = Arrays.stream(PersonalityType.values())
                .map(PersonalityInfo::from)
                .toList();
        log.info("Available personalities: {}", personalities);
        return personalities;
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public PersonalityInfo getCurrentPersonality() {
        PersonalityType currentPersonality = langChainService.getPersonalityType();
        log.info("Current personality: {}", currentPersonality);
        return PersonalityInfo.from(currentPersonality);
    }

    @POST
    @Path("/switch/{personalityName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response switchPersonality(@PathParam("personalityName") String personalityName) {
        log.info("Switching to personality: {}", personalityName);
        PersonalityType personalityType = PersonalityType.valueOf(personalityName);
        langChainService.switchPersonality(personalityType);
        PersonalityInfo personalityInfo = PersonalityInfo.from(personalityType);
        log.info("Switched to personality: {}", personalityInfo);
        return Response.ok(personalityInfo).build();
    }
}
