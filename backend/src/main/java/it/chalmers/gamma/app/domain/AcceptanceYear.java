package it.chalmers.gamma.app.domain;

public record AcceptanceYear(int value) {

    public AcceptanceYear {
        if (value < 2001) {
            throw new IllegalArgumentException("Acceptance year cannot be less than 2001");
        }
    }

}
