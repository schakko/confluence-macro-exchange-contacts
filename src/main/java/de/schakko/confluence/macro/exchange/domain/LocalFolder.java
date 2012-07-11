package de.schakko.confluence.macro.exchange.domain;

import java.util.Date;

import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.Preload;

@Preload
public interface LocalFolder extends Entity {
	public String getUniqueId();

	public void setUniqueId(String uniqueId);

	public Date getLastUpdate();

	public void setLastUpdate(Date lastUpdate);

	public boolean isFailed();

	public void setFailed(boolean failed);

	public String getFolderName();

	public void setFolderName(String folderName);

	@OneToMany
	public LocalContact[] getContacts();
}
