package me.suisui.web;

import me.suisui.repo.jdbc.dao.pub.CodeDictionaryDao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

public class BlankTest extends BaseWebTest {
    
	@Autowired
	CodeDictionaryDao codeDictionaryDao;
	
    @Test
    @Rollback(false)
    public void doNothing() throws Exception {
        logger.debug("a blank test,test startup process.");
    }
}
