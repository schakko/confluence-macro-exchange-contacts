package de.schakko.confluence.macro.exchange.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.FolderId;
import microsoft.exchange.webservices.data.ServiceLocalException;
import de.schakko.confluence.macro.exchange.dao.ContactDao;
import de.schakko.confluence.macro.exchange.dao.ExchangeDao;
import de.schakko.confluence.macro.exchange.dao.FolderDao;
import de.schakko.confluence.macro.exchange.domain.LocalContact;
import de.schakko.confluence.macro.exchange.domain.LocalFolder;
import de.schakko.confluence.macro.exchange.domain.dto.FolderWithContacts;
import de.schakko.confluence.macro.exchange.domain.dto.FolderWithContacts.SYNCHRONIZATION_STATUS;

public class ExchangeSynchronizer implements Runnable {
	BlockingQueue<FolderWithContacts> queue = new LinkedBlockingQueue<FolderWithContacts>();
	FolderDao folderDao;
	ContactDao contactDao;
	ExchangeDao exchangeDao;

	public ExchangeSynchronizer(FolderDao folderDao, ContactDao contactDao,
			ExchangeDao exchangeDao) {
		this.folderDao = folderDao;
		this.contactDao = contactDao;
		this.exchangeDao = exchangeDao;
	}

	public void run() {
		try {
			while (true) {
				consume(queue.take());
			}
		} catch (InterruptedException e) {

		}
	}

	/**
	 * Push the given folder description to the queue
	 * 
	 * @param folderWithContacts
	 * @return
	 */
	public FolderWithContacts queue(FolderWithContacts folderWithContacts) {
		// Wenn bereits in der Queue -> skippen
		if (!queue.contains(folderWithContacts)) {
			folderWithContacts
					.setSynchronizationStatus(SYNCHRONIZATION_STATUS.PUSHING_TO_QUEUE);
			queue.add(folderWithContacts);
		}

		return folderWithContacts;
	}

	public void consume(FolderWithContacts folderWithContacts) {
		LocalFolder localFolder = folderWithContacts.getFolder();

		folderWithContacts
				.setSynchronizationStatus(SYNCHRONIZATION_STATUS.IN_QUEUE);

		if (folderWithContacts.getFolder() == null) {
			localFolder = folderDao.create(folderWithContacts.getFolderName());
			folderWithContacts.setFolder(localFolder);
		}

		localFolder.setLastUpdate(new Date());

		try {
			if (null == localFolder.getUniqueId()) {
				FolderId folderId = exchangeDao.findFolder(folderWithContacts
						.getFolderName());

				localFolder.setUniqueId(folderId.getUniqueId());
			}

			if (localFolder.getUniqueId() == null) {
				folderWithContacts.getFolder().setFailed(true);
			} else {
				List<Contact> remoteContacts = exchangeDao
						.findContacts(new FolderId(localFolder.getUniqueId()));
				LocalContact[] synchronizedContacts = synchronize(
						folderWithContacts, remoteContacts);

				folderWithContacts.setContacts(synchronizedContacts);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		folderDao.save(folderWithContacts.getFolder());
	}

	/**
	 * Synchronizes all contacts stored in local database with contacts stored
	 * in Exchanges database. This method blocks the caller because of making a
	 * HTTP request to EWS.
	 * 
	 * @param folderWithContacts
	 * @param remoteUniqueId
	 * @return
	 * @throws Exception
	 */
	public LocalContact[] synchronize(FolderWithContacts folderWithContacts,
			List<Contact> remoteContacts) throws Exception {

		Map<String, LocalContact> mapLocalContactsToRemove = new HashMap<String, LocalContact>();

		for (LocalContact contact : folderWithContacts.getContacts()) {
			mapLocalContactsToRemove.put(contact.getUniqueId(), contact);
		}

		List<LocalContact> localContacts = new ArrayList<LocalContact>();
		// save all items in local store which are existent on remote side
		for (Contact contact : remoteContacts) {
			if (contact.getId() != null) {
				String uniqueId = contact.getId().getUniqueId();

				localContacts.add(contactDao.save(contact,
						folderWithContacts.getFolder()));
				mapLocalContactsToRemove.remove(uniqueId);
			}
		}

		// remove all items which are no longer available in local store
		for (LocalContact contact : mapLocalContactsToRemove.values()) {
			contactDao.delete(contact.getUniqueId());
		}

		LocalContact[] r = new LocalContact[localContacts.size()];
		r = localContacts.toArray(r);

		folderWithContacts.setContacts(r);
		return r;
	}

	/**
	 * Does a conversion between local contact and remote contact. Due to no
	 * available interfaces of EWSAPI and the fact that Active Object uses
	 * propxy objects other solutions can not be used.
	 * 
	 * @param remote
	 * @return
	 */
	public static LocalContact convert(Contact remote, LocalContact local)
			throws ServiceLocalException {
		local.setAssistantName(remote.getAssistantName());
		local.setBirthday(remote.getBirthday());
		local.setCompanyName(remote.getCompanyName());
		// local.setCompleteName(remote.getCompleteName());
		local.setDateTimeCreated(remote.getDateTimeCreated());
		local.setDepartment(remote.getDepartment());
		local.setDisplayName(remote.getDisplayName());
		local.setGivenName(remote.getGivenName());
		local.setInitials(remote.getInitials());
		local.setJobTitle(remote.getJobTitle());
		local.setMiddleName(remote.getMiddleName());
		local.setNickName(remote.getNickName());
		local.setOfficeLocation(remote.getOfficeLocation());
		local.setProfession(remote.getProfession());
		local.setSurname(remote.getSurname());
		local.setUniqueId(remote.getId().getUniqueId());

		// TODO Childelemente werden noch nicht gespeichert
		// local.getCompanies().
		return local;
	}
}
