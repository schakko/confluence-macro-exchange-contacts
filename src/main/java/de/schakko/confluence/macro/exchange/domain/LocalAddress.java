package de.schakko.confluence.macro.exchange.domain;

import net.java.ao.Entity;

public interface LocalAddress extends Entity {
	public String getCity();

	public void setCity(String city);

	public String getCountryOrRegion();

	public void setCountryOrRegion(String countryOrRegion);

	public String getPostalCode();

	public void setPostalCode(String postalCode);

	public String getState();

	public void setState(String state);

	public String getStreet();

	public void setStreet(String street);

}
