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

public class NewOrder extends AsyncTask<String, Void, HttpResponse> {
	private ProgressDialog mProgressDialog = null;
	private final Context mContext;
	private final String URL = "http://soba.cs.man.ac.uk/~sup9/AnRa/php/newOrder.php";
	private String id;

	public NewOrder(Context c) {
		mContext = c;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(mContext, "Please Wait ...",
				"Creating new order ...", true);
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
	}

	@Override
	protected void onPostExecute(HttpResponse r) {
		super.onPostExecute(r);
		mProgressDialog.dismiss();

		if (r != null) {
			BufferedReader br = null;
			String result = null;
			try {
				br = new BufferedReader(new InputStreamReader(r.getEntity()
						.getContent()));
				result = "";
				String s;
				while ((s = br.readLine()) != null) {
					result += s;
				}
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (final IOException e) {
						e.printStackTrace();
					} // catch
				}// if
			}// finally
			id = result;
		}// if
	}
	
	protected String getID() {
		return id;
	}

}
