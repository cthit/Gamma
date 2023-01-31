package it.chalmers.gamma.adapter.secondary.jpa.settings;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.settings.domain.SettingsId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import jakarta.persistence.*;

@Embeddable
public class SettingsInfoSuperGroupTypePK extends PKId<SettingsInfoSuperGroupTypePK.SettingsInfoSuperGroupTypePKDTO> {

    @ManyToOne
    @JoinColumn(name = "settings_id")
    private SettingsEntity settings;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "super_group_type_name")
    private SuperGroupTypeEntity superGroupType;

    protected SettingsInfoSuperGroupTypePK() {
    }

    protected SettingsInfoSuperGroupTypePK(SettingsEntity settingsEntity, SuperGroupTypeEntity superGroupTypeEntity) {
        this.settings = settingsEntity;
        this.superGroupType = superGroupTypeEntity;
    }

    @Override
    public SettingsInfoSuperGroupTypePKDTO getValue() {
        return new SettingsInfoSuperGroupTypePKDTO(
                settings.getId(),
                new SuperGroupType(superGroupType.getId())
        );
    }

    protected record SettingsInfoSuperGroupTypePKDTO(SettingsId settingsId, SuperGroupType superGroupType) {
    }


}
