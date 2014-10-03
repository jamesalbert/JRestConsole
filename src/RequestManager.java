import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestManager {
	private final String UA = "Chrome";
	HttpURLConnection conn;

	public boolean isHtml(String html) {
		return html.matches(".*\\<[^>]+>.*");
	}

	public boolean isJson(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException ex) {
			// edited, to include @Arthur's comment
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(json);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	public String jsonFormatter(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object jsonfmt = mapper.readValue(json, Object.class);
			String ret = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(jsonfmt);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String[] send(String urlstr, String reqtype, String params,
			Map<String, String> opts) throws MalformedURLException, IOException {
		String reqcode = "unknown";
		try {
			// initiate connection
			URL url = new URL(urlstr);
			if (urlstr.contains("https")) {
				conn = (HttpsURLConnection) url.openConnection();
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}

			// connection settings
			conn.setRequestMethod(reqtype);
			conn.setRequestProperty("User-Agent", UA);
			conn.setRequestProperty("Accept", opts.get("accept"));
			int timeout;
			if (opts.get("timeout").isEmpty()) {
				timeout = 60;
			} else {
				timeout = Integer.parseInt(opts.get("timeout"));
			}
			conn.setConnectTimeout(timeout * 1000);
			if (reqtype.contains("POST") || reqtype.contains("PUT")) {
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/json");
				DataOutputStream wr = new DataOutputStream(
						conn.getOutputStream());
				wr.writeBytes(params);
				wr.flush();
				wr.close();
			}

			// evaluate response
			reqcode = String.valueOf(conn.getResponseCode());
			InputStream stream;
			if (!reqcode.contains("200")) {
				stream = conn.getErrorStream();
			} else {
				stream = conn.getInputStream();
			}
			// return response
			BufferedReader in = new BufferedReader(
					new InputStreamReader(stream));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String[] ret = { response.toString(), reqcode };
			return ret;
		} catch (MalformedURLException e) {
			System.out.println("error");
			return new String[] { "bad url" };
		} catch (IOException e) {
			String err = e.toString();
			System.out.println(err);
			if (err.contains("Connection refused")) {
				return new String[] { "{\"error\": \"Connection refused\"}" };
			} else if (err.contains("connect timed out")) {
				return new String[] { "{\"error\": \"Connection timed out\"}" };
			}
			return new String[] { "error" };
		}

	}
}