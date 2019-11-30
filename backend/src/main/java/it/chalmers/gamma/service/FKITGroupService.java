package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;

import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class FKITGroupService {

    private final FKITGroupRepository repo;

    public FKITGroupService(FKITGroupRepository repo) {
        this.repo = repo;
    }


    public FKITGroupDTO createGroup(FKITGroupDTO fkitGroupDTO) {
        FKITGroup fkitGroup = new FKITGroup();
        fkitGroup.setName(fkitGroupDTO.getName());
        fkitGroup.setFunction(fkitGroupDTO.getFunction());
        fkitGroup.setDescription(fkitGroup.getDescription());
        return saveGroup(fkitGroup, fkitGroup.getPrettyName() == null ? fkitGroupDTO.getName() : fkitGroupDTO.getPrettyName(),
                fkitGroupDTO.getBecomesActive(), fkitGroupDTO.getBecomesInactive(),
                fkitGroupDTO.getEmail(), fkitGroupDTO.getAvatarURL());
    }

    //TODO if no info, don't change value.
    public FKITGroupDTO editGroup(UUID id, FKITGroupDTO fkitGroupDTO) {
        FKITGroup group = this.repo.findById(id).orElse(null);
        if (group == null) {
            return null;
        }
        group.getFunction().setSv(fkitGroupDTO.getFunction() == null ? group.getFunction().getSv() : fkitGroupDTO.getFunction().getSv());
        group.getFunction().setEn(fkitGroupDTO.getFunction() == null ? group.getFunction().getEn() : fkitGroupDTO.getFunction().getEn());
        if (fkitGroupDTO.getDescription() != null && group.getDescription() != null) {
            group.getDescription().setSv(fkitGroupDTO.getDescription().getSv());
            group.getDescription().setEn(fkitGroupDTO.getDescription().getEn());
        }
        return saveGroup(group, fkitGroupDTO.getPrettyName(), fkitGroupDTO.getBecomesActive(), fkitGroupDTO.getBecomesInactive(),
                fkitGroupDTO.getEmail(), fkitGroupDTO.getAvatarURL());
    }

    private FKITGroupDTO saveGroup(FKITGroup group, String prettyName,
                                Calendar becomesActive, Calendar becomesInactive,
                                String email, String avatarURL) {
        group.setPrettyName(prettyName == null ? group.getPrettyName() : prettyName);
        group.setEmail(email == null ? group.getEmail() : email);
        group.setAvatarURL(avatarURL == null ? group.getAvatarURL() : avatarURL);
        group.setBecomesActive(becomesActive == null ? group.getBecomesActive() : becomesActive);
        group.setBecomesInactive(becomesInactive == null ? group.getBecomesInactive() : becomesInactive);
        return this.repo.save(group).toDTO();
    }

    public boolean groupExists(String name) {
        return this.repo.existsFKITGroupByName(name);
    }

    public boolean groupExists(UUID id) {
        return this.repo.existsById(id);
    }

    public void removeGroup(String name) {
        this.repo.deleteByName(name);
    }

    public void removeGroup(UUID groupId) {
        this.repo.deleteById(groupId);
    }

    public List<FKITGroupDTO> getGroups() {
        return this.repo.findAll().stream().map(FKITGroup::toDTO).collect(Collectors.toList());
    }

    public FKITGroupDTO getDTOGroup(String name) {
        return this.repo.findByName(name).map(FKITGroup::toDTO).orElse(null);
    }

    public FKITGroupDTO getDTOGroup(UUID id) {
        return this.repo.findById(id).map(FKITGroup::toDTO).orElse(null);
    }

    public void editGroupAvatar(FKITGroupDTO groupDTO, String url) throws GroupDoesNotExistResponse{
        FKITGroup group = this.repo.findById(groupDTO.getId()).orElseThrow(GroupDoesNotExistResponse::new);
        group.setAvatarURL(url);
        this.repo.save(group);
    }

    public void save(FKITGroup group) {
        this.repo.save(group);
    }

    protected FKITGroup getGroup(FKITGroupDTO group) {
        return this.repo.findById(group.getId()).orElse(null);
    }

}
