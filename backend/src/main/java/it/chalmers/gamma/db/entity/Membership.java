package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import it.chalmers.gamma.db.entity.pk.MembershipPK;

import java.time.Year;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "membership")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Membership {

    @EmbeddedId
    private MembershipPK id;

    @ManyToOne
    private Post post;

    @Column(name = "unofficial_post_name", length = 100)
    private String unofficialPostName;

    @Column(name = "year")
    private int year;

    public Year getYear() {
        return Year.of(this.year);
    }

    public void setYear(Year year) {
        this.year = year.getValue();
    }

    public MembershipPK getId() {
        return this.id;
    }

    public void setId(MembershipPK id) {
        this.id = id;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public void setUnofficialPostName(String unofficialPostName) {
        this.unofficialPostName = unofficialPostName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Membership that = (Membership) o;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.post, that.post)
            && Objects.equals(this.unofficialPostName, that.unofficialPostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.post, this.unofficialPostName);
    }

    @Override
    public String toString() {
        return "Membership{"
            + "id=" + this.id
            + ", post=" + this.post
            + ", unofficialPostName='" + this.unofficialPostName + '\''
            + '}';
    }
}
