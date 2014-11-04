package mil.nga.giat.geowave.services.clients;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import mil.nga.giat.geowave.services.InfoService;

import org.glassfish.jersey.client.proxy.WebResourceFactory;

public class InfoServiceClient
{

	private final InfoService infoService;

	public InfoServiceClient(
			final String baseUrl ) {
		infoService = WebResourceFactory.newResource(
				InfoService.class,
				ClientBuilder.newClient().target(
						baseUrl));
	}

	public String[] getNamespaces() {
		final Response resp = infoService.getNamespaces();
		resp.bufferEntity();
		return resp.readEntity(String[].class);
	}

	public String[] getIndices(
			final String namespace ) {
		final Response resp = infoService.getIndices(namespace);
		resp.bufferEntity();
		return resp.readEntity(String[].class);
	}

	public String[] getAdapters(
			final String namespace ) {
		final Response resp = infoService.getAdapters(namespace);
		resp.bufferEntity();
		return resp.readEntity(String[].class);
	}
}
