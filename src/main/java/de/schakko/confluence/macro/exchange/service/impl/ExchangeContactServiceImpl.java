package de.schakko.confluence.macro.exchange.service.impl;

import microsoft.exchange.webservices.data.GetFolderResponse;
import de.schakko.confluence.macro.exchange.dao.ContactDao;
import de.schakko.confluence.macro.exchange.dao.FolderDao;
import de.schakko.confluence.macro.exchange.domain.dto.FolderWithContacts;
import de.schakko.confluence.macro.exchange.service.ExchangeContactService;

public class ExchangeContactServiceImpl implements ExchangeContactService {
	FolderDao folderDao;
	ContactDao contactDao;
	ExchangeSynchronizer exchangeSynchronizer;

	public ExchangeContactServiceImpl(FolderDao folderDao,
			ContactDao contactDao, ExchangeSynchronizer exchangeSynchronizer) {
		this.folderDao = folderDao;
		this.contactDao = contactDao;
		this.exchangeSynchronizer = exchangeSynchronizer;
	}

	@Override
	public FolderWithContacts findContactsOfFolder(String folderName) {
		FolderWithContacts r = new FolderWithContacts(folderName);
		r.setFolder(folderDao.findByName(folderName));

		// TODO Urgs.
		if (r.getFolder() != null) {
			r.setContacts(r.getFolder().getContacts());
		}

		// push dto to queue for further handling
		exchangeSynchronizer.queue(r);

		return r;
	}
}