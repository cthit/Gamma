package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "fkit_group_website")
public class GroupWebsite implements WebsiteInterface {
    @Id
    @JsonIgnore
    private UUID id;

    @JoinColumn(name = "website")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private WebsiteURL website;

    @OneToOne
    @JoinColumn(name = "fkit_group")
    @JsonIgnore
    private FKITGroup group;

    public GroupWebsite() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public WebsiteURL getWebsite() {

        return website;
    }

    public void setWebsite(WebsiteURL website) {
        this.website = website;
    }

    public FKITGroup getGroup() {
        return group;
    }

    public void setGroup(FKITGroup group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupWebsite that = (GroupWebsite) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(website, that.website) &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, website, group);
    }

    @Override
    public String toString() {
        return "GroupWebsite{" +
                "id=" + id +
                ", website=" + website +
                ", group=" + group +
                '}';
    }
    public class GroupWebsiteview{
        private UUID id;
        private WebsiteURL website;
        private FKITGroup group;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public WebsiteURL getWebsite() {
            return website;
        }

        public void setWebsite(WebsiteURL website) {
            this.website = website;
        }

        public FKITGroup getGroup() {
            return group;
        }

        public void setGroup(FKITGroup group) {
            this.group = group;
        }
    }
}
