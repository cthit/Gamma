package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.domain.ActivationCode;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Code;
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
class ActivationCodeServiceTest {

    @Mock
    private ActivationCodeRepository activationCodeRepository;

    @InjectMocks
    private ActivationCodeService activationCodeService;

    @Test
    void savedActivationCode() {
        given(activationCodeRepository.save(any(ActivationCodeEntity.class)))
                .willAnswer(returnsFirstArg());

        Cid cid = new Cid("mycid");
        ActivationCode activationCode = activationCodeService.saveActivationCode(cid);

        ActivationCodeDTOAssert.assertThat(activationCode)
                .hasCorrectCid(cid)
                .hasValidCode()
                .hasCreatedAtDateNotInFuture();
    }

    @Test
    void delete() {
        Cid cid = new Cid("mycid");
        Cid cid2 = new Cid("mycidi");

        willThrow(IllegalArgumentException.class)
                .given(activationCodeRepository)
                .deleteById(any(Cid.class));
        willDoNothing()
                .given(activationCodeRepository)
                .deleteById(cid);

        assertThatNoException()
                .isThrownBy(() -> activationCodeService.delete(cid));
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> activationCodeService.delete(cid2));
    }

    @Test
    void invalidCodeShouldNotWork() {
        assert (false);
    }

    @Test
    void getAll() {
        List<ActivationCodeEntity> activationCodeList = new ArrayList<>();
        activationCodeList.add(ActivationCodeFactory.create(new Cid("mycid"), Code.generate()));
        activationCodeList.add(ActivationCodeFactory.create(new Cid("mycidi"), Code.generate()));

        //given
        given(activationCodeRepository.findAll())
                .willReturn(activationCodeList);

        //when
        List<ActivationCode> activationCodeDTOList = activationCodeService.getAll();
        for (int i = 0; i < activationCodeDTOList.size(); i++) {
            ActivationCodeEntity ac = activationCodeList.get(0);
            ActivationCode acDTO = activationCodeDTOList.get(0);

            assertThat(ac)
                    .usingRecursiveComparison()
                    .isEqualTo(acDTO);
        }
    }

    @Test
    void codeMatchesCid() {
        Cid cid = new Cid("mycid");
        Code code = Code.generate();

        //given
        given(activationCodeRepository.findActivationCodeByCidAndCode(any(Cid.class), any(Code.class)))
                .willReturn(Optional.empty());
        given(activationCodeRepository.findActivationCodeByCidAndCode(eq(cid), eq(code)))
                .willReturn(Optional.of(ActivationCodeFactory.create(cid, code)));

        //when
        boolean combination1 = activationCodeService.codeMatchesCid(cid, code);
        boolean combination2 = activationCodeService.codeMatchesCid(cid, Code.generate());

        //then
        assertThat(combination1).isTrue();
        assertThat(combination2).isFalse();
    }

}