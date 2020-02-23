package it.chalmers.gamma.domain.dto.user;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ActivationCodeDTO {
    private final UUID id;
    private final WhitelistDTO whitelistDTO;
    private final String code;
    private final Instant createdAt;
    private final int passwordExpirationTime;

    public ActivationCodeDTO(UUID id,
                             WhitelistDTO whitelistDTO,
                             String code,
                             Instant createdAt,
                             int passwordExpirationTime) {
        this.id = id;
        this.whitelistDTO = whitelistDTO;
        this.code = code;
        this.createdAt = createdAt;
        this.passwordExpirationTime = passwordExpirationTime;
    }

    public UUID getId() {
        return this.id;
    }

    @JsonIgnore
    public WhitelistDTO getWhitelistDTO() {
        return this.whitelistDTO;
    }

    public String getCid() {
        return this.whitelistDTO.getCid();
    }

    public String getCode() {
        return this.code;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public boolean isValid() {
        return Instant.now().isBefore(this.createdAt.plus(Duration.ofSeconds(this.passwordExpirationTime)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActivationCodeDTO that = (ActivationCodeDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.whitelistDTO, that.whitelistDTO)
                && Objects.equals(this.code, that.code)
                && Objects.equals(this.createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                this.id,
                this.whitelistDTO,
                this.code,
                this.createdAt);
    }
}
