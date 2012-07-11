package de.schakko.confluence.macro.exchange.dao;

import de.schakko.confluence.macro.exchange.domain.LocalFolder;

public interface FolderDao {
	public LocalFolder findByName(String name);
	public LocalFolder save(LocalFolder folder);
	public LocalFolder create(String folderName);
}
