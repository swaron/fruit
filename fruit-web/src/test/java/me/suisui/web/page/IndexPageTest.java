package me.suisui.web.page;

import me.suisui.web.BaseWebTest;

import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class IndexPageTest extends BaseWebTest {

	@Test
	public void testIndex() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/page/login.html"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.forwardedUrl("/page/login.jsp"));
	}
}
