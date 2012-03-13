package project.AnRa.Order;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class InitialiseMenu extends AsyncTask<String, Void, HttpResponse> {
	private static final String URL = "http://soba.cs.man.ac.uk/~sup9/AnRa/php/getMenuList.php";
	private HashMap<String, String[]> menu = new HashMap<String, String[]>();
	private ProgressDialog mProgressDialog = null;
	private final Context mContext;

	// Constructor to get spinner
	public InitialiseMenu(Context c) {
		mContext = c;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "Please Wait ...",
				"Getting menu ...", true);
		super.onPreExecute();
	}

	@Override
	protected HttpResponse doInBackground(String... params) {
		final HttpGet httpgetaddress = new HttpGet(URL);
		// Default Initialization starts here
		final HttpClient httpclient = new DefaultHttpClient();

		HttpResponse result = null;
		try {
			result = httpclient.execute(httpgetaddress);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}// doInBackground

	@Override
	protected void onPostExecute(HttpResponse result) {
		// JsonArray that stores all the meal types
		JsonElement jsonElement = null;
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		if (result != null) {
			BufferedReader br = null;
			String json;
			try {
				br = new BufferedReader(new InputStreamReader(result
						.getEntity().getContent()));
				json = "";
				String s;
				while ((s = br.readLine()) != null) {
					json += s;
				}
				jsonElement = new JsonParser().parse(json);
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (final IOException e) {
						e.printStackTrace();
					} // catch
				} // if
			} // finally
		}// if

		try {
			final JsonArray jsonArray = jsonElement.getAsJsonArray();
			for (final JsonElement je : jsonArray) {
				final JsonObject jo = je.getAsJsonObject();
				final String name = jo.getAsJsonPrimitive("name").getAsString();
				final String price = jo.getAsJsonPrimitive("price")
						.getAsString();
				final String id = jo.getAsJsonPrimitive("id").getAsString();
				menu.put(name, new String[] { id, price});
			}// for

		} catch (IllegalStateException e) {
			e.printStackTrace();
		}// catch
	}
	
	protected HashMap<String, String[]> getMenu() {
		return menu;
	}	
	

}