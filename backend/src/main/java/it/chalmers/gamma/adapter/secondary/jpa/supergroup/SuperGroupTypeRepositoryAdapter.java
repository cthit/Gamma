package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.supergroup.SuperGroupTypeRepository;
import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SuperGroupTypeRepositoryAdapter implements SuperGroupTypeRepository {

    @Override
    public void add(SuperGroupType superGroupType) throws SuperGroupTypeAlreadyExistsException {

    }

    @Override
    public void delete(SuperGroupType superGroupType) throws SuperGroupTypeNotFoundException, SuperGroupTypeHasUsagesException {

    }

    @Override
    public List<SuperGroupType> getAll() {
        return Collections.emptyList();
    }
}
