package it.chalmers.gamma.domain.dto.user;

import java.util.Objects;
import java.util.UUID;

public class WhitelistDTO {
    private final UUID id;
    private final String cid;


    public WhitelistDTO(UUID id, String cid) {
        this.id = id;
        this.cid = cid;
    }

    public UUID getId() {
        return this.id;
    }

    public String getCid() {
        return this.cid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WhitelistDTO that = (WhitelistDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.cid, that.cid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.id, this.cid);
    }

    @Override
    public String toString() {
        return "WhitelistDTO{"
                + "id=" + this.id
                + ", cid='" + this.cid + '\''
                + '}';
    }
}
