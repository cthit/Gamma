package it.chalmers.gamma.internal.text.data.db;

import it.chalmers.gamma.internal.text.TextId;
import it.chalmers.gamma.internal.text.data.dto.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.*;

@Entity
@Table(name = "internal_text")
public class Text extends MutableEntity<TextId, TextDTO> {

    @EmbeddedId
    private final TextId textId;

    @Column(name = "sv")
    private String sv;

    @Column(name = "en")
    private String en;

    public Text() {
        this(new TextId(), null, null);
    }

    public Text(TextId textId, String sv, String en) {
        assert(textId != null);

        this.textId = textId;
        this.sv = sv;
        this.en = en;
    }

    public Text(TextDTO text) {
        this(new TextId(), text.sv(), text.en());
    }

    @Override
    public String toString() {
        return "Text{"
                + "id=" + this.textId
                + ", sv='" + this.sv + '\''
                + ", en='" + this.en + '\''
                + '}';
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
