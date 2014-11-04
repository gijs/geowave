package mil.nga.giat.geowave.services.rest.ingest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import mil.nga.giat.geowave.ingest.local.LocalFileIngestDriver;
import mil.nga.giat.geowave.services.rest.Services;

public class LocalIngest
{
	private LocalFileIngestDriver driver;
	private String zookeeperUrl;
	private String geowaveUsername;
	private String geowavePassword;
	private String instanceName;

	public LocalIngest() throws IOException {
		loadProperties();
		
		driver = new LocalFileIngestDriver("localingest");
	}

	public void run(String basePath, String namespace) throws ParseException {
		run(basePath, namespace, null);
	}
	
	public void run(String basePath, String namespace, String visibility) throws ParseException {
		run(basePath, namespace, visibility, false);
	}

	public void run(String basePath, String namespace, String visibility, boolean clear) throws ParseException {
		Options options = new Options();
		driver.applyOptions(options);
		
		ArrayList<String> args = new ArrayList<String>();
		args.add("--base");
		args.add(basePath);
		args.add("--zookeepers");
		args.add(zookeeperUrl);
		args.add("--namespace");
		args.add(namespace);
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
		if (clear)
			args.add("--clear");

		String[] arguments = args.toArray(new String [] {});
		CommandLine commandLine = new BasicParser().parse(options, arguments);
		driver.parseOptions(commandLine);

		driver.run(arguments);
	}
	
	private void loadProperties() throws IOException {
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
