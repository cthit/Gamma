package it.chalmers.gamma.internal.text.service;

import it.chalmers.gamma.domain.Text;
import it.chalmers.gamma.domain.TextId;
import it.chalmers.gamma.domain.TextValue;
import it.chalmers.gamma.util.entity.MutableEntity;

import javax.persistence.*;

@Entity
@Table(name = "internal_text")
public class TextEntity extends MutableEntity<TextId, Text> {

    @EmbeddedId
    private final TextId textId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "sv"))
    private TextValue sv;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "en"))
    private TextValue en;

    public TextEntity() {
        this(TextId.generate(), null, null);
    }

    public TextEntity(TextId textId, TextValue sv, TextValue en) {
        assert(textId != null);

        this.textId = textId;
        this.sv = sv;
        this.en = en;
    }

    public TextEntity(Text text) {
        this(TextId.generate(), text.sv(), text.en());
    }

    @Override
    public Text toDTO() {
        return new Text(
                this.sv,
                this.en
        );
    }

    @Override
    protected TextId id() {
        return this.textId;
    }

    @Override
    public void apply(Text newText) {
        this.sv = newText.sv();
        this.en = newText.en();
    }
}
