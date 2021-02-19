package it.chalmers.gamma.domain.authoritylevel.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authority_level")
public class AuthorityLevel {

    @Id
    @Column(name = "authority_level")
    private String authorityLevel;

    protected AuthorityLevel() {}

    public AuthorityLevel(String authorityLevel) {
        this.authorityLevel = authorityLevel;
    }

    public String getAuthorityLevel() {
        return authorityLevel;
    }

    public void setAuthorityLevel(String authorityLevel) {
        this.authorityLevel = authorityLevel;
    }
}
