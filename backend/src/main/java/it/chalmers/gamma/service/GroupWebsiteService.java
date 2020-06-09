package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.GroupWebsite;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.db.repository.GroupWebsiteRepository;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;

import it.chalmers.gamma.domain.dto.website.GroupWebsiteDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteInterfaceDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteUrlDTO;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/*
 * @ł€®®þþ←↓→œþªßðđŋħ̡ĸłøæ«»©“”nµ̣ΩŁ¢®Þ¥↑↑ıŒÞ§ÐªŊĦ̛&ŁØ<>©‘’Nº
 * Type this in the url for a secret.... If I don't forget.... in that case f me. right
 * Nobody will ever know this is here cause no-one will ever look at this code.
 */

@Service
public class GroupWebsiteService extends EntityWebsiteService {

    private final GroupWebsiteRepository repository;
    private final FKITGroupService fkitGroupService;
    private final WebsiteURLService websiteURLService;
    private final WebsiteService websiteService;

    public GroupWebsiteService(GroupWebsiteRepository repository,
                               WebsiteService websiteService, FKITGroupService fkitGroupService,
                               WebsiteURLService websiteURLService, WebsiteService websiteService1) {
        super(websiteService);
        this.repository = repository;
        this.fkitGroupService = fkitGroupService;
        this.websiteURLService = websiteURLService;
        this.websiteService = websiteService1;
    }

    public void addGroupWebsites(FKITGroupDTO groupDTO, List<WebsiteUrlDTO> websiteUrlDTOS) {
        if (websiteUrlDTOS == null || groupDTO == null) {
            return;
        }
        boolean error = false;
        for (WebsiteUrlDTO websiteUrlDTO : websiteUrlDTOS) {
            if (websiteUrlDTO.getWebsiteDTO() == null || websiteUrlDTO.getUrl() == null) {
                error = true;
                continue;
            }
            FKITGroup group = this.fkitGroupService.fromDTO(groupDTO);
            WebsiteURL websiteURL = this.websiteURLService.getWebsiteURL(websiteUrlDTO);
            GroupWebsite groupWebsite = new GroupWebsite();
            groupWebsite.setGroup(group);
            groupWebsite.setWebsite(websiteURL);
            this.repository.save(groupWebsite);
        }
        if (error) {
            throw new DataIntegrityViolationException("A SQL Constraint was violated");
        }
    }

    @Transactional
    public void deleteGroupWebsiteByWebsite(WebsiteDTO websiteDTO) {
        Website website = this.websiteService.getWebsite(websiteDTO);
        this.repository.deleteAllByWebsite_Website(website);
    }

    public List<GroupWebsiteDTO> getAllGroupWebsites() {
        return this.repository.findAll().stream().map(GroupWebsite::toDTO).collect(Collectors.toList());
    }

    public GroupWebsiteDTO getGroupWebsiteById(String id) {
        return this.repository.findById(UUID.fromString(id)).map(GroupWebsite::toDTO).orElse(null);
    }

    public void deleteGroupWebsite(String id) {

    }

    public List<WebsiteInterfaceDTO> getWebsites(FKITGroupDTO group) {
        return this.repository.findAllByGroup(this.fkitGroupService.fromDTO(group)).stream()
                .map(GroupWebsite::toDTO).collect(Collectors.toList());
    }

    public GroupWebsiteDTO getGroupWebsiteByWebsite(WebsiteDTO websiteDTO) {
        return this.repository.findByWebsite_Website(this.websiteService.getWebsite(websiteDTO)).toDTO();
    }

    @Transactional
    public void deleteWebsitesConnectedToGroup(FKITGroupDTO group) {
        this.repository.deleteAllByGroup(this.fkitGroupService.fromDTO(group));
    }

    protected GroupWebsiteDTO getGroupWebsite(GroupWebsiteDTO websiteDTO) {
        return this.repository.findById(websiteDTO.getId()).map(GroupWebsite::toDTO).orElse(null);
    }

}
