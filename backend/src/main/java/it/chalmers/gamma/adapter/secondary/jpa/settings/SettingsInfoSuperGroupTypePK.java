package it.chalmers.gamma.adapter.secondary.jpa.settings;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.settings.domain.SettingsId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Embeddable
public class SettingsInfoSuperGroupTypePK extends PKId<SettingsInfoSuperGroupTypePK.SettingsInfoSuperGroupTypePKDTO> {

    @ManyToOne
    @JoinColumn(name = "settings_id")
    private SettingsEntity settings;

    @OneToOne
    @JoinColumn(name = "super_group_type_name")
    private SuperGroupTypeEntity superGroupType;

    protected SettingsInfoSuperGroupTypePK() {}

    protected SettingsInfoSuperGroupTypePK(SettingsEntity settingsEntity, SuperGroupTypeEntity superGroupTypeEntity) {
        this.settings = settingsEntity;
        this.superGroupType = superGroupTypeEntity;
    }

    protected record SettingsInfoSuperGroupTypePKDTO(SettingsId settingsId, SuperGroupType superGroupType) { }

    @Override
    public SettingsInfoSuperGroupTypePKDTO getValue() {
        return new SettingsInfoSuperGroupTypePKDTO(
                settings.domainId(),
                superGroupType.get()
        );
    }



}
