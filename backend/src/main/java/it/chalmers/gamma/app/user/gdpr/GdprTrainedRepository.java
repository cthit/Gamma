package it.chalmers.gamma.app.user.gdpr;

import it.chalmers.gamma.app.user.domain.UserId;

import java.util.List;

public interface GdprTrainedRepository {

    void setGdprTrainedStatus(UserId userId, boolean gdprTrained);
    List<UserId> getAll();


}
