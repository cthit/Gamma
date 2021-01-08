package it.chalmers.gamma.whitelist;

import it.chalmers.gamma.domain.user.WhitelistDTO;
import it.chalmers.gamma.whitelist.response.WhitelistAlreadyAddedException;
import it.chalmers.gamma.whitelist.response.WhitelistDoesNotExistsException;
import it.chalmers.gamma.util.UUIDUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WhitelistService {

    private final WhitelistRepository whitelistRepository;

    public WhitelistService(WhitelistRepository whitelistRepository) {
        this.whitelistRepository = whitelistRepository;
    }

    /**
     * adds a cid that can create a account.
     *
     * @param cid the cid that is added to the whitelisted database
     * @return a copy of the whitelist object that is created
     */
    public WhitelistDTO addWhiteListedCID(String cid) {
        if (this.whitelistRepository.existsByCid(cid)) {
            throw new WhitelistAlreadyAddedException();
        }
        Whitelist whitelistedCID = new Whitelist(cid);
        return this.whitelistRepository.save(whitelistedCID).toDTO();
    }

    @Transactional
    public void removeWhiteListedCID(String id) {
        WhitelistDTO whitelistDTO = this.getWhitelist(id);
        this.whitelistRepository.delete(this.fromDTO(whitelistDTO));
    }

    public Whitelist fromDTO(WhitelistDTO whitelistDTO) {
        return this.whitelistRepository.findById(whitelistDTO.getId())
                .orElseThrow(WhitelistDoesNotExistsException::new);
    }

    /**
     * gets whitelist object by id.
     *
     * @param id the GROUP_ID of the whitelist object to get
     * @return the whitelist object that has corresponding GROUP_ID
     */
    public WhitelistDTO getWhitelist(String id) {
        if (UUIDUtil.validUUID(id)) {
            return this.whitelistRepository.findById(UUID.fromString(id))
                    .orElseThrow(WhitelistDoesNotExistsException::new).toDTO();
        } else {
            return this.whitelistRepository.findByCid(id)
                    .orElseThrow(WhitelistAlreadyAddedException::new).toDTO();
        }
    }

    /**
     * checks if cid has been whitelisted.
     *
     * @param cid the cid check if whitelisted
     * @return true if exists in the database, false otherwise
     */
    public boolean isCIDWhiteListed(String cid) {
        return this.whitelistRepository.existsByCid(cid.toLowerCase())
                || UUIDUtil.validUUID(cid) && this.whitelistRepository.existsById(UUID.fromString(cid));
    }               // Above works because java only checks right if left is correct.

    public List<WhitelistDTO> getAllWhitelist() {
        return this.whitelistRepository.findAll()
                .stream().map(Whitelist::toDTO).collect(Collectors.toList());
    }


    /**
     * edits the cid of a already whitelisted object.
     *
     * @param whitelistDTO the old whitelist object that should be edited
     * @param newCid       the new cid that will replace the old whitelisted cid
     */
    public void editWhitelist(WhitelistDTO whitelistDTO, String newCid) {
        Whitelist whitelist = this.whitelistRepository.findById(whitelistDTO.getId()).orElse(null);
        Objects.requireNonNull(whitelist).setCid(newCid);
        this.whitelistRepository.save(whitelist);
    }


}
