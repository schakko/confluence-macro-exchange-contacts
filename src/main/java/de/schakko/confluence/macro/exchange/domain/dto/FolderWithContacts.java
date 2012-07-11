package de.schakko.confluence.macro.exchange.domain.dto;

import de.schakko.confluence.macro.exchange.domain.LocalContact;
import de.schakko.confluence.macro.exchange.domain.LocalFolder;

/**
 * Transfer object contains a composite view of a folder and the contacts inside
 * of it.
 * 
 * @author ckl
 * 
 */
public class FolderWithContacts {
	private String folderName;
	private LocalFolder folder;
	private LocalContact[] contacts;
	private SYNCHRONIZATION_STATUS synchronizationStatus;

	/**
	 * Simple marker to describe the synchronziation status inside the worker
	 * queue of Exchange synchronizer
	 * 
	 * @author ckl
	 * 
	 */
	public enum SYNCHRONIZATION_STATUS {
		PUSHING_TO_QUEUE, IN_QUEUE
	};

	public FolderWithContacts(String folderName) {
		this.folderName = folderName;
	}

	public LocalFolder getFolder() {
		return folder;
	}

	public void setFolder(LocalFolder folder) {
		this.folder = folder;
	}

	public LocalContact[] getContacts() {
		return contacts;
	}

	public void setContacts(LocalContact[] contacts) {
		this.contacts = contacts;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public SYNCHRONIZATION_STATUS getSynchronizationStatus() {
		return synchronizationStatus;
	}

	public void setSynchronizationStatus(
			SYNCHRONIZATION_STATUS synchronizationStatus) {
		this.synchronizationStatus = synchronizationStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((folderName == null) ? 0 : folderName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FolderWithContacts other = (FolderWithContacts) obj;
		if (folderName == null) {
			if (other.folderName != null)
				return false;
		} else if (!folderName.equals(other.folderName))
			return false;
		return true;
	}

}
