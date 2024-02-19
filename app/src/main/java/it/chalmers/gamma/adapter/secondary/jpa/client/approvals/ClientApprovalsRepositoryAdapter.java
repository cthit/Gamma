package it.chalmers.gamma.adapter.secondary.jpa.client.approvals;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.approval.ClientApprovalsRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientApprovalsRepositoryAdapter implements ClientApprovalsRepository {

    private final UserApprovalJpaRepository userApprovalJpaRepository;
    private final UserEntityConverter userEntityConverter;

    public ClientApprovalsRepositoryAdapter(UserApprovalJpaRepository userApprovalJpaRepository,
                                            UserEntityConverter userEntityConverter) {
        this.userApprovalJpaRepository = userApprovalJpaRepository;
        this.userEntityConverter = userEntityConverter;
    }

    @Override
    public List<GammaUser> getAllByClientId(ClientId clientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<GammaUser> getAllByClientUid(ClientUid clientUid) {
        List<UserApprovalEntity> userApprovalEntities = this.userApprovalJpaRepository.findAllById_Client_ClientUid(clientUid.value());

        return userApprovalEntities
                .stream()
                .map(UserApprovalEntity::getUserEntity)
                .map(this.userEntityConverter::toDomain)
                .toList();
    }
}
