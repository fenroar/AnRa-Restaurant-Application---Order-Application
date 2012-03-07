package project.AnRa.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class InitialiseSpinner extends AsyncTask<String, Void, HttpResponse> {
	private final Spinner spinner;
	private final Context mContext;

	private ProgressDialog mProgressDialog = null;

	public InitialiseSpinner(Spinner s, Context c) {
		// TODO Auto-generated constructor stub
		spinner = s;
		mContext = c;

	}// Constructor

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}// OnPreExecute

	@Override
	protected HttpResponse doInBackground(String... params) {
		try {
			if (params.length >= 1) {
				final HttpGet httpgetaddress = new HttpGet(params[0]);
				// Default Initialization starts here
				final HttpClient httpclient = new DefaultHttpClient();
				try {
					return httpclient.execute(httpgetaddress);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return null;

	}// doInBackground

	@Override
	protected void onPostExecute(HttpResponse result) {
		JsonArray jsonArray = null;
		mProgressDialog.dismiss();
		try {
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
					jsonArray = new JsonParser().parse(json).getAsJsonArray();
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
			}

			final String[] array_spinner = new String[jsonArray.size()];

			int i = 0;
			for (final JsonElement je : jsonArray) {
				final JsonObject jo = je.getAsJsonObject();
				final String name = jo.getAsJsonPrimitive("name").getAsString();
				array_spinner[i] = name;
				i++;
			}// for

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
					android.R.layout.simple_spinner_item, array_spinner);

			adapter.setDropDownViewResource(R.layout.spinner);

			spinner.setAdapter(adapter);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		super.onPostExecute(result);

	}// onPostExecute

}// InitialiseSpinner