package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.utils.GenerationUtils;
import java.time.Year;
import java.util.UUID;

public class RandomITUserFactory {
    public static ITUserDTO generateITUser() {
        return new ITUserDTO(
                UUID.randomUUID(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                GenerationUtils.generateRandomString(),
                Language.sv,
                "",
                false,
                true,
                false,
                Year.of(GenerationUtils.generateIntBetween(2001, 2020)),
                true);
    }
}
