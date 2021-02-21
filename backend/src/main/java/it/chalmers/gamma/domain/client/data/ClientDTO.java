package it.chalmers.gamma.domain.client.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;

public class ClientDTO {

    private final ClientId clientId;
    private final ClientSecret clientSecret;
    private final String webServerRedirectUri;
    private final boolean autoApprove;
    private final String name;
    private final Text description;

    public ClientDTO(ClientId clientId,
                     ClientSecret clientSecret,
                     String webServerRedirectUri,
                     boolean autoApprove,
                     String name,
                     Text description) {
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

    public Text getDescription() {
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
