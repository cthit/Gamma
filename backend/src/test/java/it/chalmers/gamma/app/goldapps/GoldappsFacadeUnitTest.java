package it.chalmers.gamma.app.goldapps;

import it.chalmers.gamma.DomainUtils;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static it.chalmers.gamma.DomainUtils.alumni;
import static it.chalmers.gamma.DomainUtils.board;
import static it.chalmers.gamma.DomainUtils.chair;
import static it.chalmers.gamma.DomainUtils.committee;
import static it.chalmers.gamma.DomainUtils.digit18;
import static it.chalmers.gamma.DomainUtils.digit19;
import static it.chalmers.gamma.DomainUtils.drawit18;
import static it.chalmers.gamma.DomainUtils.drawit19;
import static it.chalmers.gamma.DomainUtils.g;
import static it.chalmers.gamma.DomainUtils.gm;
import static it.chalmers.gamma.DomainUtils.member;
import static it.chalmers.gamma.DomainUtils.prit18;
import static it.chalmers.gamma.DomainUtils.prit19;
import static it.chalmers.gamma.DomainUtils.sg;
import static it.chalmers.gamma.DomainUtils.society;
import static it.chalmers.gamma.DomainUtils.styrit18;
import static it.chalmers.gamma.DomainUtils.styrit19;
import static it.chalmers.gamma.DomainUtils.treasurer;
import static it.chalmers.gamma.DomainUtils.u;
import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.inOrder;

@ExtendWith(SpringExtension.class)
class GoldappsFacadeUnitTest {

    @Mock
    private AccessGuard accessGuard;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GoldappsFacade goldappsFacade;

//    @Test
//    public void getActiveSuperGroupsTest() throws JSONException {
//        ActiveSuperGroupsTestCase testCase = getActiveSuperGroupTestCase();
//        given(groupRepository.getAll())
//                .willReturn(testCase.groups);
//
//        List<GoldappsFacade.GoldappsSuperGroupDTO> activeSuperGroups = goldappsFacade.getActiveSuperGroups(
//                List.of("committee", "board")
//        );
//
//        assertThat(false).isTrue();
//    }

    @Test
    public void getActiveUsersTest() {
        ActiveUsersTestCase testCase = getActiveUsersTestCase();
        List<GoldappsFacade.GoldappsUserDTO> expectedActiveUsers = testCase.expectedActive
                .stream()
                .map(GoldappsFacade.GoldappsUserDTO::new)
                .toList();

        given(groupRepository.getAll())
                .willReturn(testCase.groups.stream().map(DomainUtils::asSaved).toList());

        List<GoldappsFacade.GoldappsUserDTO> activeUsers = goldappsFacade.getActiveUsers(
                List.of("committee", "board")
        );

        assertThat(activeUsers)
                .hasSameElementsAs(expectedActiveUsers);

        InOrder inOrder = inOrder(accessGuard, groupRepository);

        //Makes sure that isApi is called first.
        //TODO: check that goldapps api is called
        inOrder.verify(accessGuard).require(isApi(any()));
        inOrder.verify(groupRepository).getAll();
        inOrder.verifyNoMoreInteractions();
    }

    private record ActiveSuperGroupsTestCase(List<Group> groups,
                                             List<GoldappsFacade.GoldappsSuperGroupDTO> expectedSuperGroups) { }

    private ActiveSuperGroupsTestCase getActiveSuperGroupTestCase() {
        return new ActiveSuperGroupsTestCase(
                List.of(
                        digit18,
                        digit19,
                        prit18,
                        prit19,
                        drawit18,
                        drawit19,
                        styrit18,
                        styrit19
                ),
                new ArrayList<>()
        );
    }

    private record ActiveUsersTestCase(List<Group> groups,
                                       List<User> expectedActive) { }

    private ActiveUsersTestCase getActiveUsersTestCase() {
        var digit = sg("digit", committee);
        var didit = sg("didit", alumni);
        var prit = sg("prit", committee);
        var sprit = sg("sprit", alumni);
        var drawit = sg("drawit", society);
        var dragit = sg("dragit", alumni);
        var styrit = sg("board", board);
        var emeritus = sg("emeritus", alumni);

        var u1 = u("abca");
        var u2 = u("abcb", true, true);
        var u3 = u("abcc", false, false);
        var u4 = u("abcd");
        var u5 = u("abce", false, false);
        var u6 = u("abcf");
        var u7 = u("abcg", true, true);
        var u8 = u("abch");
        var u9 = u("abci");
        var u10 = u("abcj");
        var u11 = u("abck");

        var digit18 = g("digit18", didit, List.of(gm(u1, chair), gm(u2, treasurer)));
        var digit19 = g("digit19", digit, List.of(gm(u3, chair), gm(u4, member), gm(u2, member)));
        var prit18 = g("prit18", sprit, List.of(gm(u1, chair), gm(u1, treasurer), gm(u2, member)));
        var prit19 = g("prit19", prit, List.of(gm(u5, chair), gm(u6, treasurer), gm(u6, member)));
        var drawit18 = g("drawit18", dragit, List.of(gm(u6, chair)));
        var drawit19 = g("drawit19", drawit, List.of(gm(u1, chair), gm(u11, member)));
        var styrit18 = g("styrit18", emeritus, List.of(gm(u7, chair), gm(u8, member), gm(u9, member)));
        var styrit19 = g("styrit19", styrit, List.of(gm(u10, chair), gm(u11, treasurer)));

        return new ActiveUsersTestCase(
                List.of(
                        digit18,
                        digit19,
                        prit18,
                        prit19,
                        drawit18,
                        drawit19,
                        styrit18,
                        styrit19
                ),
                List.of(
                        u4,
                        u6,
                        u10,
                        u11
                )
        );
    }



}