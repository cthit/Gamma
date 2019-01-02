package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.repository.FKITGroupRepository;
import it.chalmers.gamma.requests.CreateGroupRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import it.chalmers.gamma.views.FKITGroupView;
import org.springframework.stereotype.Service;

@Service
public class FKITService {

    private final FKITGroupRepository repo;

    public FKITService(FKITGroupRepository repo) {
        this.repo = repo;
    }

    public FKITGroup createGroup(CreateGroupRequest request, FKITSuperGroup group) {
        FKITGroup fkitGroup = new FKITGroup();
        fkitGroup.setSuperGroup(group);
        fkitGroup.setName(request.getName().toLowerCase());
        fkitGroup.setFunc(request.getFunc());
        fkitGroup.setDescription(request.getDescription());
        fkitGroup.setYear(request.getYear());
        return saveGroup(fkitGroup, request.getPrettyName() == null ? request.getName() : request.getPrettyName(),
                request.getBecomesActive(), request.getBecomesInactive(),
                request.getEmail(), request.getAvatarURL());
    }

    //TODO if no info, don't change value.
    public FKITGroup editGroup(UUID id, CreateGroupRequest request) {
        FKITGroup group = this.repo.findById(id).orElse(null);
        if (group == null) {
            return null;
        }
        group.setYear(request.getYear() == 0 ? group.getYear() : request.getYear());
        group.setSVFunction(request.getFunc() == null ? group.getSVFunction() : request.getFunc().getSv());
        group.setENFunction(request.getFunc() == null ? group.getENFunction() : request.getFunc().getEn());
        if (request.getDescription() != null && group.getDescription() != null) {
            group.setSVDescription(request.getDescription().getSv());
            group.setENDescription(request.getDescription().getEn());
        }
        return saveGroup(group, request.getPrettyName(), request.getBecomesActive(), request.getBecomesInactive(),
                request.getEmail(), request.getAvatarURL());
    }

    private FKITGroup saveGroup(FKITGroup group, String prettyName,
                                Calendar becomesActive, Calendar becomesInactive,
                                String email, String avatarURL) {
        group.setPrettyName(prettyName == null ? group.getPrettyName() : prettyName);
        group.setEmail(email == null ? group.getEmail() : email);
        group.setAvatarURL(avatarURL == null ? group.getAvatarURL() : avatarURL);
        group.setBecomesActive(becomesActive == null ? group.getBecomesActive() : becomesActive);
        group.setBecomesInactive(becomesInactive == null ? group.getBecomesInactive() : becomesInactive);
        return this.repo.save(group);
    }

    public boolean groupExists(String name) {
        return this.repo.existsFKITGroupByName(name);
    }

    public boolean groupExists(UUID id) {
        return this.repo.existsById(id);
    }

    public void removeGroup(String group) {
        this.repo.delete(this.repo.findByName(group));
    }

    public void removeGroup(UUID groupId) {
        this.repo.deleteById(groupId);
    }

    public List<FKITGroup> getGroups() {
        return this.repo.findAll();
    }

    public FKITGroup getGroup(String group) {
        return this.repo.findByName(group);
    }

    public FKITGroup getGroup(UUID id) {
        return this.repo.findById(id).orElse(null);
    }

    public List<FKITGroupView> getGroupsOrdered(){
        List<FKITGroup> groups = this.repo.findAll();
        List<FKITGroupView> views = new ArrayList<>();
        for (FKITGroup group : groups) {
            boolean superGroupFound = false;
            for (FKITGroupView view : views) {
                if (view.getSuperGroup().equals(group.getSuperGroup())) {
                   view.addGroup(group);
                   superGroupFound = true;
                }
            }
            if(!superGroupFound){
                FKITGroupView newView = new FKITGroupView(group.getSuperGroup());
                newView.addGroup(group);
                views.add(newView);
            }
        }
        return views;
    }

}
