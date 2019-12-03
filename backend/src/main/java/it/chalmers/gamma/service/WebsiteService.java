package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.repository.WebsiteRepository;

import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WebsiteService {

    private final WebsiteRepository repository;

    public WebsiteService(WebsiteRepository repository) {
        this.repository = repository;
    }

    /**
     * adds a possible website to the database.
     *
     * @param name       the name of the website
     * @param prettyName the display-name of the website
     */
    public void addPossibleWebsite(String name, String prettyName) {
        Website website = new Website();
        website.setPrettyName(prettyName == null ? name.toLowerCase() : prettyName);
        website.setName(name);
        this.repository.save(website);
    }

    public WebsiteDTO getWebsite(String websiteName) {
        return this.repository.findByName(websiteName).map(Website::toDTO).orElse(null);
    }

    public void editWebsite(WebsiteDTO websiteDTO, String name, String prettyName) {
        Website website = this.getWebsite(websiteDTO);
        website.setName(name.toLowerCase());
        website.setPrettyName(prettyName == null ? name.toLowerCase() : prettyName);
        this.repository.save(website);
    }

    public void deleteWebsite(String id) {
        this.repository.deleteById(UUID.fromString(id));
    }

    public WebsiteDTO getWebsiteById(String id) {
        return this.repository.findById(UUID.fromString(id)).map(Website::toDTO).orElse(null);
    }

    public boolean websiteExists(UUID id) {
        return this.repository.existsById(id);
    }

    public List<WebsiteDTO> getAllWebsites() {
        return this.repository.findAll().stream().map(Website::toDTO).collect(Collectors.toList());
    }

    protected Website getWebsite(WebsiteDTO websiteDTO) {
        return this.repository.findById(websiteDTO.getId()).orElse(null);
    }

}
