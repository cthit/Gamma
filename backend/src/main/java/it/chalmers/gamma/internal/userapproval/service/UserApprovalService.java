package it.chalmers.gamma.internal.userapproval.service;

import it.chalmers.gamma.internal.user.service.UserFinder;
import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityHasUsagesException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service()
public class UserApprovalService implements CreateEntity<UserApprovalDTO>, DeleteEntity<UserApprovalPK> {

    private final UserApprovalRepository userApprovalRepository;

    public UserApprovalService(UserApprovalRepository userApprovalRepository, UserFinder userFinder) {
        this.userApprovalRepository = userApprovalRepository;
    }

    public void create(UserApprovalDTO userApproval) {
        this.userApprovalRepository.save(new UserApproval(userApproval));
    }

    @Override
    public void delete(UserApprovalPK id) throws EntityNotFoundException, EntityHasUsagesException {
        this.userApprovalRepository.deleteById(id);
    }
}
