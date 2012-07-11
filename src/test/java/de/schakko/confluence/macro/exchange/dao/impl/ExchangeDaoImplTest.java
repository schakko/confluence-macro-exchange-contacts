package de.schakko.confluence.macro.exchange.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import microsoft.exchange.webservices.data.AutodiscoverLocalException;
import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.ExchangeServerInfo;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.FindItemsResults;
import microsoft.exchange.webservices.data.FolderId;
import microsoft.exchange.webservices.data.Item;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.schakko.confluence.macro.exchange.dao.ConfigurationDao;
import de.schakko.confluence.macro.exchange.dao.impl.ExchangeDaoImpl;
import de.schakko.confluence.macro.exchange.domain.Configuration;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeDaoImplTest {

	private static final String INTEGRATION_TEST_CONTACT_FOLDER_UNIQUE_KEY = "AQEuAAADGkRzkKpmEc2byACqAC/EWgMArge0swDAuk2LlOXHNqBcCwAAAissAAAA";

	public final static String REMOTE_URL = "https://172.16.1.10/ews/Exchange.asmx";

	@Mock
	ConfigurationDao configurationDao;
	@Mock
	Configuration configuration;

	ExchangeDaoImpl exchangeDao;

	@Before
	public void setUp() {
		exchangeDao = new ExchangeDaoImpl(configurationDao,
				new ExchangeService());
		when(configurationDao.get()).thenReturn(configuration);
	}

	private void setValidConfiguration() {
		when(configuration.getWebserviceEntryPoint()).thenReturn(REMOTE_URL);
		when(configuration.getEmail()).thenReturn("test@schakko.local");
		when(configuration.getPassword()).thenReturn("Test123!");
	}

	@Test
	public void canSplitPathComponents() {
		String path = "test";
		List<String> components = ExchangeDaoImpl.sanitizePathComponents(path);
		assertEquals(1, components.size());
		assertEquals("test", components.get(0));

		path = "test\\subfolder";
		components = ExchangeDaoImpl.sanitizePathComponents(path);
		assertEquals(2, components.size());
		assertEquals("test", components.get(0));
		assertEquals("subfolder", components.get(1));

		path = "test/subfolderwithslashes";
		components = ExchangeDaoImpl.sanitizePathComponents(path);
		assertEquals(2, components.size());
		assertEquals("test", components.get(0));
		assertEquals("subfolderwithslashes", components.get(1));
	}

	@Test
	public void throwsErrorOnMissingLoginInformation() {
		try {
			exchangeDao.init();
			fail("Exception expected");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("User name or password"));
		}

	}

	@Test
	public void failIfAutodiscoverDoesNotWork() {
		when(configuration.getEmail()).thenReturn("fail@domain.com");
		when(configuration.getPassword()).thenReturn("some password");

		try {
			exchangeDao.init();
			fail("Exception expected because of invalid autodiscovering");
		} catch (Exception e) {
			assertTrue(e instanceof AutodiscoverLocalException);
		}
	}

	public void failOnWrongEndpoint() {
		when(configuration.getWebserviceEntryPoint()).thenReturn(
				"http://someinvalidurl");
		when(configuration.getEmail()).thenReturn("fail@domain.com");
		when(configuration.getPassword()).thenReturn("some password");

		try {
			exchangeDao.init();
			fail("Exception expected if invalid webservice entry point was given");
		} catch (Exception e) {
			assertTrue(e instanceof AutodiscoverLocalException);
			assertNull(exchangeDao.getServerInfo());
		}
	}

	@Test
	public void failOnInvalidCredentials() {
		when(configuration.getWebserviceEntryPoint()).thenReturn(REMOTE_URL);
		when(configuration.getEmail()).thenReturn("test@schakko.local");
		when(configuration.getPassword()).thenReturn("some invalid password");

		try {
			exchangeDao.init();
			fail("Expected exception not thrown because of invalid credentials");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validCredentialsAreAbleToLogin() {
		setValidConfiguration();

		try {
			exchangeDao.init();
			ExchangeServerInfo esi = exchangeDao.getServerInfo();
			assertNotNull(esi);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void noExceptionOnNonExistingFolder() {
		setValidConfiguration();
		try {
			exchangeDao.init();
			exchangeDao.findFolder("nonExistingFolder");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void findFolderCanFindSubFolder() {
		setValidConfiguration();

		try {
			exchangeDao.init();
			FolderId r = exchangeDao.findFolder("pub_contacts/contacts");
			assertNotNull(r);
			assertEquals(INTEGRATION_TEST_CONTACT_FOLDER_UNIQUE_KEY,
					r.getUniqueId());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canFindContactsInFolder() {
		setValidConfiguration();

		try {
			exchangeDao.init();
			List<Contact> r = exchangeDao.findContacts(new FolderId(
					INTEGRATION_TEST_CONTACT_FOLDER_UNIQUE_KEY));
			assertNotNull(r);
			assertEquals(1, r.size());

			assertEquals("Testkontakt-Nachname", r.get(0).getSurname());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
