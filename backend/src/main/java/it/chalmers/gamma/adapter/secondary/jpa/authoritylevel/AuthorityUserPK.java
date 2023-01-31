package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.user.domain.UserId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class AuthorityUserPK extends PKId<AuthorityUserPK.AuthorityUserPKRecord> {

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

    protected record AuthorityUserPKRecord(UserId userId, AuthorityLevelName authorityLevelName) {
    }
}
