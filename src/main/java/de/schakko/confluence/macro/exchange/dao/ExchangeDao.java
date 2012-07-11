package de.schakko.confluence.macro.exchange.dao;

import java.util.ArrayList;
import java.util.List;

import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.FolderId;

public interface ExchangeDao {

	/**
	 * Returns the FolderId for given path
	 * 
	 * @param path
	 * @return null if folder can not be found
	 */
	public FolderId findFolder(String path) throws Exception;

	/**
	 * Returns all contacts inside given folder
	 * 
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	public List<Contact> findContacts(FolderId folderId)
			throws Exception;

	/**
	 * Does a recursive search for the given folder
	 * 
	 * @param folderId
	 * @param pathComponents
	 *            stack with path components
	 * @param currentIdx
	 *            current index in stack pathComponents
	 * @see <pre>
	 * http://geekswithblogs.net/cskardon/archive/2008/12/01/hunting-those-elusive-public-folders-using-exchange-web-services-part.aspx
	 * </pre>
	 * @return
	 */
	public FolderId findPublicFolder(FolderId parentFolderId,
			ArrayList<String> pathComponents, int currentIdx) throws Exception;

}