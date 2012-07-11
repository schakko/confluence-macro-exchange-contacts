package de.schakko.confluence.macro.exchange;

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.SpaceManager;

import de.schakko.confluence.macro.exchange.domain.LocalContact;
import de.schakko.confluence.macro.exchange.domain.dto.FolderWithContacts;
import de.schakko.confluence.macro.exchange.service.ExchangeContactService;

/**
 * This very simple macro shows you the very basic use-case of displaying
 * *something* on the Confluence page where it is used. Use this example macro
 * to toy around, and then quickly move on to the next example - this macro
 * doesn't really show you all the fun stuff you can do with Confluence.
 */
public class ExchangeContactMacro implements Macro {
	public final static String PARAM_FOLDER = "folder";

	// We just have to define the variables and the setters, then Spring injects
	// the correct objects for us to use. Simple and efficient.
	// You just need to know *what* you want to inject and use.

	private final PageManager pageManager;
	private final SpaceManager spaceManager;
	private final ExchangeContactService contactService;

	public ExchangeContactMacro(PageManager pageManager, SpaceManager spaceManager,
			ExchangeContactService contactService) {

		this.pageManager = pageManager;
		this.spaceManager = spaceManager;
		this.contactService = contactService;
	}

	/**
	 * This method returns XHTML to be displayed on the page that uses this
	 * macro we just do random stuff here, trying to show how you can access the
	 * most basic managers and model objects. No emphasis is put on beauty of
	 * code nor on doing actually useful things :-)
	 */
	@Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
    {
        StringBuffer result = new StringBuffer();

        FolderWithContacts folderInformation = contactService.findContactsOfFolder(parameters.get(PARAM_FOLDER));

        if (folderInformation.getContacts() == null || folderInformation.getContacts().length == 0) {
        	result.append("No contacts available");
        }
        else {
        	for (LocalContact c : folderInformation.getContacts()) {
//        		result.append(c.getGivenName() + " " + c.getSurname());
        	}
        }
        
        return result.toString();
    }

	@Override
	public BodyType getBodyType() {
		return BodyType.NONE;
	}

	@Override
	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}

}
