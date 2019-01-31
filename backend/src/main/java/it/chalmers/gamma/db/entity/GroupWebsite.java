package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fkit_group_website")
public class GroupWebsite implements WebsiteInterface {
    @Id
    @JsonIgnore
    private final UUID id;

    @JoinColumn(name = "website")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private WebsiteURL website;

    @OneToOne
    @JoinColumn(name = "fkit_group")
    @JsonIgnore
    private FKITGroup group;

    public GroupWebsite() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return this.id;
    }

    @Override
    public WebsiteURL getWebsite() {
        return this.website;
    }

    public void setWebsite(WebsiteURL website) {
        this.website = website;
    }

    public FKITGroup getGroup() {
        return this.group;
    }

    public void setGroup(FKITGroup group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupWebsite that = (GroupWebsite) o;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.website, that.website)
            && Objects.equals(this.group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.website, this.group);
    }

    @Override
    public String toString() {
        return "GroupWebsite{"
            + "id=" + this.id
            + ", website=" + this.website
            + ", group=" + this.group
            + '}';
    }

}
