package de.schakko.confluence.macro.exchange.dao;

import de.schakko.confluence.macro.exchange.domain.Configuration;

public interface ConfigurationDao {
	public Configuration save(Configuration configuration);

	public Configuration get();
}
