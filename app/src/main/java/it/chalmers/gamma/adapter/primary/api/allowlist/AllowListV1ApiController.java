package it.chalmers.gamma.adapter.primary.api.allowlist;

import it.chalmers.gamma.adapter.primary.api.utils.SuccessResponse;
import it.chalmers.gamma.app.user.allowlist.AllowListFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(AllowListV1ApiController.URI)
public class AllowListV1ApiController {

    public static final String URI = "/api/allow-list/v1";

    private static final Logger LOGGER = LoggerFactory.getLogger(AllowListV1ApiController.class);

    private final AllowListFacade allowListFacade;

    public AllowListV1ApiController(AllowListFacade allowListFacade) {
        this.allowListFacade = allowListFacade;
    }

    @GetMapping()
    public List<String> getAllowList() {
        return this.allowListFacade.getAllowList();
    }

    @PostMapping()
    public ResponseEntity<?> addAllowedUsers(@RequestBody AddListOfAllowListRequest request)  {
        List<String> failedToAdd = new ArrayList<>();

        for (String cid : request.cids) {
            try {
                this.allowListFacade.allow(cid);
                LOGGER.info("Added user " + cid + " to allow list");
            } catch (Exception e) {
                LOGGER.info("Failed to add " + cid + " to allow list");
                failedToAdd.add(cid);
            }
        }

        if (!failedToAdd.isEmpty()) {
            return new ResponseEntity<>(failedToAdd, HttpStatus.PARTIAL_CONTENT);
        }

        return new AllowListAddedResponse();
    }

    private record AddListOfAllowListRequest(List<String> cids) {
    }

    private static class AllowListAddedResponse extends SuccessResponse {
    }

}
