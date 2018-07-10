package de.fmk.kicknrush.models.settings;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.models.AbstractStatusModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author FabianK
 */
public class AppSettingsModel extends AbstractStatusModel {
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider cacheProvider;


	@PostConstruct
	public void init() {

	}
}
