package mil.nga.giat.geowave.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mil.nga.giat.geowave.ingest.AbstractCommandLineDriver;
import mil.nga.giat.geowave.ingest.OperationCommandLineOptions.Operation;
import mil.nga.giat.geowave.services.IngestService;
import mil.nga.giat.geowave.services.utils.ServiceUtils;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.google.common.io.Files;

@Produces(MediaType.APPLICATION_JSON)
@Path("/ingest")
public class IngestServiceImpl implements
		IngestService
{
	private final String zookeeperUrl;
	private final String instance;
	private final String username;
	private final String password;
	private final String hdfs;
	private final String hdfsBase;
	private final String jobTracker;

	public IngestServiceImpl(
			@Context final ServletConfig servletConfig ) {
		final Properties props = ServiceUtils.loadProperties(servletConfig.getServletContext().getResourceAsStream(
				servletConfig.getInitParameter("config.properties")));

		zookeeperUrl = ServiceUtils.getProperty(
				props,
				"zookeeper.url");

		instance = ServiceUtils.getProperty(
				props,
				"zookeeper.instance");

		username = ServiceUtils.getProperty(
				props,
				"zookeeper.username");

		password = ServiceUtils.getProperty(
				props,
				"zookeeper.password");

		hdfs = ServiceUtils.getProperty(
				props,
				"hdfs");

		hdfsBase = ServiceUtils.getProperty(
				props,
				"hdfsBase");

		jobTracker = ServiceUtils.getProperty(
				props,
				"jobTracker");
	}

	@Override
	public Response localIngest(
			final FormDataMultiPart multiPart ) {
		ingest(
				Operation.LOCAL_INGEST,
				multiPart);
		return Response.ok().build();
	}

	@Override
	public Response hdfsIngest(
			final FormDataMultiPart multiPart ) {
		ingest(
				Operation.INGEST_FROM_HDFS,
				multiPart);
		return Response.ok().build();
	}

	private void ingest(
			final Operation operation,
			final FormDataMultiPart multiPart ) {

		// read the list of files
		final List<FormDataBodyPart> fields = multiPart.getFields("file");
		final Map<String, InputStream> fileMap = new HashMap<String, InputStream>();
		for (final FormDataBodyPart field : fields) {
			fileMap.put(
					field.getFormDataContentDisposition().getFileName(),
					field.getValueAs(InputStream.class));
		}

		final String namespace = multiPart.getField(
				"namespace").getValue();
		final String visibility = multiPart.getField(
				"visibility").getValue();

		if ((namespace == null) || namespace.isEmpty()) {
			throw new WebApplicationException(
					Response.status(
							Status.BAD_REQUEST).entity(
							"Ingest Failed - Missing Namespace").build());
		}

		final File baseDir = Files.createTempDir();

		final Set<String> filenames = fileMap.keySet();
		for (final String filename : filenames) {
			final File tempFile = new File(
					baseDir,
					filename);

			// read the file
			try (OutputStream fileOutputStream = new FileOutputStream(
					tempFile)) {

				final InputStream inStream = fileMap.get(filename);

				int read = 0;
				final byte[] bytes = new byte[1024];
				while ((read = inStream.read(bytes)) != -1) {
					fileOutputStream.write(
							bytes,
							0,
							read);
				}
			}
			catch (final IOException e) {
				throw new WebApplicationException(
						Response.status(
								Status.INTERNAL_SERVER_ERROR).entity(
								"Ingest Failed" + e.getMessage()).build());
			}
		}

		// ingest the files
		try {
			if (operation == Operation.LOCAL_INGEST) {
				runLocalIngest(
						baseDir,
						namespace,
						visibility);
			}
			else if (operation == Operation.LOCAL_TO_HDFS_INGEST) {
				runHdfsIngest(
						baseDir,
						namespace,
						visibility);
			}
		}
		catch (final ParseException e) {
			throw new WebApplicationException(
					Response.status(
							Status.INTERNAL_SERVER_ERROR).entity(
							"Ingest Failed" + e.getMessage()).build());
		}
	}

	private void runLocalIngest(
			final File baseDir,
			final String namespace,
			final String visibility )
			throws ParseException {
		final AbstractCommandLineDriver driver = Operation.LOCAL_INGEST.getDriver();

		final ArrayList<String> args = new ArrayList<String>();
		args.add("--base");
		args.add(baseDir.getAbsolutePath());
		args.add("--zookeepers");
		args.add(zookeeperUrl);
		args.add("--namespace");
		args.add(namespace);
		args.add("--user");
		args.add(username);
		args.add("--password");
		args.add(password);
		args.add("--instance-id");
		args.add(instance);
		if ((visibility != null) && !visibility.isEmpty()) {
			args.add("--visibility");
			args.add(visibility);
		}

		final Options options = new Options();
		driver.applyOptions(options);

		final String[] arguments = args.toArray(new String[] {});
		final CommandLine commandLine = new BasicParser().parse(
				options,
				arguments);
		driver.parseOptions(commandLine);
		driver.run(arguments);
	}

	private void runHdfsIngest(
			final File baseDir,
			final String namespace,
			final String visibility )
			throws ParseException {
		final AbstractCommandLineDriver driver = Operation.LOCAL_TO_HDFS_INGEST.getDriver();

		final ArrayList<String> args = new ArrayList<String>();
		args.add("--base");
		args.add(baseDir.getAbsolutePath());
		args.add("--zookeepers");
		args.add(zookeeperUrl);
		args.add("--namespace");
		args.add(namespace);
		args.add("--user");
		args.add(username);
		args.add("--password");
		args.add(password);
		args.add("--instance-id");
		args.add(instance);
		if ((visibility != null) && !visibility.isEmpty()) {
			args.add("--visibility");
			args.add(visibility);
		}
		args.add("-hdfs");
		args.add(hdfs);
		args.add("-hdfsbase");
		args.add(hdfsBase);
		args.add("-jobtracker");
		args.add(jobTracker);

		final Options options = new Options();
		driver.applyOptions(options);

		final String[] arguments = args.toArray(new String[] {});
		final CommandLine commandLine = new BasicParser().parse(
				options,
				arguments);
		driver.parseOptions(commandLine);
		driver.run(arguments);
	}

}