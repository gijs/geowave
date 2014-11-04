package mil.nga.giat.geowave.services.clients;

import java.io.File;
import java.io.FileNotFoundException;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mil.nga.giat.geowave.services.IngestService;

import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

public class IngestServiceClient
{
	private final IngestService ingestService;

	public IngestServiceClient(
			final String baseUrl ) {
		ingestService = WebResourceFactory.newResource(
				IngestService.class,
				ClientBuilder.newClient().target(
						baseUrl));
	}

	public boolean localIngest(
			final File[] inputFiles,
			final String namespace,
			final String visibility )
			throws FileNotFoundException {
		final FormDataMultiPart multiPart = new FormDataMultiPart();

		for (final File file : inputFiles) {
			multiPart.bodyPart(new FileDataBodyPart(
					"file",
					file));
		}

		multiPart.field(
				"namespace",
				namespace);

		multiPart.field(
				"visibility",
				visibility);
		
		final Response resp = ingestService.localIngest(multiPart);
		return resp.getStatus() == Status.OK.getStatusCode();
	}

	public boolean hdfsIngest(
			final File[] inputFiles,
			final String namespace,
			final String visibility )
			throws FileNotFoundException {
		final FormDataMultiPart multiPart = new FormDataMultiPart();

		for (final File file : inputFiles) {
			multiPart.bodyPart(new FileDataBodyPart(
					"file",
					file));
		}

		multiPart.field(
				"namespace",
				namespace);

		multiPart.field(
				"visibility",
				visibility);

		final Response resp = ingestService.hdfsIngest(multiPart);
		return resp.getStatus() == Status.OK.getStatusCode();
	}
}
