package it.chalmers.gamma.adapter.primary.api.v2;

import it.chalmers.gamma.app.user.allowlist.AllowListFacade;
import it.chalmers.gamma.app.user.allowlist.AllowListRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/allowlist")
public class AllowListV2Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(AllowListV2Controller.class);

  private final AllowListFacade allowListFacade;

  public AllowListV2Controller(AllowListFacade allowListFacade) {
    this.allowListFacade = allowListFacade;
  }

  @PostMapping
  public ResponseEntity<?> addAllowedUsers(@RequestBody AddListOfAllowListRequest request) {
    List<String> failedToAdd = new ArrayList<>();

    for (String cid : request.cids) {
      try {
        this.allowListFacade.allowV2(cid);
        LOGGER.info("Added user " + cid + " to allow list");
      } catch (Exception e) {
        LOGGER.info("Failed to add " + cid + " to allow list");
        failedToAdd.add(cid);
      }
    }

    if (!failedToAdd.isEmpty()) {
      return new ResponseEntity<>(failedToAdd, HttpStatus.PARTIAL_CONTENT);
    }

    return ResponseEntity.ok().build();
  }

  private record AddListOfAllowListRequest(List<String> cids) {}
}
