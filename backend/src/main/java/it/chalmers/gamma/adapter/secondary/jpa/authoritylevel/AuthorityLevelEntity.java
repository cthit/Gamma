package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authority_level")
public class AuthorityLevelEntity extends ImmutableEntity<String> {

    @Id
    @Column(name = "authority_level")
    private String authorityLevel;

    @OneToMany(mappedBy = "id.authorityLevel", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<AuthorityPostEntity> postEntityList;

    @OneToMany(mappedBy = "id.authorityLevel", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<AuthorityUserEntity> userEntityList;

    @OneToMany(mappedBy = "id.authorityLevel", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<AuthoritySuperGroupEntity> superGroupEntityList;

    protected AuthorityLevelEntity() {}

    public AuthorityLevelEntity(String name) {
        this.authorityLevel = name;
        this.postEntityList = new ArrayList<>();
        this.userEntityList = new ArrayList<>();
        this.superGroupEntityList = new ArrayList<>();
    }

    @Override
    public String getId() {
        return this.authorityLevel;
    }

    public String getAuthorityLevel() {
        return authorityLevel;
    }

    public List<AuthorityPostEntity> getPosts() {
        return postEntityList;
    }

    public List<AuthorityUserEntity> getUsers() {
        return userEntityList;
    }

    public List<AuthoritySuperGroupEntity> getSuperGroups() {
        return superGroupEntityList;
    }
}
