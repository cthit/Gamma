package it.chalmers.gamma.adapter.secondary.jpa.text;

import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.TextId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "internal_text")
public class TextEntity extends MutableEntity<TextId> {

    @Id
    @Column(name = "text_id")
    private final UUID id;

    @Column(name = "sv")
    private String sv;

    @Column(name = "en")
    private String en;

    public TextEntity() {
        this(TextId.generate().getValue(), null, null);
    }

    public TextEntity(UUID id, String sv, String en) {
        this.id = id;
        this.sv = sv;
        this.en = en;
    }

    public TextEntity(Text text) {
        this(TextId.generate().getValue(), text.sv().value(), text.en().value());
    }

    public Text toDomain() {
        return new Text(
                this.sv,
                this.en
        );
    }

    @Override
    protected TextId id() {
        return new TextId(this.id);
    }

    public void apply(Text newText) {
        this.sv = newText.sv().value();
        this.en = newText.en().value();
    }
}
