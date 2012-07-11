package de.schakko.confluence.macro.exchange.dao;

import microsoft.exchange.webservices.data.Contact;
import de.schakko.confluence.macro.exchange.domain.LocalContact;
import de.schakko.confluence.macro.exchange.domain.LocalFolder;

public interface ContactDao {
	/**
	 * Returns the local contact by Exchanges unique id
	 * 
	 * @param uniqueId
	 * @return
	 */
	public LocalContact findByUniqueId(String uniqueId);

	/**
	 * Deletes a contact
	 * 
	 * @param uniqueId
	 */
	public void delete(String uniqueId);

	/**
	 * Saves a contact, which means => create a new one if not existent or
	 * update the existent contact
	 * 
	 * @param contact
	 * @param parentFolder
	 * @return local contact
	 */
	public LocalContact save(Contact contact, LocalFolder parentFolder);
}
