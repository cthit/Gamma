package it.chalmers.gamma.app.domain.common;

import java.util.Objects;

public record Text(TextValue sv,
                   TextValue en) {

    public Text {
        Objects.requireNonNull(sv);
        Objects.requireNonNull(en);
    }

    public Text(String sv, String en) {
        this(TextValue.valueOf(sv), TextValue.valueOf(en));
    }

    public Text() {
        this(TextValue.empty(), TextValue.empty());
    }
}