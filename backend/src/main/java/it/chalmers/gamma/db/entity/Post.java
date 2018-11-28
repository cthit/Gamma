package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private Text postName;
    public Post() {
        this.postName = new Text();
        this.id = UUID.randomUUID();
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
                && Objects.equals(this.postName, post.postName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.postName);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + this.id
                + ", postName='" + this.postName + '\''
                + '}';
    }
}
