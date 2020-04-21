package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;

import java.util.Calendar;
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
    private Text function;

    @Column(name = "becomes_active")
    private Calendar becomesActive;

    @Column(name = "becomes_inactive")
    private Calendar becomesInactive;

    @Column(name = "email")
    private String email;

    @JoinColumn(name = "fkit_super_group")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private FKITSuperGroup superGroup;

    public FKITGroup() {
        this.id = UUID.randomUUID();
    }

    public FKITGroupDTO toDTO() {
        return new FKITGroupDTO(
                this.id,
                this.becomesActive,
                this.becomesInactive,
                this.description,
                this.email,
                this.function,
                this.name,
                this.prettyName,
                this.avatarURL,
                this.superGroup.toDTO());
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
        this.name = name.toLowerCase();
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

    public Text getFunction() {
        return this.function;
    }

    public void setFunction(Text function) {
        this.function = function;
    }

    public String getPrettyName() {
        return this.prettyName;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public FKITSuperGroup getSuperGroup() {
        return this.superGroup;
    }

    public void setSuperGroup(FKITSuperGroup superGroup) {
        this.superGroup = superGroup;
    }

    @Override
    public String toString() {
        return "FKITGroup{"
                + "id=" + this.id
                + ", avatarURL='" + this.avatarURL + '\''
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", description=" + this.description
                + ", func=" + this.function
                + ", becomesActive=" + this.becomesActive
                + ", becomesInactive=" + this.becomesInactive
                + ", email=" + this.email + '\''
                + ", superGroup='" + this.superGroup
                + '}';
    }

}