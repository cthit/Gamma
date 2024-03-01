package it.chalmers.gamma.domain.dto.user;

import it.chalmers.gamma.domain.dto.access.ITClientDTO;

import java.util.Objects;

public class ITUserApprovalDTO {

    private final ITUserDTO user;
    private final ITClientDTO client;

    public ITUserApprovalDTO(ITUserDTO user, ITClientDTO client) {
        this.user = user;
        this.client = client;
    }

    public ITUserDTO getUser() {
        return this.user;
    }

    public ITClientDTO getClient() {
        return this.client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ITUserApprovalDTO that = (ITUserApprovalDTO) o;
        return Objects.equals(this.user, that.user)
            && Objects.equals(this.client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.user, this.client);
    }

    @Override
    public String toString() {
        return "ITUserApprovalDTO{"
            + "user=" + user
            + ", client=" + client
            + '}';
    }
}
