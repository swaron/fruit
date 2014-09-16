package me.suisui.web.support.uadetector;

import java.net.URL;

import net.sf.uadetector.datareader.DataReader;
import net.sf.uadetector.datareader.XmlDataReader;
import net.sf.uadetector.datastore.AbstractDataStore;

public  final class ResourceModuleXmlDataStore extends AbstractDataStore {

	/**
	 * The default data reader to read in <em>UAS data</em> in XML format
	 */
	private static final DataReader DEFAULT_DATA_READER = new XmlDataReader();

	/**
	 * Path where the UAS data file is stored for the {@code ClassLoader}
	 */
	private static final String PATH = "net/sf/uadetector/resources";

	/**
	 * {@link URL} to the UAS data delivered in this module
	 */
	public static final URL UAS_DATA = ResourceModuleXmlDataStore.class.getClassLoader().getResource(PATH + "/uas.xml");

	/**
	 * {@link URL} to the version information of the delivered UAS data in this module
	 */
	public static final URL UAS_VERSION = ResourceModuleXmlDataStore.class.getClassLoader().getResource(PATH + "/uas.version");

	/**
	 * Constructs an {@code ResourceModuleXmlDataStore} by reading <em>UAS data</em> from the specified URL
	 * {@link #UAS_DATA} (in XML format).
	 */
	public ResourceModuleXmlDataStore() {
		super(DEFAULT_DATA_READER, UAS_DATA, UAS_VERSION, DEFAULT_CHARSET);
	}

}