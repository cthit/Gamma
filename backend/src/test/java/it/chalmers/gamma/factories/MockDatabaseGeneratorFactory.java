package it.chalmers.gamma.factories;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.PostService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockDatabaseGeneratorFactory {
    @Autowired
    private ITUserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private FKITGroupService groupService;
    @Autowired
    private FKITSuperGroupService superGroupService;
    private ITUserDTO user;
    private PostDTO post;
    private FKITGroupDTO group;
    private FKITSuperGroupDTO superGroup;

    public void generateNewMock() { ;
        ITUserDTO user = RandomITUserFactory.generateITUser("user");
        this.user = this.userService.createUser(user.getNick(), user.getFirstName(), user.getLastName(), user.getCid(),
                user.getAcceptanceYear(), user.isUserAgreement(), user.getEmail(), "password");
        this.post = this.postService.addPost(new Text());
        this.group = this.groupService.createGroup(RandomFKITGroupFactory.generateActiveFKITGroup("group"));
        this.superGroup =
                this.superGroupService.createSuperGroup(RandomSuperGroupFactory.generateSuperGroup("supergroup"));
    }

    public ITUserDTO getMockedUser() {
        return this.user;
    }
    // Make this prettier by making a list and looping
    public UUID getMockedUUID(Class c) {
        if (user.getClass() == c) {
            return user.getId();
        }
        if (post.getClass() == c) {
            return post.getId();
        }
        if (group.getClass() == c) {
            return group.getId();
        }
        if (superGroup.getClass() == c) {
            return superGroup.getId();
        }
        return null;
    }
}
