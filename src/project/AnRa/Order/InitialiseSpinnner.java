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

public class InitialiseSpinnner extends
		AsyncTask<String, Void, ArrayAdapter<String>> {
	private ProgressDialog mProgressDialog = null;
	private final Spinner mSpinner;
	private final Context mContext;
	private ArrayAdapter<String> mAdapter;

	// Constructor to get spinner
	public InitialiseSpinnner(Context c, Spinner s) {
		mContext = c;
		mSpinner = s;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "Please Wait ...",
				"Getting menu ...", true);
		super.onPreExecute();
	}

	@Override
	protected ArrayAdapter<String> doInBackground(String... params) {
		// JsonArray that stores all the meal types
		JsonElement jsonElement = null;
		final HttpGet httpgetaddress = new HttpGet(params[0]);
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
			final String[] array_spinner = new String[jsonArray.size()];

			int i = 0;
			for (final JsonElement je : jsonArray) {
				final JsonObject jo = je.getAsJsonObject();
				final String name = jo.getAsJsonPrimitive(params[1])
						.getAsString();
				array_spinner[i] = name;
				i++;
			}// for

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
					android.R.layout.simple_spinner_item, array_spinner);

			adapter.setDropDownViewResource(R.layout.spinner);
			return adapter;

		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		}// catch
	}// doInBackground

	@Override
	protected void onPostExecute(ArrayAdapter<String> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		if (result != null) {
			mAdapter = result;
			mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinner.setAdapter(mAdapter);
		}
	}

}