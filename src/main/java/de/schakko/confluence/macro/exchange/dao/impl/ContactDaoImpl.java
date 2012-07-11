package de.schakko.confluence.macro.exchange.dao.impl;

import microsoft.exchange.webservices.data.Contact;

import com.atlassian.activeobjects.external.ActiveObjects;

import de.schakko.confluence.macro.exchange.dao.ContactDao;
import de.schakko.confluence.macro.exchange.domain.LocalContact;
import de.schakko.confluence.macro.exchange.domain.LocalFolder;

public class ContactDaoImpl implements ContactDao {
	private final ActiveObjects ao;

	public ContactDaoImpl(ActiveObjects ao) {
		this.ao = ao;
	}

	@Override
	public LocalContact findByUniqueId(String uniqueId) {
		ao.migrate(LocalContact.class);
		LocalContact r = ao.get(LocalContact.class, uniqueId);
		
		return r;
	}

	@Override
	public void delete(String uniqueId) {
		LocalContact contact = findByUniqueId(uniqueId);

		if (contact != null) {
			ao.delete(contact);
		}
	}

	@Override
	public LocalContact save(Contact contact, LocalFolder parentFolder) {
		try {
			LocalContact localContact = findByUniqueId(contact.getId()
					.getUniqueId());

			if (localContact == null) {
				localContact = ao.create(LocalContact.class);
			}

//			localContact.setLocalFolder(parentFolder);

			// TODO Konvertierung
			localContact.save();
			
			return localContact;
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO
		}

		return null;
	}
}
