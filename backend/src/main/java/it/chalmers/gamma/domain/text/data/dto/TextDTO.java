package it.chalmers.gamma.domain.text.data.dto;

import it.chalmers.gamma.domain.DTO;

public class TextDTO implements DTO {

    private final String sv;
    private final String en;

    public TextDTO(String sv, String en) {
        this.sv = sv;
        this.en = en;
    }

    public String getSv() {
        return sv;
    }

    public String getEn() {
        return en;
    }
}
