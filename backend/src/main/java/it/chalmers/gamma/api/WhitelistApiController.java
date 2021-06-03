package it.chalmers.gamma.api;

import it.chalmers.gamma.internal.whitelist.service.WhitelistService;
import it.chalmers.gamma.domain.Cid;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/whitelist")
public class WhitelistApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistApiController.class);

    private final WhitelistService whitelistService;

    public WhitelistApiController(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    @GetMapping()
    public List<Cid> getWhiteList() {
        return this.whitelistService.getAll();
    }

    private record AddListOfWhitelistedRequest(List<Cid> cids) { }

    @PostMapping()
    public ResponseEntity<?> addWhitelistedUsers(@Valid @RequestBody AddListOfWhitelistedRequest request) {
        List<Cid> failedToAdd = new ArrayList<>();

        for (Cid cid : request.cids) {
            try {
                this.whitelistService.create(cid);
                LOGGER.info("Added user " + cid + " to whitelist");
            } catch (WhitelistService.WhitelistNotFoundException e) {
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
