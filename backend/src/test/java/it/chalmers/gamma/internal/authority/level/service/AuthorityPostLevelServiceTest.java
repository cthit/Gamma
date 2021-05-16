package it.chalmers.gamma.internal.authority.level.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorityPostLevelServiceTest {

    @Mock
    private AuthorityLevelRepository authorityLevelRepository;

    @InjectMocks
    private AuthorityLevelService authorityLevelService;

    @Test
    void successfulCreate() {

    }

}
