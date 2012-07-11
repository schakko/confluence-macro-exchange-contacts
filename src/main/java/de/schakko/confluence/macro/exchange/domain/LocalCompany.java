package de.schakko.confluence.macro.exchange.domain;

import net.java.ao.Entity;

public interface LocalCompany extends Entity {
	public String getCompany();

	public void setCompany(String company);

	public LocalContact getLocalContact();

	public void setLocalContact(LocalContact localContact);
}
