package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.user.domain.UserId;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthorityUserPK extends PKId<AuthorityUserPK.AuthorityUserPKRecord> {

    protected record AuthorityUserPKRecord(UserId userId, AuthorityLevelName authorityLevelName) { }

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
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
                new UserId(this.userEntity.getId()),
                new AuthorityLevelName(this.authorityLevel.getId())
        );
    }

    public UserEntity getUserEntity() {
        return this.userEntity;
    }
}
