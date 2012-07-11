package de.schakko.confluence.macro.exchange.domain;

import net.java.ao.Entity;

public interface Configuration extends Entity {
	public String getWebserviceEntryPoint();

	public void setWebserviceEntryPoint(String webserviceEntryPoint);

	public String getEmail();

	public void setUsername(String username);

	public String getPassword();

	public void setPassword(String password);

	public String getLastContact();

	public void setLastContact(String lastContact);
}
