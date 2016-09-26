package week5_apiheuristic;

import java.net.URI;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

public class APIUtils {

	//Allowed methods for API Service
	public static enum HTTPMethods {
		GET, //Get HTTP method
		POST //Post HTTP method
	}
	
	/**
	 * Add headers to the current request
	 * @param r current request
	 * @param headers headers to add
	 */
	public static void addHeaders(HttpRequestBase r, List<NameValuePair> headers) {
		headers.stream()
		.forEach(h -> r.addHeader(h.getName(), h.getValue()));
	}
	
	/**
	 * Map HTTP request type with Apache HttpClient classes
	 * @param m HTTPMethod to execute
	 * @return the Apace HttpClient proper class
	 */
	public static HttpRequestBase craftRequest(HTTPMethods m, URI req) {
		switch(m) {
			case POST: return new HttpPost(req);
			default: return new HttpGet(req);
		}
	}
	
}
