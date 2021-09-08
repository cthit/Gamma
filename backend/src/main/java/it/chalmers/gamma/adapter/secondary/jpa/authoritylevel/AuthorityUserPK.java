package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthorityUserPK extends Id<AuthorityUserPK.AuthorityUserPKRecord> {

    protected record AuthorityUserPKRecord(User user, AuthorityLevelName authorityLevelName) { }

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity userEntity;

    @Column(name = "authority_level")
    private String authorityLevel;

    protected AuthorityUserPK() {

    }

    public AuthorityUserPK(UserEntity userEntity, String authorityLevel) {
        this.userEntity = userEntity;
        this.authorityLevel = authorityLevel;
    }

    @Override
    public AuthorityUserPKRecord value() {
        return new AuthorityUserPKRecord(
                this.userEntity.toDomain(),
                AuthorityLevelName.valueOf(this.authorityLevel)
        );
    }
}
