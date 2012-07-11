package de.schakko.confluence.macro.exchange.domain;

import java.util.Date;

import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

@Preload
public interface LocalContact extends RawEntity<String> {
	@NotNull
	@PrimaryKey("UNIQUE_ID")
	public String getUniqueId();

	@NotNull
	public void setUniqueId(String uid);
	
	public void setLocalFolder(LocalFolder localFolder);

	public Date getDateTimeCreated();

	public void setDateTimeCreated(Date dateTime);

	public String getAssistantName();

	public void setAssistantName(String assistantName);

	public Date getBirthday();

	public void setBirthday(Date birthday);

	/**
	 * setCompanies(StringBag[] s) is *not* allowed, otherwise "unrecognized type: [Lde.schakko.confluence.domain.StringBag" occurs
	 * @return
	 */
//	@OneToMany
//	public LocalCompany[] getCompanies();

	public String getCompanyName();

	public void setCompanyName(String companyName);

	public String getCompleteName();

	public void setCompleteName(String completeName);

	public String getDepartment();

	public void setDepartment(String department);

	public String getDisplayName();

	public void setDisplayName(String displayName);

//	@OneToMany
//	public LocalEmailAddress[] getEmailAddresses();

	public void setGivenName(String givenName);

	public String getGivenName();

	public void setInitials(String initials);

	public String getInitials();

	public void setJobTitle(String jobTitle);

	public String getJobTitle();

	public void setMiddleName(String middleName);

	public String getMiddleName();

	public void setNickName(String nickName);

	public String getNickName();

	public void setOfficeLocation(String location);

//	@OneToMany
//	public LocalAddress[] getOfficeLocations();

	public void setProfession(String profession);

	public String getProfession();

	public void setSurname(String surname);

	public String getSurname();
}
