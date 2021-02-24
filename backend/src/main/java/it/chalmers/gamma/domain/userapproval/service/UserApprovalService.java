package it.chalmers.gamma.domain.userapproval.service;

import it.chalmers.gamma.domain.*;
import it.chalmers.gamma.domain.userapproval.data.db.UserApproval;
import it.chalmers.gamma.domain.userapproval.data.db.UserApprovalPK;
import it.chalmers.gamma.domain.userapproval.data.db.UserApprovalRepository;

import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.userapproval.data.dto.UserApprovalDTO;
import org.springframework.stereotype.Service;

@Service()
public class UserApprovalService implements CreateEntity<UserApprovalDTO>, DeleteEntity<UserApprovalPK> {

    private final UserApprovalRepository userApprovalRepository;
    private final UserFinder userFinder;

    public UserApprovalService(UserApprovalRepository userApprovalRepository, UserFinder userFinder) {
        this.userApprovalRepository = userApprovalRepository;
        this.userFinder = userFinder;
    }

    public void create(UserApprovalDTO userApproval) {
        this.userApprovalRepository.save(new UserApproval(userApproval));
    }

    @Override
    public void delete(UserApprovalPK id) throws EntityNotFoundException, EntityHasUsagesException {
        this.userApprovalRepository.deleteById(id);
    }
}
