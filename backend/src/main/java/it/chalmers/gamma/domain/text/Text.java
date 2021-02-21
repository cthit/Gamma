package it.chalmers.gamma.domain.text;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "internal_text")
public class Text {

    @EmbeddedId
    private final TextId textId;

    @Column(name = "sv")
    private String sv;

    @Column(name = "en")
    private String en;

    public Text() {
        this(null, null);
    }

    public Text(String sv, String en) {
        this.textId = new TextId();
        this.sv = sv;
        this.en = en;
    }

    public TextId getId() {
        return this.textId;
    }

    public String getSv() {
        return this.sv;
    }

    public void setSv(String sv) {
        this.sv = sv;
    }

    public String getEn() {
        return this.en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Text text = (Text) o;
        return Objects.equals(this.textId, text.textId)
                && Objects.equals(this.sv, text.sv)
                && Objects.equals(this.en, text.en);
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
    public int hashCode() {
        return Objects.hash(this.textId, this.sv, this.en);
    }

    public void apply(Text description) {
        this.sv = description.getSv();
        this.en = description.getEn();
    }
}
