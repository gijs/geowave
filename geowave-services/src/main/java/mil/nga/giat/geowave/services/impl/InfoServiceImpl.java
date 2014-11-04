package mil.nga.giat.geowave.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mil.nga.giat.geowave.accumulo.util.ConnectorPool;
import mil.nga.giat.geowave.services.InfoService;
import mil.nga.giat.geowave.services.utils.ServiceUtils;
import mil.nga.giat.geowave.store.adapter.DataAdapter;
import mil.nga.giat.geowave.store.index.Index;
import mil.nga.giat.geowave.utils.GeowaveUtils;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.log4j.Logger;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/info")
public class InfoServiceImpl implements
		InfoService
{
	private final static Logger log = Logger.getLogger(InfoServiceImpl.class);

	private final String zookeeperUrl;
	private final String instance;
	private final String username;
	private final String password;

	private Connector connector;

	public InfoServiceImpl(
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

		try {
			connector = new ConnectorPool().getConnector(
					zookeeperUrl,
					instance,
					username,
					password);
		}
		catch (AccumuloException | AccumuloSecurityException e) {
			log.error(
					"Could not create the Accumulo Connector. ",
					e);
		}
	}

	// lists the namespaces in geowave
	@Override
	public Response getNamespaces() {
		final Collection<String> namespaces = GeowaveUtils.getNamespaces(connector);
		return Response.ok(
				namespaces.toArray(new String[namespaces.size()])).build();
	}

	// lists the indices associated with the given namespace
	@Override
	public Response getIndices(
			@PathParam("namespace") final String namespace ) {
		final List<Index> indices = GeowaveUtils.getIndices(
				connector,
				namespace);
		final List<String> indexNames = new ArrayList<String>();
		for (final Index index : indices) {
			if ((index != null) && (index.getId() != null)) {
				indexNames.add(index.getId().getString());
			}
		}
		return Response.ok(
				indexNames.toArray(new String[indexNames.size()])).build();
	}

	// lists the adapters associated with the given namespace
	@Override
	public Response getAdapters(
			@PathParam("namespace") final String namespace ) {
		final Collection<DataAdapter<?>> dataAdapters = GeowaveUtils.getDataAdapters(
				connector,
				namespace);
		final List<String> dataAdapterNames = new ArrayList<String>();
		for (final DataAdapter<?> dataAdapter : dataAdapters) {
			if ((dataAdapter != null) && (dataAdapter.getAdapterId() != null)) {
				dataAdapterNames.add(dataAdapter.getAdapterId().getString());
			}
		}
		return Response.ok(
				dataAdapterNames.toArray(new String[dataAdapterNames.size()])).build();
	}

}