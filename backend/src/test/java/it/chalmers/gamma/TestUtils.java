package it.chalmers.gamma;

import it.chalmers.gamma.service.ITUserService;

import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@Component
@SuppressWarnings("PMD.AvoidPrintStackTrace")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TestUtils {

    MockMvc mockMvc;

    ITUserService userService;

    public void setMockMvc(MockMvc mockMvc, ITUserService userService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
    }
    //    public String addAdminUser() {
    //        String nick1 = "admin";
    //        String cid1 = "admin";
    //        CreateITUserRequest itUser1 = new CreateITUserRequest();
    //        itUser1.setNick(nick1);
    //        itUser1.setPassword("examplePassword");
    //        itUser1.setWhitelist(new Whitelist(cid1));
    //        this.userService.createUser(
    //                itUser1.getNick(),
    //                null,
    //                null,
    //                cid1,
    //                Year.of(2018),
    //                false,
    //                "",
    //                itUser1.getPassword()
    //        );
    //        this.token = this.tokenProvider.createToken(cid1);
    //        return this.token;
    //    }
    //
    //    public void sendCreateCode(String cid) {
    //        String authcid = "admin";
    //        this.token = this.tokenProvider.createToken(authcid);
    //        JSONObject object = new JSONObject();
    //        ArrayList<String> cids = new ArrayList<>();
    //        cids.add(cid);
    //        object.put("cids", cids);
    //        MockHttpServletRequestBuilder mocker = MockMvcRequestBuilders.post("/admin/users/whitelist")
    //                .header("Authorization", "Bearer " + this.token)
    //                .content(object.toJSONString())
    //                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    //
    //        try {
    //            this.mockMvc.perform(mocker);
    //        } catch (Exception e) {
    //            LoggerFactory.getLogger(ITUserController.class).info(e.getMessage(), e);
    //        }
    //
    //        JSONObject jsoncid = new JSONObject();
    //        jsoncid.put("cid", cid);
    //        MockHttpServletRequestBuilder mocker2 = MockMvcRequestBuilders.post("/whitelist/activate_cid")
    //                .header("Authorization", "Bearer " + this.token)
    //                .content(jsoncid.toJSONString())
    //                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    //
    //        try {
    //            this.mockMvc.perform(mocker2);
    //        } catch (Exception e) {
    //            LoggerFactory.getLogger(ITUserController.class).info(e.getMessage(), e);
    //        }
    //    }
    //
    //    public String asJsonString(final Object obj) {
    //        try {
    //            final ObjectMapper mapper = new ObjectMapper();
    //            return mapper.writeValueAsString(obj);
    //        } catch (Exception e) {
    //            throw new RuntimeException(e);
    //        }
    //    }
}
