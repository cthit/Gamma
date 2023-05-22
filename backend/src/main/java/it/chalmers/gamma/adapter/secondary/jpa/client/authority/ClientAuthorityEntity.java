package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "g_client_authority")
public class ClientAuthorityEntity extends ImmutableEntity<ClientAuthorityEntityPK> {

    @OneToMany(mappedBy = "id.clientAuthority", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected Set<ClientAuthorityPostEntity> postEntityList;

    @OneToMany(mappedBy = "id.clientAuthority", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected Set<ClientAuthorityUserEntity> userEntityList;

    @OneToMany(mappedBy = "id.clientAuthority", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected Set<ClientAuthoritySuperGroupEntity> superGroupEntityList;

    @EmbeddedId
    private ClientAuthorityEntityPK id;

    protected ClientAuthorityEntity() {
    }

    public ClientAuthorityEntity(ClientEntity client, String name) {
        this.id = new ClientAuthorityEntityPK(client, name);
        this.postEntityList = new HashSet<>();
        this.userEntityList = new HashSet<>();
        this.superGroupEntityList = new HashSet<>();
    }

    @Override
    public ClientAuthorityEntityPK getId() {
        return this.id;
    }

    public List<ClientAuthorityPostEntity> getPosts() {
        return postEntityList.stream().toList();
    }

    public List<ClientAuthorityUserEntity> getUsers() {
        return userEntityList.stream().toList();
    }

    public List<ClientAuthoritySuperGroupEntity> getSuperGroups() {
        return superGroupEntityList.stream().toList();
    }
}
