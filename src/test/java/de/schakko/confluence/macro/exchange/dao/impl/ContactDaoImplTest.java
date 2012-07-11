package de.schakko.confluence.macro.exchange.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.ItemId;
import microsoft.exchange.webservices.data.ServiceLocalException;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.Lists;

import de.schakko.confluence.macro.exchange.dao.ContactDao;
import de.schakko.confluence.macro.exchange.dao.ExchangeDao;
import de.schakko.confluence.macro.exchange.dao.FolderDao;
import de.schakko.confluence.macro.exchange.domain.LocalContact;
import de.schakko.confluence.macro.exchange.domain.LocalFolder;
import de.schakko.confluence.macro.exchange.domain.dto.FolderWithContacts;

@RunWith(ActiveObjectsJUnitRunner.class)
public class ContactDaoImplTest {
	private EntityManager entityManager;

	private ActiveObjects ao;

	@Before
	public void setUp() {
		assertNotNull(entityManager);
		ao = new TestActiveObjects(entityManager);
	}

	@Test
	public void canSaveANewLocalContact() throws Exception {
		ItemId itemId = new ItemId("UNIQUEID");
		ContactDaoImpl dao = new ContactDaoImpl(ao);
		Contact contact = mock(Contact.class);
		LocalFolder folder = mock(LocalFolder.class);
		when(contact.getId()).thenReturn(itemId);
		
		folder.setUniqueId("BLA-UniqueId");
		
		LocalContact r = dao.save(contact, folder);
		
	}
}
