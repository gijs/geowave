package mil.nga.giat.geowave.services.rest.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;

public class HttpUtils
{
	private static final Logger LOGGER = Logger.getLogger(HttpUtils.class);
	
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String PUT = "PUT";
	private static final String DELETE = "DELETE";
	
    public static String decurtSlash(String geoserverURL) {
        if (geoserverURL.endsWith("/")) {
            geoserverURL = decurtSlash(geoserverURL.substring(0, geoserverURL.length() - 1));
        }
        return geoserverURL;
    }

	public static String get(String sUrl, String contentType, String geoserverUsername, String geoserverPassword) {
		StringBuffer output = new StringBuffer();
		
		HttpURLConnection connection = null;
		OutputStream os = null;
	
		try {
			connection = createConnection(sUrl, geoserverUsername, geoserverPassword);
			connection.setRequestMethod(GET);
			connection.setRequestProperty("Accept", contentType);
	
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(connection.getInputStream())));
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				LOGGER.error("Failed [" + sUrl +"]: HTTP error code : " + connection.getResponseCode());
			else {
				LOGGER.info("get [" + sUrl +"]: response: " + connection.getResponseCode());
				String line;
				while ((line = br.readLine()) != null) {
					output.append(line);
				}
			}
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} 
			catch (IOException e) {
				LOGGER.warn(e.getMessage());
			}
	
			if (connection != null)
				connection.disconnect();
		}
	
		return output.toString();
	}

	public static int post(String sUrl, String content, String contentType, String geoserverUsername, String geoserverPassword) {
		int response = Integer.MIN_VALUE;
		
		HttpURLConnection connection = null;
		OutputStream os = null;

		try {
			connection = createConnection(sUrl, geoserverUsername, geoserverPassword);
			connection.setDoOutput(true);
			connection.setRequestMethod(POST);
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Accept", contentType);

			os = connection.getOutputStream();
			os.write(content.getBytes());

			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED)
				LOGGER.error("Failed [" + sUrl +"]: HTTP error code : " + connection.getResponseCode());
			else
				LOGGER.info("post [" + sUrl +"]: response: " + connection.getResponseCode());
			
			response = connection.getResponseCode();
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} 
			catch (IOException e) {
				LOGGER.warn(e.getMessage());
			}

			if (connection != null)
				connection.disconnect();
		}
		return response;
	}
	
	public static int post(String sUrl, File file, String contentType, String geoserverUsername, String geoserverPassword) {
		int response = Integer.MIN_VALUE;
		
		HttpURLConnection connection = null;
		FileInputStream is = null;
		OutputStream os = null;

		try {
			connection = createConnection(sUrl, geoserverUsername, geoserverPassword);
			connection.setDoOutput(true);
			connection.setRequestMethod(POST);
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Accept", contentType);
			connection.setUseCaches(false);

	        is = new FileInputStream(file);
	        os = connection.getOutputStream();

	        // POST
	        // Read bytes until EOF to write
	        byte[] buffer = new byte[4096];
	        int bytes_read;    // How many bytes in buffer
	        while((bytes_read = is.read(buffer)) != -1) {
	            os.write(buffer, 0, bytes_read);
	        }

			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED)
				LOGGER.error("Failed [" + sUrl +"]: HTTP error code : " + connection.getResponseCode());
			else 
				LOGGER.info("post [" + sUrl +"]: response: " + connection.getResponseCode());

			response = connection.getResponseCode();
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {
				LOGGER.warn(e.getMessage());
			}
			
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} 
			catch (IOException e) {
				LOGGER.warn(e.getMessage());
			}

			if (connection != null)
				connection.disconnect();
		}
		return response;
	}

	public static int put(String sUrl, String content, String contentType, String geoserverUsername, String geoserverPassword) {
		int response = Integer.MIN_VALUE;
		
		HttpURLConnection connection = null;
		OutputStream os = null;

		try {
			connection = createConnection(sUrl, geoserverUsername, geoserverPassword);
			connection.setDoOutput(true);
			connection.setRequestMethod(PUT);
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Accept", contentType);

			os = connection.getOutputStream();
			os.write(content.getBytes());

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				LOGGER.error("Failed [" + sUrl +"]: HTTP error code : " + connection.getResponseCode());
			else
				LOGGER.info("put [" + sUrl +"]: response: " + connection.getResponseCode());

			response = connection.getResponseCode();
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} 
			catch (IOException e) {
				LOGGER.warn(e.getMessage());
			}

			if (connection != null)
				connection.disconnect();
		}
		return response;
	}

	public static int put(String sUrl, File file, String contentType, String geoserverUsername, String geoserverPassword) {
		int response = Integer.MIN_VALUE;
		
		HttpURLConnection connection = null;
		FileInputStream is = null;
		OutputStream os = null;

		try {
			connection = createConnection(sUrl, geoserverUsername, geoserverPassword);
			connection.setDoOutput(true);
			connection.setRequestMethod(PUT);
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Accept", contentType);
			connection.setUseCaches(false);

	        is = new FileInputStream(file);
	        os = connection.getOutputStream();

	        // POST
	        // Read bytes until EOF to write
	        byte[] buffer = new byte[4096];
	        int bytes_read;    // How many bytes in buffer
	        while((bytes_read = is.read(buffer)) != -1) {
	            os.write(buffer, 0, bytes_read);
	        }

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				LOGGER.error("Failed [" + sUrl +"]: HTTP error code : " + connection.getResponseCode());
			else
				LOGGER.info("post [" + sUrl +"]: response: " + connection.getResponseCode());

			response = connection.getResponseCode();
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {
				LOGGER.warn(e.getMessage());
			}
			
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} 
			catch (IOException e) {
				LOGGER.warn(e.getMessage());
			}

			if (connection != null)
				connection.disconnect();
		}
		return response;
	}

	public static int delete(String sUrl, String geoserverUsername, String geoserverPassword) {
		int response = Integer.MIN_VALUE;
		
		HttpURLConnection connection = null;

		try {
			connection = createConnection(sUrl, geoserverUsername, geoserverPassword);
			connection.setDoOutput(true);
			connection.setRequestMethod(DELETE);
			connection.connect();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				LOGGER.error("Failed [" + sUrl +"]: HTTP error code : " + connection.getResponseCode());
			else
				LOGGER.info("delete [" + sUrl +"]: response: " + connection.getResponseCode());
			
			response = connection.getResponseCode();
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		finally {
			if (connection != null)
				connection.disconnect();
		}
		return response;
	}
	
	private static HttpURLConnection createConnection(String sUrl, String geoserverUsername, String geoserverPassword) throws IOException {
		URL url = new URL(sUrl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		String userpass = geoserverUsername + ":" + geoserverPassword;
		String basicAuth = "Basic " + new String(Base64.encodeBase64(userpass.getBytes()));
		connection.setRequestProperty ("Authorization", basicAuth);
		
		return connection;
	}
}
