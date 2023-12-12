package it.chalmers.gamma.adapter.secondary.jpa.user.gdpr;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.gdpr.GdprTrainedRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GdprTrainedRepositoryAdapter implements GdprTrainedRepository {

    private final GdprTrainedJpaRepository gdprTrainedJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public GdprTrainedRepositoryAdapter(GdprTrainedJpaRepository gdprTrainedJpaRepository, UserJpaRepository userJpaRepository) {
        this.gdprTrainedJpaRepository = gdprTrainedJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public void setGdprTrainedStatus(UserId userId, boolean gdprTrained) {
        Optional<GdprTrainedEntity> maybeGdprTrainedEntity = this.gdprTrainedJpaRepository.findById(userId.value());

        if(gdprTrained && maybeGdprTrainedEntity.isEmpty()) {
            this.gdprTrainedJpaRepository.save(new GdprTrainedEntity(userJpaRepository.findById(userId.value()).orElseThrow()));
        } else if(!gdprTrained && maybeGdprTrainedEntity.isPresent()) {
            this.gdprTrainedJpaRepository.deleteById(userId.value());
        }
    }

    @Override
    public List<UserId> getAll() {
        return this.gdprTrainedJpaRepository.findAll().stream().map(gdprTrainedEntity -> new UserId(gdprTrainedEntity.getId())).toList();
    }


}
