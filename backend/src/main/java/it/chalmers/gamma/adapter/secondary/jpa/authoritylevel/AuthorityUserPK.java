package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.Id;
import it.chalmers.gamma.domain.user.UserId;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthorityUserPK implements Id<AuthorityUserPK.AuthorityUserPKRecord> {

    protected record AuthorityUserPKRecord(UserId userId, AuthorityLevelName authorityLevelName) { }

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity userEntity;

    @JoinColumn(name = "authority_level")
    @ManyToOne
    private AuthorityLevelEntity authorityLevel;

    protected AuthorityUserPK() {

    }

    public AuthorityUserPK(UserEntity userEntity, AuthorityLevelEntity authorityLevel) {
        this.userEntity = userEntity;
        this.authorityLevel = authorityLevel;
    }

    @Override
    public AuthorityUserPKRecord getValue() {
        return new AuthorityUserPKRecord(
                this.userEntity.id(),
                this.authorityLevel.id()
        );
    }

    public UserEntity getUserEntity() {
        return this.userEntity;
    }
}
