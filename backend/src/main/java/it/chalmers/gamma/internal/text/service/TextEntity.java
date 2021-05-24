package it.chalmers.gamma.internal.text.service;

import it.chalmers.gamma.domain.TextId;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "internal_text")
public class TextEntity extends MutableEntity<TextId, TextDTO> {

    @EmbeddedId
    private final TextId textId;

    @Column(name = "sv")
    private String sv;

    @Column(name = "en")
    private String en;

    public TextEntity() {
        this(new TextId(), null, null);
    }

    public TextEntity(TextId textId, String sv, String en) {
        assert(textId != null);

        this.textId = textId;
        this.sv = sv;
        this.en = en;
    }

    public TextEntity(TextDTO text) {
        this(new TextId(), text.sv(), text.en());
    }

    @Override
    public TextDTO toDTO() {
        return new TextDTO(
                this.sv,
                this.en
        );
    }

    @Override
    protected TextId id() {
        return this.textId;
    }

    @Override
    public void apply(TextDTO newText) {
        this.sv = newText.sv();
        this.en = newText.en();
    }
}
