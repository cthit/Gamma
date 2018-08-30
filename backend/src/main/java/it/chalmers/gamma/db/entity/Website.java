package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "website")
public class Website {
    @Id
    @Column(updatable = false)
    private UUID id;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "pretty_name")
    private String prettyName;

    public Website() {
        id = UUID.randomUUID();
    }

    public Website(String name){
        this.name = name;
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

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Website website = (Website) o;
        return Objects.equals(id, website.id) &&
                Objects.equals(name, website.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Website{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    public WebsiteView getView(List<String> websites){
        WebsiteView view = new WebsiteView();
        for(String website : websites) {
            switch (website){
                case "id":
                    view.setId(this.id);
                    break;
                case "name":
                    view.setName(this.name);
                    break;
                case "prettyName":
                    view.setPrettyName(this.prettyName);
                    break;
            }
        }
        return view;
    }
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public class WebsiteView{
        private UUID id;
        private String name;
        private String prettyName;
        private List<String> url;

        private WebsiteView(){

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

        public String getPrettyName() {
            return prettyName;
        }

        public void setPrettyName(String prettyName) {
            this.prettyName = prettyName;
        }

        public List<String> getUrl() {
            return url;
        }

        public void setUrl(List<String> url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "WebsiteView{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", prettyName='" + prettyName + '\'' +
                    ", url=" + url +
                    '}';
        }
    }
}
