package it.chalmers.gamma.app.client.domain;

public record WebServerRedirectUrl(String value) {

    public WebServerRedirectUrl {
        if (value == null) {
            throw new NullPointerException();
        }

        //TODO: add more validation
    }

}
