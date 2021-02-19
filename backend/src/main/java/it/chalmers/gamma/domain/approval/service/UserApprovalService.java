package it.chalmers.gamma.domain.approval.service;

import it.chalmers.gamma.domain.approval.data.UserApprovalDTO;
import it.chalmers.gamma.domain.approval.data.UserApproval;
import it.chalmers.gamma.domain.approval.data.UserApprovalPK;
import it.chalmers.gamma.domain.approval.data.UserApprovalRepository;
import it.chalmers.gamma.domain.approval.exception.UserApprovalNotFoundException;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import it.chalmers.gamma.domain.client.exception.ClientNotFoundException;
import it.chalmers.gamma.domain.client.service.ClientFinder;
import it.chalmers.gamma.domain.Cid;

import java.util.*;
import java.util.stream.Collectors;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Service;

@Service()
public class UserApprovalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserApprovalService.class);

    private final UserApprovalRepository userApprovalRepository;
    private final UserFinder userFinder;

    public UserApprovalService(UserApprovalRepository userApprovalRepository, UserFinder userFinder) {
        this.userApprovalRepository = userApprovalRepository;
        this.userFinder = userFinder;
    }

    public void saveApproval(UserId userId, String clientId) {
        this.userApprovalRepository.save(new UserApproval(new UserApprovalPK(userId, clientId)));
    }

    public void saveApproval(Approval approval) throws UserNotFoundException, ClientNotFoundException {
        String cid = approval.getUserId();
        UserDTO user = this.userFinder.getUser(new Cid(cid));

        String clientId = approval.getClientId();

        saveApproval(user.getId(), clientId);
    }

}
