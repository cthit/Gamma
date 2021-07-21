package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.domain.AuthorityLevel;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.adapter.secondary.jpa.util.SingleImmutableEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authority_level")
public class AuthorityLevelEntity extends MutableEntity<AuthorityLevelName> {

    @Id
    @Column(name = "autority_level")
    private String authorityLevel;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "authority_post")
    private List<AuthorityPostEntity> postEntityList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "authority_user")
    private List<AuthorityUserEntity> userEntityList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "authority_super_group")
    private List<AuthoritySuperGroupEntity> superGroupEntityList;

    protected AuthorityLevelEntity() {}

    protected AuthorityLevelEntity(AuthorityLevelName authorityLevel) {
        this.authorityLevel = authorityLevel.value();
        this.postEntityList = new ArrayList<>();
        this.userEntityList = new ArrayList<>();
        this.superGroupEntityList = new ArrayList<>();
    }

    protected AuthorityLevelEntity(AuthorityLevel authorityLevel) {
        this.authorityLevel = authorityLevel.name().value();
        this.postEntityList = authorityLevel.posts()
                .stream().map(AuthorityPostEntity::new).toList();
        this.userEntityList = authorityLevel.users()
                .stream().map(AuthorityUserEntity::new).toList();
        this.superGroupEntityList = authorityLevel.superGroups()
                .stream().map(AuthoritySuperGroupEntity::new).toList();
    }

    @Override
    protected AuthorityLevelName id() {
        return AuthorityLevelName.valueOf(this.authorityLevel);
    }

    public AuthorityLevel toDomain() {
        return new AuthorityLevel(
                AuthorityLevelName.valueOf(this.authorityLevel),
                this.postEntityList.stream().map(AuthorityPostEntity::toDomain).toList(),
                this.superGroupEntityList.stream().map(AuthoritySuperGroupEntity::toDomain).toList(),
                this.userEntityList.stream().map(AuthorityUserEntity::toDomain).toList()
        );
    }

}
