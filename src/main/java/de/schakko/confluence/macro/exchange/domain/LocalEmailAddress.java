package de.schakko.confluence.macro.exchange.domain;

import net.java.ao.Entity;

public interface LocalEmailAddress extends Entity {
	public void setEmail(String email);

	public String getEmail();

	public LocalContact getLocalContact();

	public void setLocalContact(LocalContact localContact);
}
