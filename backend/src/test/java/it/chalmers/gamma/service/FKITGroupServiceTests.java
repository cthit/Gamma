package it.chalmers.gamma.service;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class FKITGroupServiceTests {

    @Autowired
    FKITGroupService fkitGroupService;

    @Test
    public void createAndValidateGroup() {
        Calendar becomesActive = Calendar.getInstance();
        becomesActive.set(Calendar.MONTH, Calendar.JANUARY);
        becomesActive.set(Calendar.DAY_OF_MONTH, 1);

        Calendar becomesInactive = Calendar.getInstance();
        becomesInactive.set(Calendar.MONTH, Calendar.MARCH);
        becomesInactive.set(Calendar.DAY_OF_MONTH, 1);

      /*  CreateGroupRequest createGroupRequest = new CreateGroupRequest("digit", "digIT",
                new Text("digIT beskrivning", "digIT description"),
                new Text("digIT funktion", "digIT function"),
                "url", new ArrayList<>(), becomesActive, becomesInactive,
                "admin", "mail@mail.com"
        );*/

        //        fkitGroupService.createGroup(createGroupRequest);
        //        FKITGroup group = fkitGroupService.getDTOGroup(createGroupRequest.getName());

        //        assertRequestEntityEquals(createGroupRequest, group);
    }

    /*private void assertRequestEntityEquals(CreateGroupRequest c, FKITGroup g) {
        Assert.assertEquals(c.getName(), g.getName());
        Assert.assertEquals(c.getFunction(), g.getFunction());
        Assert.assertEquals(c.getDescription(), g.getDescription());
        Assert.assertEquals(c.getPrettyName(), g.getPrettyName());
        Assert.assertEquals(c.getAvatarURL(), g.getAvatarURL());
        Assert.assertEquals(c.getEmail(), g.getEmail());

        //Compare dates
        //Assert.assertEquals(0, c.getBecomesActive().compareTo(g.getBecomesActive()));
        //Assert.assertEquals(0, c.getBecomesInactive().compareTo(g.getBecomesInactive()));
    }*/

}

