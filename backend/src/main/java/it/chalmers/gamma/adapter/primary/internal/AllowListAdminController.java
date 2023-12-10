package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.allowlist.AllowListFacade;
import it.chalmers.gamma.app.user.allowlist.AllowListRepository;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/users/allow-list")
public final class AllowListAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllowListAdminController.class);

    private final AllowListFacade allowListFacade;

    public AllowListAdminController(AllowListFacade allowListFacade) {
        this.allowListFacade = allowListFacade;
    }

    @GetMapping("/{cid}/activated")
    public CidIsAllowListResponse getAllowed(@PathVariable("cid") String cid) {
        if (!this.allowListFacade.isAllowed(cid)) {
            throw new CidNotAllowedResponse();
        }
        return new CidIsAllowListResponse();
    }

    @GetMapping()
    public List<String> getAllowList() {
        return this.allowListFacade.getAllowList();
    }

    @PostMapping()
    public AllowAddedResponse addAllowedUser(@RequestBody AddToAllowList request) {
        try {
            this.allowListFacade.allow(request.cid);
        } catch (AllowListRepository.AlreadyAllowedException e) {
            throw new CidAlreadyAllowedResponse();
        }

        return new AllowAddedResponse();
    }

    @DeleteMapping("/{cid}")
    public CidRemovedFromAllowListResponse removeFromAllowList(@PathVariable("cid") String cid) {
        try {
            this.allowListFacade.removeFromAllowList(cid);
        } catch (AllowListRepository.NotOnAllowListException e) {
            throw new CidNotAllowedResponse();
        }
        return new CidRemovedFromAllowListResponse();
    }

    private record AddToAllowList(String cid) {
    }

    private static class CidRemovedFromAllowListResponse extends SuccessResponse {
    }

    private static class AllowAddedResponse extends SuccessResponse {
    }

    private static class CidNotAllowedResponse extends NotFoundResponse {
    }

    private static class CidAlreadyAllowedResponse extends ErrorResponse {
        private CidAlreadyAllowedResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static class CidIsAllowListResponse extends SuccessResponse {
    }

}
