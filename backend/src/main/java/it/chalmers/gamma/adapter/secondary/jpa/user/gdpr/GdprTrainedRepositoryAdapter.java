package it.chalmers.gamma.adapter.secondary.jpa.user.gdpr;

import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.gdpr.GdprTrainedRepository;
import org.springframework.stereotype.Repository;

@Repository
public class GdprTrainedRepositoryAdapter implements GdprTrainedRepository {

    private final GdprTrainedJpaRepository gdprTrainedJpaRepository;

    public GdprTrainedRepositoryAdapter(GdprTrainedJpaRepository gdprTrainedJpaRepository) {
        this.gdprTrainedJpaRepository = gdprTrainedJpaRepository;
    }

    @Override
    public void setGdprTrainedStatus(UserId userId, boolean gdprTrained) {
        throw new UnsupportedOperationException();
    }
}
