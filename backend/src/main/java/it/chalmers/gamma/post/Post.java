package it.chalmers.gamma.post;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.post.PostDTO;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @Column(updatable = false)
    private UUID id;

    @JoinColumn(name = "post_name")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Text postName;

    @Column(name = "email_prefix")
    private String emailPrefix;

    public Post() {
        this.postName = new Text();
        this.emailPrefix = "";
    }

    public Post(Text postName, String emailPrefix) {
        this.postName = postName;
        this.id = UUID.randomUUID();
        this.emailPrefix = emailPrefix;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonProperty("sv")
    public void setSVPostName(String postName) {
        this.postName.setSv(postName);
    }

    @JsonProperty("en")
    public void setENPostName(String postName) {
        this.postName.setEn(postName);
    }

    public String getSVPostName() {
        return this.postName.getSv();
    }

    public String getENPostName() {
        return this.postName.getEn();
    }

    public String getEmailPrefix() {
        return this.emailPrefix;
    }

    public void setEmailPrefix(String emailPrefix) {
        this.emailPrefix = emailPrefix == null ? null : emailPrefix.toLowerCase();
    }

    public PostDTO toDTO() {
        return new PostDTO(this.id, this.postName, this.emailPrefix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(this.id, post.id)
            && Objects.equals(this.postName, post.postName)
            && Objects.equals(this.emailPrefix, post.emailPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.postName, this.emailPrefix);
    }

    @Override
    public String toString() {
        return "Post{"
            + "id=" + this.id
            + ", postName=" + this.postName
            + ", emailPrefix='" + this.emailPrefix + '\''
            + '}';
    }
}
