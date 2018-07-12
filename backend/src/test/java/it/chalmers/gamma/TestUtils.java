package it.chalmers.gamma;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.chalmers.gamma.db.entity.Whitelist;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@Component
public class TestUtils {

    MockMvc mockMvc;

    public TestUtils(){}

    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public void sendCreateCode(String cid) throws Exception {
        MockHttpServletRequestBuilder mocker = (MockMvcRequestBuilders.post("/whitelist/add/").content(asJsonString(new Whitelist(cid))).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        MockHttpServletRequestBuilder mocker4 = (MockMvcRequestBuilders.post("/whitelist/activate_cid/").content(asJsonString(new Whitelist(cid))).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        mockMvc.perform(mocker);
        mockMvc.perform(mocker4).andDo(MockMvcResultHandlers.print());
    }

    public String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
