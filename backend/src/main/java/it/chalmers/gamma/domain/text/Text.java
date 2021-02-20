package it.chalmers.gamma.domain.text;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "internal_text")
public class Text {

    @Id
    @Column(name = "id")
    @JsonIgnore
    private UUID id;

    @Column(name = "sv")
    private String sv;

    @Column(name = "en")
    private String en;

    public Text() {
        this(null, null);
    }

    public Text(String sv, String en) {
        this.id = UUID.randomUUID();
        this.sv = sv;
        this.en = en;
    }

    public UUID getId() {
        return this.id;
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
        return Objects.equals(this.id, text.id)
                && Objects.equals(this.sv, text.sv)
                && Objects.equals(this.en, text.en);
    }

    @Override
    public String toString() {
        return "Text{"
                + "id=" + this.id
                + ", sv='" + this.sv + '\''
                + ", en='" + this.en + '\''
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.sv, this.en);
    }
}
