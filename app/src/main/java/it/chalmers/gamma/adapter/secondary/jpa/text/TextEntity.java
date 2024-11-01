package it.chalmers.gamma.adapter.secondary.jpa.text;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.common.TextId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "g_text")
public class TextEntity extends ImmutableEntity<TextId> {

  @Id
  @Column(name = "text_id", columnDefinition = "uuid")
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
    return new Text(this.sv, this.en);
  }

  @Override
  public TextId getId() {
    return new TextId(this.id);
  }

  public void apply(Text newText) {
    this.sv = newText.sv().value();
    this.en = newText.en().value();
  }
}
