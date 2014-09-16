package me.suisui.web.support.uadetector;

import java.util.concurrent.TimeUnit;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.parser.UserAgentStringParserImpl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class CachedUserAgentStringParser implements UserAgentStringParser {
	private ResourceModuleXmlDataStore RESOURCE_MODULE = new ResourceModuleXmlDataStore();
	private final UserAgentStringParser parser = new UserAgentStringParserImpl<ResourceModuleXmlDataStore>(
			RESOURCE_MODULE);
	private final Cache<String, ReadableUserAgent> cache = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterWrite(1, TimeUnit.DAYS).build();

	@Override
	public String getDataVersion() {
		return parser.getDataVersion();
	}

	@Override
	public ReadableUserAgent parse(final String userAgentString) {
		ReadableUserAgent result = cache.getIfPresent(userAgentString);
		if (result == null) {
			result = parser.parse(userAgentString);
			cache.put(userAgentString, result);
		}
		return result;
	}

	@Override
	public void shutdown() {
		parser.shutdown();
	}

}