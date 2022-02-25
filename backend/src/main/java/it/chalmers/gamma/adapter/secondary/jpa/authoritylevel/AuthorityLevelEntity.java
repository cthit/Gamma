package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "authority_level")
public class AuthorityLevelEntity extends ImmutableEntity<String> {

    @Id
    @Column(name = "authority_level")
    private String authorityLevel;

    @OneToMany(mappedBy = "id.authorityLevel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected Set<AuthorityPostEntity> postEntityList;

    @OneToMany(mappedBy = "id.authorityLevel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected Set<AuthorityUserEntity> userEntityList;

    @OneToMany(mappedBy = "id.authorityLevel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected Set<AuthoritySuperGroupEntity> superGroupEntityList;

    protected AuthorityLevelEntity() {}

    public AuthorityLevelEntity(String name) {
        this.authorityLevel = name;
        this.postEntityList = new HashSet<>();
        this.userEntityList = new HashSet<>();
        this.superGroupEntityList = new HashSet<>();
    }

    @Override
    public String getId() {
        return this.authorityLevel;
    }

    public String getAuthorityLevel() {
        return authorityLevel;
    }

    public List<AuthorityPostEntity> getPosts() {
        return postEntityList.stream().toList();
    }

    public List<AuthorityUserEntity> getUsers() {
        return userEntityList.stream().toList();
    }

    public List<AuthoritySuperGroupEntity> getSuperGroups() {
        return superGroupEntityList.stream().toList();
    }
}
