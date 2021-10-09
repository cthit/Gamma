package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.domain.user.Name;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.*;
import java.util.UUID;


@Entity
@Table(name = "fkit_super_group")
public class SuperGroupEntity extends MutableEntity<SuperGroupId> {

    @Id
    @Column(name = "super_group_id")
    protected UUID id;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.MERGE)
    protected TextEntity description;

    @Column(name = "e_name")
    protected String name;

    @Column(name = "pretty_name")
    protected String prettyName;

    @Column(name = "super_group_type_name")
    protected String superGroupType;

    @Column(name = "email")
    protected String email;

    protected SuperGroupEntity() {}

    protected SuperGroupEntity(UUID superGroupId) {
        this.id = superGroupId;
    }

    @Override
    public SuperGroupId id() {
        return new SuperGroupId(this.id);
    }
}
