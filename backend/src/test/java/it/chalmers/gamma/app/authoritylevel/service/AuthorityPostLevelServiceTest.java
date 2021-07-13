package it.chalmers.gamma.app.authoritylevel.service;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelJpaRepository;
import it.chalmers.gamma.app.authority.AuthorityLevelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorityPostLevelServiceTest {

    @Mock
    private AuthorityLevelJpaRepository authorityLevelRepository;

    @InjectMocks
    private AuthorityLevelService authorityLevelService;

    @Test
    void successfulCreate() {

    }

}
