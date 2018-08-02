package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.chalmers.gamma.domain.GroupType;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "fkit_group")
public class FKITGroup {

    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "pretty_name", length = 50, nullable = false)
    private String prettyName;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    @JoinColumn(name = "function", nullable = false)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text func;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "type", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupType type;

    public FKITGroup() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public Text getFunc() {
        return func;
    }

    public void setFunc(Text func) {
        this.func = func;
    }
    @JsonIgnore
    public String getSVFunction() {
        return func.getSv();
    }

    public void setSVFunction(String function) {
        this.func.setSv(function);
    }
    @JsonIgnore
    public String getENFunction() {
        return func.getEn();
    }

    public void setENFunction(String function) {
        this.func.setEn(function);
    }
    public String getPrettyName() {
        return prettyName;
    }
    @JsonIgnore
    public String getSVDescription() {
        if(description == null){
            return null;
        }
        return description.getSv();
    }

    public void setSVDescription(String description) {
        this.description.setSv(description);
    }
    @JsonIgnore
    public String getENDescription() {
        if(description == null){
            return null;
        }
        return description.getEn();
    }

    public void setENDescription(String description) {
        this.description.setEn(description);
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    @Override
    public String toString() {
        return "FKITGroup{" +
                "id=" + id +
                ", avatarURL='" + avatarURL + '\'' +
                ", name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                ", description=" + description +
                ", func=" + func +
                ", email='" + email + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FKITGroup fkitGroup = (FKITGroup) o;
        return Objects.equals(id, fkitGroup.id) &&
                Objects.equals(avatarURL, fkitGroup.avatarURL) &&
                Objects.equals(name, fkitGroup.name) &&
                Objects.equals(prettyName, fkitGroup.prettyName) &&
                Objects.equals(description, fkitGroup.description) &&
                Objects.equals(func, fkitGroup.func) &&
                Objects.equals(email, fkitGroup.email) &&
                type == fkitGroup.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, avatarURL, name, prettyName, description, func, email, type);
    }

    public FKITGroupView getView(List<String> props) {
        FKITGroupView view = new FKITGroupView();
        view.func = new Text();
        view.description = new Text();
        for (String prop : props) {
            switch (prop) {
                case "id":
                    view.id = this.id;
                    break;
                case "avatarURL":
                    view.avatarURL = this.avatarURL;
                    break;
                case "name":
                    view.name = this.name;
                    break;
                case "prettyName":
                    view.prettyName = this.prettyName;
                    break;
                case "description":
                    view.description = this.description;
                    break;
                case "func":
                    view.func = this.func;
                    break;
                case "email":
                    view.email = this.email;
                    break;
                case "type":
                    view.type = this.type;
                    break;
            }
        }
        return view;
    }
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public class FKITGroupView{

        private UUID id;
        private String avatarURL;
        private String name;
        private String prettyName;
        private Text description;
        private Text func;
        private String email;
        private GroupType type;
        private List<GroupWebsite> websites;
        private List<ITUser> users;

        public Text getDescription() {
            return description;
        }

        public Text getFunc() {
            return func;
        }

        public String getSVDescription() {
            if(description == null){
                return null;
            }
            return description.getSv();
        }

        public void setSVDescription(String description) {
            this.description.setSv(description);
        }

        public String getENDescription() {
            if(description == null){
                return null;
            }
            return description.getEn();
        }

        public void setENDescription(String description) {
            this.description.setEn(description);
        }
        @JsonIgnore
        public String getSVFunction() {
            return func.getSv();
        }

        public void setSVFunction(String function) {
            this.func.setSv(function);
        }
        @JsonIgnore
        public String getENFunction() {
            return func.getEn();
        }

        public void setENFunction(String function) {
            this.func.setEn(function);
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getAvatarURL() {
            return avatarURL;
        }

        public void setAvatarURL(String avatarURL) {
            this.avatarURL = avatarURL;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrettyName() {
            return prettyName;
        }

        public void setPrettyName(String prettyName) {
            this.prettyName = prettyName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public GroupType getType() {
            return type;
        }

        public void setType(GroupType type) {
            this.type = type;
        }
        @JsonIgnore
        public List<GroupWebsite> getWebsites() {
            return websites;
        }
        @JsonProperty("websites")
        public List<Website.WebsiteView> getWebsitesOrdered(){     // TODO CHECK IF THIS WORKS.
            String[] properties = {"id", "name", "prettyName"};
            List<String> props = new ArrayList<>(Arrays.asList(properties));
            List<Website> websiteTypes = new ArrayList<>();
            List<Website.WebsiteView> groupedWebsites = new ArrayList<>();

            for(GroupWebsite website : websites){       //loops through all websites added to group.
                boolean websiteFound = false;
                for(int y = 0; y < websiteTypes.size(); y++){   //loops through all added website types.
                    if(websiteTypes.get(y).equals(website.getWebsite().getWebsite())){  // checks if the website has been added to found types.
                        groupedWebsites.get(y).getUrl().add(website.getWebsite().getUrl()); // if website has been found before the url is added to a list of websites connected to that.
                        websiteFound = true;
                    }
                }
                if(!websiteFound) {
                    websiteTypes.add(website.getWebsite().getWebsite());    // if the websitetype is not found, it is added.
                    Website.WebsiteView newGroup = website.getWebsite().getWebsite().getView(props);
                    newGroup.setUrl(new ArrayList<>());
                    newGroup.getUrl().add(website.getWebsite().getUrl());
                    groupedWebsites.add(newGroup);
                }
            }
            return groupedWebsites;
        }

        public void setWebsites(List<GroupWebsite> websites) {
            this.websites = websites;
        }
        public void setUsers(List<ITUser> users){
            this.users = users;
        }
        public List<ITUser> getUsers(){
            return users;
        }
    }

}
