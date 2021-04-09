package it.chalmers.gamma.domain.client.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import java.util.Objects;

public class ClientDTO implements DTO {

    private final ClientId clientId;
    private final ClientSecret clientSecret;
    private final String webServerRedirectUri;
    private final boolean autoApprove;
    private final String name;
    private final TextDTO description;

    public ClientDTO(ClientId clientId,
                     ClientSecret clientSecret,
                     String webServerRedirectUri,
                     boolean autoApprove,
                     String name,
                     TextDTO description) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webServerRedirectUri = webServerRedirectUri;
        this.autoApprove = autoApprove;
        this.name = name;
        this.description = description;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public ClientSecret getClientSecret() {
        return clientSecret;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public boolean isAutoApprove() {
        return autoApprove;
    }

    public String getName() {
        return name;
    }

    public TextDTO getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO clientDTO = (ClientDTO) o;
        return autoApprove == clientDTO.autoApprove && Objects.equals(clientId, clientDTO.clientId) && Objects.equals(clientSecret, clientDTO.clientSecret) && Objects.equals(webServerRedirectUri, clientDTO.webServerRedirectUri) && Objects.equals(name, clientDTO.name) && Objects.equals(description, clientDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientSecret, webServerRedirectUri, autoApprove, name, description);
    }
}
