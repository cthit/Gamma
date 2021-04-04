package it.chalmers.gamma.domain.whitelist.controller.request;

import it.chalmers.gamma.util.domain.Cid;

import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AddListOfWhitelistedRequest {

    @NotEmpty(message = "NO_CID_IN_REQUEST")
    @Valid
    public List<Cid> cids;

}
