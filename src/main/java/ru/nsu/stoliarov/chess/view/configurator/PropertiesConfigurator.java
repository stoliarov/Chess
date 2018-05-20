package ru.nsu.stoliarov.chess.view.configurator;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Class to work with properties file
 */
public class PropertiesConfigurator {
	private static final Logger logger = LogManager.getLogger(PropertiesConfigurator.class.getName());
	
	/**
	 * Open properties file and return java.util.Properties object
	 *
	 * @param pathToProperties path to opening properties file
	 * @return java.util.Properties object or null if open file failed
	 * (for example if incorrect path to properties)
	 */
	public static java.util.Properties getProperties(String pathToProperties) {
		java.util.Properties properties = new java.util.Properties();
		try {
			try {
				Class classObj = Class.forName(PropertiesConfigurator.class.getName());
				ClassLoader loader = classObj.getClassLoader();
				
				java.io.InputStream inStream = loader.getResourceAsStream(pathToProperties);
				if (null == inStream) {
					logger.error("Not found this properties (wrong path): '" +
							pathToProperties + "'");
					return null;
				}
				properties.load(inStream);
				return properties;
			} catch (ClassNotFoundException exception) {
				logger.error("Class nat found: '" + PropertiesConfigurator.class.getName() + "'");
				return null;
			}
		} catch (java.io.IOException exception) {
			logger.error("File not found: " + pathToProperties);
			return null;
		}
	}
}

