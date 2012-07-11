package de.schakko.confluence.macro.exchange.service;

import de.schakko.confluence.macro.exchange.domain.dto.FolderWithContacts;

public interface ExchangeContactService {
	/**
	 * Find the given folder and all contacts inside of it.
	 * Every folder request will be stored into the worker queue.
	 * 
	 * @param folderName
	 * @return
	 */
	public FolderWithContacts findContactsOfFolder(String folderName);
}
