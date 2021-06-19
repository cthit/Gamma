package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.domain.Group;
import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
import it.chalmers.gamma.util.ConstraintErrorUtils;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository repository;
    private final SuperGroupService superGroupService;

    public GroupService(GroupRepository repository,
                        SuperGroupService superGroupService) {
        this.repository = repository;
        this.superGroupService = superGroupService;
    }

    public void create(GroupShallowDTO group) throws GroupAlreadyExistsException {
        try {
            this.repository.save(new GroupEntity(group));
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println(ConstraintErrorUtils.getConstraint(e));
            throw new GroupAlreadyExistsException();
        }
    }

    public void delete(GroupId id) throws GroupNotFoundException {
        this.repository.deleteById(id);
    }

    public void update(GroupShallowDTO newEdit) throws GroupNotFoundException {
        GroupEntity group = this.getGroupEntity(newEdit.id());
        group.apply(newEdit);
        this.repository.save(group);
    }

    public List<Group> getAll() {
        return this.repository
                .findAll()
                .stream()
                .map(GroupEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<Group> getGroupsBySuperGroup(SuperGroupId superGroupId) {
        return this.repository.findAllBySuperGroupId(superGroupId)
                .stream()
                .map(GroupEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public Group get(GroupId id) throws GroupNotFoundException {
        return fromShallow(getGroupEntity(id).toDTO());
    }

    protected GroupEntity getGroupEntity(GroupId id) throws GroupNotFoundException {
        return this.repository.findById(id)
                .orElseThrow(GroupNotFoundException::new);
    }


    protected Group fromShallow(GroupShallowDTO group) {
        try {
            return new Group(
                    group.id(),
                    group.email(),
                    group.name(),
                    group.prettyName(),
                    superGroupService.get(group.superGroupId())
            );
        } catch (SuperGroupService.SuperGroupNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class GroupNotFoundException extends Exception { }
    public static class GroupAlreadyExistsException extends Exception { }

}
