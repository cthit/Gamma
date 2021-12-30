package it.chalmers.gamma.app.client.domain;

public record RedirectUrl(String value) {

    public RedirectUrl {
        if (value == null) {
            throw new NullPointerException();
        }

        //TODO: add more validation
    }

}
