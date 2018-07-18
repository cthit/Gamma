package it.chalmers.gamma.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "text")
public class Text {
    @Id
    @Column(updatable = false)
    private UUID id;
    @Column(name = "sv")
    private String sv;
    @Column(name = "sv")
    private String en;

    public Text(String sv, String en) {
        this.sv = sv;
        this.en = en;
        id = UUID.randomUUID();
    }

    public Text(String sv) {
        this.sv = sv;
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getSv() {

        return sv;
    }

    public void setSv(String sv) {
        this.sv = sv;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text = (Text) o;
        return Objects.equals(id, text.id) &&
                Objects.equals(sv, text.sv) &&
                Objects.equals(en, text.en);
    }

    @Override
    public String toString() {
        return "Text{" +
                "id=" + id +
                ", sv='" + sv + '\'' +
                ", en='" + en + '\'' +
                '}';
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, sv, en);
    }
}
