package mil.nga.giat.geowave.services.rest.ingest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import mil.nga.giat.geowave.ingest.hdfs.StageToHdfsDriver;
import mil.nga.giat.geowave.services.rest.Services;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class StageToHdfs
{
	private StageToHdfsDriver driver;
	private String hdfs;
	private String hdfsbase;
	
	public StageToHdfs() throws IOException {
		loadProperties();
		
		driver = new StageToHdfsDriver("hdfsstage");
	}

	public void run(String basePath) throws ParseException {
		Options options = new Options();
		driver.applyOptions(options);
		
		ArrayList<String> args = new ArrayList<String>();
		args.add("--base");
		args.add(basePath);
		args.add("-hdfs");
		args.add(hdfs);
		args.add("-hdfsbase");
		args.add(hdfsbase);
		
		String[] arguments = args.toArray(new String [] {});
		CommandLine commandLine = new BasicParser().parse(options, arguments);
		driver.parseOptions(commandLine);

		driver.run(arguments);
	}
	
	private void loadProperties() throws IOException {
		// load ingest geowave properties
		Properties prop = new Properties();
		String propFileName = "mil/nga/giat/geowave/webservices/ingest/config.properties";
		InputStream inputStream = Services.class.getClassLoader().getResourceAsStream(propFileName);
		if (inputStream == null)
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		prop.load(inputStream);

		hdfs = prop.getProperty("hdfs");
		hdfsbase = prop.getProperty("hdfsbase");
	}
}
