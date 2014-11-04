package mil.nga.giat.geowave.services.test;

import java.io.File;
import java.io.IOException;

import mil.nga.giat.geowave.services.clients.GeoserverServiceClient;
import mil.nga.giat.geowave.services.clients.InfoServiceClient;
import mil.nga.giat.geowave.services.clients.IngestServiceClient;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

public class ServicesIT
{
	private static InfoServiceClient infoServiceClient;
	private static GeoserverServiceClient geoserverServiceClient;
	private static IngestServiceClient ingestServiceClient;

	@BeforeClass
	public static void startClient() {
		final String baseUrl = "http://localhost:8080/geowave-services/";
		infoServiceClient = new InfoServiceClient(
				baseUrl);
		geoserverServiceClient = new GeoserverServiceClient(
				baseUrl);
		ingestServiceClient = new IngestServiceClient(
				baseUrl);
	}

	@Test
	public void InfoServiceTest() {

		System.out.println("InfoService.getAdapters");
		final String[] adapters = infoServiceClient.getAdapters("featureTest_vector");
		for (final String adapter : adapters) {
			System.out.println("  " + adapter);
		}

		System.out.println("InfoService.getIndices");
		final String[] indices = infoServiceClient.getIndices("featureTest_vector");
		for (final String index : indices) {
			System.out.println("  " + index);
		}

		System.out.println("InfoService.getNamespaces");
		final String[] namespaces = infoServiceClient.getNamespaces();
		for (final String namespace : namespaces) {
			System.out.println("  " + namespace);
		}
	}

	@Test
	public void GeoserverServiceTest()
			throws IOException {

		// create the workspace
		System.out.println("GeoserverService.createWorkspace");
		System.out.println("  " + geoserverServiceClient.createWorkspace("geowave"));

		// Style tests
		System.out.println("GeoserverService.publishStyle");
		System.out.println("  " + geoserverServiceClient.publishStyle(new File[] {
			new File(
					"C:/Projects/geowave/geowave-examples/example-slds/DecimatePoints.sld")
		}));

		System.out.println("GeoserverService.getStyles");
		final String[] styles = geoserverServiceClient.getStyles();
		for (final String style : styles) {
			System.out.println("  " + style);
		}

		System.out.println("GeoserverService.getStyle");
		final String style = IOUtils.toString(geoserverServiceClient.getStyle("DecimatePoints"));
		System.out.println("  " + style);

		// Datastore tests
		System.out.println("GeoserverService.publishDatastore");
		System.out.println("  " + geoserverServiceClient.publishDatastore(
				"geowave-master:2181,geowave-node1:2181,geowave-node2:2181",
				"root",
				"geowave",
				"geowave",
				"featureTest_vector"));

		System.out.println("GeoserverService.getDatastores");
		final String[] datastores = geoserverServiceClient.getDatastores();
		for (final String datastore : datastores) {
			System.out.println("  " + datastore);
		}

		System.out.println("GeoserverService.getDatastore");
		final String datastore = IOUtils.toString(geoserverServiceClient.getDatastore("featureTest_vector"));
		System.out.println("  " + datastore);

		// Layer tests
		SimpleFeatureType TYPE = null;
		try {
			TYPE = DataUtilities.createType(
					"TestPoint",
					"location:Point:srid=4326,dim1:Double,dim2:Double,dim3:Double,startTime:Date,stopTime:Date,index:String");
		}
		catch (final SchemaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("GeoserverService.publishLayer");
		System.out.println("  " + geoserverServiceClient.publishLayer(
				"featureTest_vector",
				"DecimatePoints",
				TYPE));

		System.out.println("GeoserverService.getLayers");
		final String[] layers = geoserverServiceClient.getLayers();
		for (final String layer : layers) {
			System.out.println("  " + layer);
		}

		System.out.println("GeoserverService.getLayer");
		final String layer = IOUtils.toString(geoserverServiceClient.getLayer("TestPoint"));
		System.out.println("  " + layer);
	}

	@Test
	public void IngestServiceTest() {

		System.out.println("IngestService.localIngest");
		//ingestServiceClient.localIngest(inputFiles, namespace, visibility);
	}

	public static void main(
			final String[] args )
			throws IOException {
		startClient();

		final ServicesIT servicesIT = new ServicesIT();
		servicesIT.InfoServiceTest();
		servicesIT.GeoserverServiceTest();
	}
}
