package it.chalmers.gamma.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class BootstrapConfig {

    @Value("${application.standard-admin-account.password}")
    private String password;

    @Value("${application.default-oauth2-client.client-name}")
    private String oauth2ClientName;

    @Value("${application.default-oauth2-client.client-id}")
    private String oauth2ClientId;

    @Value("${application.default-oauth2-client.client-secret}")
    private String oauth2ClientSecret;

    @Value("${application.default-oauth2-client.redirect-uri}")
    private String oauth2ClientRedirectUri;

    @Value("${application.default-oauth2-client.api-key}")
    private String oauth2ClientApiKey;

    @Value("${application.default-oauth2-client.mock-client}")
    private boolean mocking;

    @Value("${application.auth.accessTokenValidityTime}")       // TODO Fix this
    private int accessTokenValidityTime;

    @Value("${application.auth.autoApprove}")
    private boolean autoApprove;

    @Value("${application.auth.refreshTokenValidityTime}")
    private int refreshTokenValidityTime;

    public String getPassword() {
        return this.password;
    }

    public String getOauth2ClientName() {
        return this.oauth2ClientName;
    }

    public String getOauth2ClientId() {
        return this.oauth2ClientId;
    }

    public String getOauth2ClientSecret() {
        return this.oauth2ClientSecret;
    }

    public String getOauth2ClientRedirectUri() {
        return this.oauth2ClientRedirectUri;
    }

    public String getOauth2ClientApiKey() {
        return this.oauth2ClientApiKey;
    }

    public boolean isMocking() {
        return this.mocking;
    }

    public int getAccessTokenValidityTime() {
        return this.accessTokenValidityTime;
    }

    public boolean isAutoApprove() {
        return this.autoApprove;
    }

    public int getRefreshTokenValidityTime() {
        return this.refreshTokenValidityTime;
    }

}
