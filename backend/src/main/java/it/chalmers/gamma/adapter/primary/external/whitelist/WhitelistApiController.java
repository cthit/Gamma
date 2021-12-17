package it.chalmers.gamma.adapter.primary.external.whitelist;

import it.chalmers.gamma.app.facade.internal.WhitelistFacade;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(WhitelistApiController.URI)
public class WhitelistApiController {

    public static final String URI = "/external/whitelist";

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistApiController.class);

    private final WhitelistFacade whitelistFacade;

    public WhitelistApiController(WhitelistFacade whitelistFacade) {
        this.whitelistFacade = whitelistFacade;
    }

    @GetMapping()
    public List<String> getWhiteList() {
        return this.whitelistFacade.getWhitelist();
    }

    private record AddListOfWhitelistedRequest(List<String> cids) { }

    @PostMapping()
    public ResponseEntity<?> addWhitelistedUsers(@RequestBody AddListOfWhitelistedRequest request) {
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

    private static class WhitelistAddedResponse extends SuccessResponse { }

}
