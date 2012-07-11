package de.schakko.confluence.macro.exchange.dao.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.ContactsFolder;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeServerInfo;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.FindFoldersResults;
import microsoft.exchange.webservices.data.FindItemsResults;
import microsoft.exchange.webservices.data.Folder;
import microsoft.exchange.webservices.data.FolderId;
import microsoft.exchange.webservices.data.FolderTraversal;
import microsoft.exchange.webservices.data.FolderView;
import microsoft.exchange.webservices.data.ITraceListener;
import microsoft.exchange.webservices.data.Item;
import microsoft.exchange.webservices.data.ItemView;
import microsoft.exchange.webservices.data.PropertySet;
import microsoft.exchange.webservices.data.WebCredentials;
import microsoft.exchange.webservices.data.WellKnownFolderName;

import com.google.common.collect.Lists;

import de.schakko.confluence.macro.exchange.dao.ConfigurationDao;
import de.schakko.confluence.macro.exchange.dao.ExchangeDao;
import de.schakko.confluence.macro.exchange.domain.Configuration;

public class ExchangeDaoImpl implements ExchangeDao {
	ConfigurationDao configurationDao;
	Configuration configuration;
	ExchangeService exchangeService;
	private ExchangeServerInfo serverInfo;

	public ExchangeDaoImpl(ConfigurationDao configurationDao,
			ExchangeService exchangeService) {
		this.configurationDao = configurationDao;
		this.exchangeService = exchangeService;
	}

	/**
	 * Replaces every Backslash with forward slashes and does a split, so that
	 * "folder1\subfolder1/subfolder2" becomes ["folder1", "subfolder1",
	 * "subfolder2"]
	 * 
	 * @param path
	 * @return
	 */
	public static ArrayList<String> sanitizePathComponents(String path) {
		return Lists.newArrayList(path.replace("\\", "/").split("/"));
	}

	/**
	 * Initializes the service binding
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		if (serverInfo != null) {
			return;
		}

		configuration = configurationDao.get();

		ExchangeCredentials credentials = new WebCredentials(
				configuration.getEmail(), configuration.getPassword());

		exchangeService.setCredentials(credentials);

		String entryPoint = configuration.getWebserviceEntryPoint();

		if (entryPoint == null || entryPoint.length() == 0) {
			exchangeService.autodiscoverUrl(configuration.getEmail());
		} else {
			exchangeService.setUrl(new URI(entryPoint));
		}

		exchangeService.setTraceEnabled(true);
		exchangeService.setTraceListener(new ITraceListener() {

			@Override
			public void trace(String traceType, String traceMessage) {
				System.out.println(traceType + ": " + traceMessage);
			}
		});

		Folder.bind(exchangeService, WellKnownFolderName.Inbox);

		serverInfo = exchangeService.getServerInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.schakko.confluence.dao.impl.ExchangeDao#findFolder(java.lang.String)
	 */
	@Override
	public FolderId findFolder(String path) throws Exception {
		FolderId folderId = findPublicFolder(new FolderId(
				WellKnownFolderName.PublicFoldersRoot),
				sanitizePathComponents(path), 0);

		return folderId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.schakko.confluence.dao.impl.ExchangeDao#findContacts(microsoft.exchange
	 * .webservices.data.FolderId)
	 */
	@Override
	public List<Contact> findContacts(FolderId folderId) throws Exception {
		List<Contact> r = new ArrayList<Contact>();
		ContactsFolder remoteFolder = ContactsFolder.bind(exchangeService,
				folderId);

		FindItemsResults<Item> contacts = null;

		contacts = remoteFolder.findItems(new ItemView(Integer.MAX_VALUE));

		for (Item item : contacts) {
			if (item instanceof Contact) {
				r.add((Contact) item);
			}
		}
		
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.schakko.confluence.dao.impl.ExchangeDao#findPublicFolder(microsoft
	 * .exchange.webservices.data.FolderId, java.util.ArrayList, int)
	 */
	@Override
	public FolderId findPublicFolder(FolderId parentFolderId,
			ArrayList<String> pathComponents, int currentIdx) throws Exception {
		FolderView fv = new FolderView(Integer.MAX_VALUE);
		fv.setTraversal(FolderTraversal.Shallow);
		fv.setPropertySet(PropertySet.FirstClassProperties);

		FindFoldersResults results = exchangeService.findFolders(
				parentFolderId, fv);

		for (Folder folder : results.getFolders()) {
			System.out.println(currentIdx + " :: Folder: "
					+ folder.getDisplayName() + " / UID: " + folder.getId());
			// subfolder found
			if (folder.getDisplayName().equals(pathComponents.get(currentIdx))) {
				// last element => our subfolder
				if (pathComponents.size() == (currentIdx + 1)) {
					System.out.println("Last element in stack!");
					return folder.getId();
				} else {
					return findPublicFolder(folder.getId(), pathComponents,
							++currentIdx);
				}
			}
		}

		return null;
	}

	/**
	 * Returns the server information
	 * 
	 * @return
	 */
	public ExchangeServerInfo getServerInfo() {
		return serverInfo;
	}
}
