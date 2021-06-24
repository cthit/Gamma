package it.chalmers.gamma.app.useractivation.service;

import it.chalmers.gamma.app.domain.UserActivation;
import it.chalmers.gamma.app.domain.UserActivationToken;
import it.chalmers.gamma.app.domain.Cid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class ActivationUserActivationTokenServiceTest {

    @Mock
    private UserActivationRepository userActivationRepository;

    @InjectMocks
    private UserActivationService userActivationService;

    @Test
    void savedActivationCode() {
        given(userActivationRepository.save(any(UserActivationEntity.class)))
                .willAnswer(returnsFirstArg());

        Cid cid = Cid.valueOf("mycid");
        UserActivation userActivation = userActivationService.saveUserActivation(cid);

        ActivationCodeDTOAssert.assertThat(userActivation)
                .hasCorrectCid(cid)
                .hasValidCode()
                .hasCreatedAtDateNotInFuture();
    }

    @Test
    void delete() {
        Cid cid = Cid.valueOf("mycid");
        Cid cid2 = Cid.valueOf("mycidi");

        willThrow(IllegalArgumentException.class)
                .given(userActivationRepository)
                .deleteById(any(Cid.class));
        willDoNothing()
                .given(userActivationRepository)
                .deleteById(cid);

        assertThatNoException()
                .isThrownBy(() -> userActivationService.delete(cid));
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> userActivationService.delete(cid2));
    }

    @Test
    void invalidCodeShouldNotWork() {
        assert (false);
    }

    @Test
    void getAll() {
        List<UserActivationEntity> activationCodeList = new ArrayList<>();
        activationCodeList.add(ActivationCodeFactory.create(Cid.valueOf("mycid"), UserActivationToken.generate()));
        activationCodeList.add(ActivationCodeFactory.create(Cid.valueOf("mycidi"), UserActivationToken.generate()));

        //given
        given(userActivationRepository.findAll())
                .willReturn(activationCodeList);

        //when
        List<UserActivation> userActivationList = userActivationService.getAll();
        for (int i = 0; i < userActivationList.size(); i++) {
            UserActivationEntity ac = activationCodeList.get(0);
            UserActivation acDTO = userActivationList.get(0);

            assertThat(ac)
                    .usingRecursiveComparison()
                    .isEqualTo(acDTO);
        }
    }

    @Test
    void codeMatchesCid() {
        Cid cid = Cid.valueOf("mycid");
        UserActivationToken token = UserActivationToken.generate();

        //given
        given(userActivationRepository.findUserActivationByCidAndToken(any(Cid.class), any(UserActivationToken.class)))
                .willReturn(Optional.empty());
        given(userActivationRepository.findUserActivationByCidAndToken(eq(cid), eq(token)))
                .willReturn(Optional.of(ActivationCodeFactory.create(cid, token)));

        //when
        boolean combination1 = userActivationService.codeMatchesCid(cid, token);
        boolean combination2 = userActivationService.codeMatchesCid(cid, UserActivationToken.generate());

        //then
        assertThat(combination1).isTrue();
        assertThat(combination2).isFalse();
    }

}