package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Calendar;
import java.util.GregorianCalendar;
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


    @Column(name = "becomes_active")
    private Calendar becomesActive;

    @Column(name = "becomes_inactive")
    private Calendar becomesInactive;

    public FKITGroup() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return this.description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public String getAvatarURL() {
        return this.avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public Text getFunc() {
        return this.func;
    }

    public void setFunc(Text func) {
        this.func = func;
    }

    @JsonIgnore
    public String getSVFunction() {
        return this.func.getSv();
    }

    public void setSVFunction(String function) {
        this.func.setSv(function);
    }

    @JsonIgnore
    public String getENFunction() {
        return this.func.getEn();
    }

    public void setENFunction(String function) {
        this.func.setEn(function);
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    @JsonIgnore
    public String getSVDescription() {
        if (this.description == null) {
            return null;
        }
        return this.description.getSv();
    }

    public void setSVDescription(String description) {
        this.description.setSv(description);
    }

    @JsonIgnore
    public String getENDescription() {
        if (this.description == null) {
            return null;
        }
        return this.description.getEn();
    }

    public String email;

    public void setENDescription(String description) {
        this.description.setEn(description);
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public Calendar getBecomesActive() {
        return this.becomesActive;
    }

    public void setBecomesActive(Calendar becomesActive) {
        this.becomesActive = becomesActive;
    }

    public Calendar getBecomesInactive() {
        return this.becomesInactive;
    }

    public void setBecomesInactive(Calendar becomesInactive) {
        this.becomesInactive = becomesInactive;
    }

    public boolean isActive() {
        Calendar now = new GregorianCalendar();
        return now.after(this.becomesActive) && now.before(this.becomesInactive);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String
    toString() {
        return "FKITGroup{" +
                "id=" + id +
                ", avatarURL='" + avatarURL + '\'' +
                ", name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                ", description=" + description +
                ", func=" + func +
                ", becomesActive=" + becomesActive +
                ", becomesInactive=" + becomesInactive +
                ", email='" + email + '\'' +
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
                Objects.equals(becomesActive, fkitGroup.becomesActive) &&
                Objects.equals(becomesInactive, fkitGroup.becomesInactive) &&
                Objects.equals(email, fkitGroup.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, avatarURL, name, prettyName, description, func, becomesActive, becomesInactive, email);
    }
}