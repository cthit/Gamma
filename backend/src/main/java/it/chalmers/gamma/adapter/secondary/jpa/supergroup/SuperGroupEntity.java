package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.UUID;


@Entity
@Table(name = "fkit_super_group")
public class SuperGroupEntity extends MutableEntity<UUID> {

    @Id
    @Column(name = "super_group_id")
    protected UUID id;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.ALL)
    protected TextEntity description;

    @Column(name = "e_name")
    protected String name;

    @Column(name = "pretty_name")
    protected String prettyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "super_group_type_name")
    protected SuperGroupTypeEntity superGroupType;

    protected SuperGroupEntity() {}

    protected SuperGroupEntity(UUID superGroupId) {
        this.id = superGroupId;
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
