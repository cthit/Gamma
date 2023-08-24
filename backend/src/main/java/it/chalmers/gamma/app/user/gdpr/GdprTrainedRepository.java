package it.chalmers.gamma.app.user.gdpr;

import it.chalmers.gamma.app.user.domain.UserId;

public interface GdprTrainedRepository {

    void setGdprTrainedStatus(UserId userId, boolean gdprTrained);

}
