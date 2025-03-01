package learning.jakarta.ai.model;

import learning.jakarta.ai.prompts.PersonalityType;
import lombok.Data;
import java.util.stream.Collectors;
import java.util.Arrays;

public record PersonalityInfo(String name, String displayName) {
    public static PersonalityInfo from(PersonalityType personalityType) {
        String formattedName = Arrays.stream(personalityType.name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));

        return new PersonalityInfo(personalityType.name(), formattedName);
    }

}