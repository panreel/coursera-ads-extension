package week5_apiheuristic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.xml.internal.stream.Entity;

import week5_apiheuristic.APIUtils.HTTPMethods;

/**
 * Abstract over an API Service
 * @author Nico
 */
public class APIService {
	
	//Base Endpoint
	private String baseURL;
	private String apiProtocol;
	//App Id
	private String appId;
	//App Code
	private String appCode;
	//Encode/decode Json
	JsonParser jsParser;
	
	/**
	 * Create a new API Service
	 * @param configPath path for configuration file
	 * @param jsP the Json parser
	 * @throws FileNotFoundException 
	 * @throws JsonSyntaxException 
	 * @throws JsonIOException 
	 */
	public APIService(String configFile) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		
		jsParser = new JsonParser();
		InputStream in = this.getClass().getResourceAsStream(configFile);
		JsonObject config = (JsonObject) jsParser.parse(new InputStreamReader(in));
		appId = config.get("app_id").getAsString();
		appCode = config.get("app_code").getAsString();
		baseURL = config.get("base_endpoint").getAsString();
		apiProtocol = config.get("api_protocol").getAsString();
		
	};
	
	/**
	 * Getter for app_id
	 * @return the app ID
	 */
	protected String getAppId() {
		return appId;
	}

	/**
	 * Setter for app_id
	 * @param appId the app ID
	 */
	protected void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * Getter for app_code
	 * @return the app code
	 */
	protected String getAppCode() {
		return appCode;
	}

	/**
	 * Setter for app_code
	 * @param appCode the app code
	 */
	protected void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	
	/**
	 * Getter for api_protocol
	 * @return protocol used in the API
	 */
	protected String getApiProtocol() {
		return apiProtocol;
	}

	/**
	 * Setter for api_protocol
	 * @param apiProtocol protocol to be used in the API
	 */
	protected void setApiProtocol(String apiProtocol) {
		this.apiProtocol = apiProtocol;
	}
	
	/**
	 * Perform an API request
	 * @param method the method for the API call
	 * @param endpoint the final endpoint to call
	 * @param params parameters to be passed to the call
	 * @param body body of the request
	 * @return the response JsonObject
	 * @throws URISyntaxException if address of the request is not correct
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	protected JsonObject doAPIRequest(
			HTTPMethods method, 
			String endpoint, 
			List<NameValuePair> query,
			List<NameValuePair> headers,
			String body) 
			throws URISyntaxException, ClientProtocolException, IOException {
		
		HttpClient http = HttpClients.createMinimal();
		//Define base URL
		URI requestedURI = new URIBuilder()
				.setScheme(apiProtocol)
				.setHost(baseURL)
				.setPath(endpoint)
				.addParameters(query)
				.build();
		HttpRequestBase req = APIUtils.craftRequest(method, requestedURI);
		//Add headers if present
		if(headers != null) APIUtils.addHeaders(req, headers);
		//Add body if present and we are posting
		if(body != null && req.getClass() == HttpPost.class) {
			HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
			((HttpPost) req).setEntity(entity);
		}
		
		//Execute and return response wrapped in JSONObject
		return http.execute(req, new JsonResponseHandler());
		
	}

	/**
	 * Response handler for JSON data
	 */
	private class JsonResponseHandler implements ResponseHandler<JsonObject> {

		@Override
		public JsonObject handleResponse(HttpResponse response) 
				throws ClientProtocolException, IOException {
			
			int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? 
                jsParser.parse(EntityUtils.toString(entity)).getAsJsonObject() : null;
            } else {
            	//throw new ClientProtocolException("Unexpected response status: " + status);
            	System.out.println("Unexpected response: " + response.toString());
            	return null;
            }
		}

	}
	
}
