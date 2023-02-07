package it.chalmers.gamma.adapter.primary.external.whitelist;

import it.chalmers.gamma.app.user.whitelist.WhitelistFacade;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(WhitelistV1ApiController.URI)
public class WhitelistV1ApiController {

    public static final String URI = "/external/whitelist/v1";

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistV1ApiController.class);

    private final WhitelistFacade whitelistFacade;

    public WhitelistV1ApiController(WhitelistFacade whitelistFacade) {
        this.whitelistFacade = whitelistFacade;
    }

    @GetMapping()
    public List<String> getWhiteList() {
        return this.whitelistFacade.getWhitelist();
    }

    @PostMapping()
    public ResponseEntity<?> addWhitelistedUsers(@RequestBody AddListOfWhitelistedRequest request)  {
        List<String> failedToAdd = new ArrayList<>();

        for (String cid : request.cids) {
            try {
                this.whitelistFacade.whitelist(cid);
                LOGGER.info("Added user " + cid + " to whitelist");
            } catch (Exception e) {
                LOGGER.info("Failed to add " + cid + " to whitelist");
                failedToAdd.add(cid);
            }
        }

        if (!failedToAdd.isEmpty()) {
            return new ResponseEntity<>(failedToAdd, HttpStatus.PARTIAL_CONTENT);
        }

        return new WhitelistAddedResponse();
    }

    private record AddListOfWhitelistedRequest(List<String> cids) {
    }

    private static class WhitelistAddedResponse extends SuccessResponse {
    }

}
