package it.chalmers.gamma.domain.text.data.dto;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import javax.validation.constraints.NotEmpty;

public class TextDTO implements DTO {

    private String sv;
    private String en;

    protected TextDTO() {}

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
