package mil.nga.giat.geowave.services.rest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import mil.nga.giat.geowave.accumulo.metadata.AccumuloAdapterStore;
import mil.nga.giat.geowave.accumulo.AccumuloDataStore;
import mil.nga.giat.geowave.accumulo.metadata.AccumuloIndexStore;
import mil.nga.giat.geowave.vector.adapter.FeatureDataAdapter;
import mil.nga.giat.geowave.services.rest.data.DatastoreEncoder;
import mil.nga.giat.geowave.services.rest.data.FeatureTypeEncoder;
import mil.nga.giat.geowave.services.rest.data.GeoserverPublisher;
import mil.nga.giat.geowave.services.rest.data.GeoserverReader;
import mil.nga.giat.geowave.services.rest.data.HttpUtils;
import mil.nga.giat.geowave.store.adapter.AdapterStore;
import mil.nga.giat.geowave.store.adapter.DataAdapter;
import mil.nga.giat.geowave.store.index.Index;
import mil.nga.giat.geowave.store.index.IndexStore;
import mil.nga.giat.geowave.utils.GeowaveUtils;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.opengis.feature.simple.SimpleFeatureType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * Geoserver facades with default GeoWave configuration to
 * 		publish data stores
 * 		publish layers
 * 		get/set styles
 * 		list GeoWave data stores, with zookeepers, accumulo instance and namespace of each
 * 		list all GeoWave layers, and list layers by namespace
 *
 */

@Path("/services")
public interface Services
{

	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/geowaveNamespaces")
	public String getGeowaveNamespaces();

	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/geowaveLayers")
	public String getGeowaveLayers();
	
	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/geowaveLayers/{namespace}")
	public String getGeowaveLayers(@PathParam("namespace")String namespace);

	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/getGeowaveDatastores")
	public String getGeowaveDatastores();

	@POST
	@Path("/publishDataStore")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response publishDataStore(MultivaluedMap<String, String> parameter);

	@POST
	@Path("/publishDataStore/{namespace}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response publishDataStore(@PathParam("namespace")String namespace);

	@POST
	@Path("/publishLayer")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response publishLayer(MultivaluedMap<String, String> parameter);

	@POST
	@Path("/publishLayer/{dataStore}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response publishLayer(@PathParam("dataStore")String dataStore, @QueryParam("name")String name);

	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/getStyles")
	public String getStyles();

	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/getStyles/{styleName}")
	public String getStyle(@PathParam("styleName")String styleName);

	@POST
	@Path("/publishStyle")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public boolean publishStyle(@Context HttpServletRequest request);
	
	public boolean publishStyle(String styleName, File sld);
	
	@PUT
	@Path("/updateStyle")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public boolean updateStyle(@Context HttpServletRequest request);

	public boolean updateStyle(String styleName, File sld);
}
