package mil.nga.giat.geowave.services.rest.ingest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import mil.nga.giat.geowave.ingest.ClearNamespaceDriver;
import mil.nga.giat.geowave.services.rest.Services;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ClearNamespace
{
	private static String zookeeperUrl;
	private static String instanceName;
	private static String geowaveUsername;
	private static String geowavePassword;
	private ClearNamespaceDriver driver;
	
	public ClearNamespace() throws IOException {
		loadProperties();

		driver = new ClearNamespaceDriver(
				"clear");
	}
	
	public void run() throws ParseException {
		run(null, null);
	}
	
	public void run(String namespace) throws ParseException {
		run(namespace, null);
	}
	
	public void run(String namespace, String visibility) throws ParseException {
		Options options = new Options();
		driver.applyOptions(options);
		
		ArrayList<String> args = new ArrayList<String>();
		args.add("--zookeepers");
		args.add(zookeeperUrl);
		if (namespace != null && namespace.trim().length() > 0) {
			args.add("--namespace");
			args.add(namespace);
		}
		args.add("--user");
		args.add(geowaveUsername);
		args.add("--password");
		args.add(geowavePassword);
		args.add("--instance-id");
		args.add(instanceName);
		if (visibility != null && visibility.trim().length() > 0) {
			args.add("--visibility");
			args.add(visibility);
		}
		
		String[] arguments = args.toArray(new String [] {});
		CommandLine commandLine = new BasicParser().parse(options, arguments);
		driver.parseOptions(commandLine);

		driver.run(arguments);
	}
	
	private static void loadProperties() throws IOException {
		// load geowave properties
		Properties prop = new Properties();
		String propFileName = "mil/nga/giat/geowave/utils/config.properties";
		InputStream inputStream = Services.class.getClassLoader().getResourceAsStream(propFileName);
		if (inputStream == null)
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		prop.load(inputStream);

		zookeeperUrl = prop.getProperty("zookeeperUrl");
		instanceName = prop.getProperty("instanceName");
		geowaveUsername = prop.getProperty("geowave_username");
		geowavePassword = prop.getProperty("geowave_password");
	}
}
