package me.suisui.web;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import me.suisui.config.AppConfig;
import me.suisui.data.jdbc.po.pub.UsrSession;
import me.suisui.repo.jdbc.dao.EntityMetadata;
import me.suisui.repo.jdbc.dao.GenericDao;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
@ActiveProfiles("test")
public abstract class BaseWebTest extends AbstractJUnit4SpringContextTests {
	
	protected JdbcTemplate jdbcTemplate;
//	@PersistenceContext(unitName = "app-repo")
//	protected EntityManager entityManager;
	@Autowired
	protected GenericDao genericDao;

	protected WebApplicationContext webApplicationContext;
	
	protected MockMvc mockMvc;
	
	@Autowired
	public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
		this.webApplicationContext = webApplicationContext;
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		logger.info("##build web applicaiton");
	}
	
	@Before
	public void setup() {
		logger.info("##doing junit setup.");
	}
	
	@Autowired
	@Qualifier("dataSource_app")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	protected <T> void removeEntity(T entity) {
		EntityMetadata<? extends Object> meta = EntityMetadata.getMeta(entity.getClass(), jdbcTemplate.getDataSource());
		try {
			Serializable object = (Serializable) PropertyUtils.getProperty(entity, meta.getIdColumn().getName());
			genericDao.remove(entity.getClass(), object);
		} catch (IllegalAccessException e) {
			logger.warn("unable to remove entity via id.",e);
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
