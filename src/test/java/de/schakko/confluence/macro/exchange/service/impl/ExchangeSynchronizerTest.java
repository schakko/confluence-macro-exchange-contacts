package de.schakko.confluence.macro.exchange.service.impl;

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
public class ExchangeSynchronizerTest {
	private EntityManager entityManager;

	private ActiveObjects ao;

	@Before
	public void setUp() {
		assertNotNull(entityManager);
		ao = new TestActiveObjects(entityManager);
	}

	@Test
	public void canConvertRemoteContactToLocalContact() {
		ao.migrate(LocalContact.class);
		LocalContact local = ao.create(LocalContact.class);
		try {
			Contact remote = mock(Contact.class);
			Date d = new Date();
			when(remote.getAssistantName()).thenReturn("assistentname");
			when(remote.getBirthday()).thenReturn(d);
			when(remote.getBusinessHomePage()).thenReturn("businesshomepage");
			when(remote.getCompanyName()).thenReturn("companyname");
			when(remote.getDateTimeCreated()).thenReturn(d);
			when(remote.getDepartment()).thenReturn("department");
			when(remote.getDisplayName()).thenReturn("displayname");
			when(remote.getGivenName()).thenReturn("givenname");
			when(remote.getId()).thenReturn(new ItemId("itemid"));
			when(remote.getInitials()).thenReturn("initials");
			when(remote.getJobTitle()).thenReturn("jobtitle");
			when(remote.getMiddleName()).thenReturn("middlename");
			when(remote.getNickName()).thenReturn("nickname");
			when(remote.getOfficeLocation()).thenReturn("officelocation");
			when(remote.getProfession()).thenReturn("profession");
			when(remote.getSurname()).thenReturn("surname");
			local = ExchangeSynchronizer.convert(remote, local);
			assertEquals(local.getAssistantName(), "assistentname");
			assertEquals(local.getBirthday(), d);
			assertEquals(local.getCompanyName(), "companyname");
			assertEquals(local.getDateTimeCreated(), d);
			assertEquals(local.getDepartment(), "department");
			assertEquals(local.getDisplayName(), "displayname");
			assertEquals(local.getGivenName(), "givenname");
			assertEquals(local.getUniqueId(), "itemid");
			assertEquals(local.getInitials(), "initials");
			assertEquals(local.getJobTitle(), "jobtitle");
			assertEquals(local.getMiddleName(), "middlename");
			assertEquals(local.getNickName(), "nickname");
//			assertEquals(local.getOfficeLocation(), "officelocation");
			assertEquals(local.getProfession(), "profession");
			assertEquals(local.getSurname(), "surname");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void canConvertNestedElementsOfRemoteContact() {

	}

	@Test
	public void canSynchronizeElementsFromExchangeWithLocalStorage()
			throws ServiceLocalException, Exception {
		// a damn long mocking sequence
		LocalContact localContactUpdate = mock(LocalContact.class);
		LocalContact localContactDelete = mock(LocalContact.class);
		when(localContactUpdate.getUniqueId()).thenReturn("UID-1-UPDATE");
		when(localContactDelete.getUniqueId()).thenReturn("UID-2-DELETE");

		Contact remoteContactUpdate = mock(Contact.class);
		Contact remoteContactAdd = mock(Contact.class);
		List<Contact> remoteContacts = Lists.newArrayList(new Contact[] {
				remoteContactUpdate, remoteContactAdd });

		when(remoteContactUpdate.getId())
				.thenReturn(new ItemId("UID-1-UPDATE"));
		when(remoteContactAdd.getId()).thenReturn(new ItemId("UID-3-ADD"));

		FolderWithContacts folderWithContacts = new FolderWithContacts("BLA");
		folderWithContacts.setContacts(new LocalContact[] { localContactDelete,
				localContactUpdate });
		folderWithContacts.setFolder(mock(LocalFolder.class));

		FolderDao folderDao = mock(FolderDao.class);
		ContactDao contactDao = mock(ContactDao.class);
		ExchangeDao exchangeDao = mock(ExchangeDao.class);

		ExchangeSynchronizer underTest = new ExchangeSynchronizer(folderDao,
				contactDao, exchangeDao);

		LocalContact localContactAdded = mock(LocalContact.class);
		when(localContactAdded.getUniqueId()).thenReturn("UID-3-ADD");
		when(contactDao.save(remoteContactAdd, folderWithContacts.getFolder()))
				.thenReturn(localContactAdded);
		when(
				contactDao.save(remoteContactUpdate,
						folderWithContacts.getFolder())).thenReturn(
				localContactUpdate);

		LocalContact[] r = underTest.synchronize(folderWithContacts,
				remoteContacts);

		assertNotNull(r);
		assertEquals(2, r.length);

		assertTrue(r[0].getUniqueId().equals("UID-1-UPDATE")
				|| r[1].getUniqueId().equals("UID-1-UPDATE"));
		assertTrue(r[0].getUniqueId().equals("UID-3-ADD")
				|| r[1].getUniqueId().equals("UID-3-ADD"));
		assertTrue(!r[0].getUniqueId().equals("UID-1-DELETE")
				&& !r[1].getUniqueId().equals("UID-1-DELETE"));
	}
}
