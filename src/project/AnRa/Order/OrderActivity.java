package project.AnRa.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OrderActivity extends Activity {
	private ArrayList<Meal> mealList = new ArrayList<Meal>();
	private ArrayAdapter<Meal> mealAdapter;
	private MealAdapter mAdapter;
	private Meal meal;
	private HashMap<String, String> menu = new HashMap<String, String>();
	private HashMap<String, BigDecimal> sidePrices = new HashMap<String, BigDecimal>();
	private BigDecimal basePrice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);

		//sets up values for sides and gets base price
		setSidePrices();
		new GetBasePrice(this).execute();

		// Sets up adapter for list of items being ordered
		mAdapter = new MealAdapter(this, R.layout.row, mealList);
		final ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(mAdapter);

		final Button addButton = (Button) findViewById(R.id.add_button);
		final Button checkoutButton = (Button) findViewById(R.id.checkout_button);
		final Spinner mealSpinner = (Spinner) this
				.findViewById(R.id.meal_spinner);
		final Spinner sideSpinner = (Spinner) findViewById(R.id.side_spinner);

		// Initialising adapters to populate spinner
		// Iinitialising mealSpinner adapter
		new InitialiseMealSpinnner(mealSpinner).execute();

		// Initialising sideSpinner adapter
		ArrayAdapter<CharSequence> sideAdapter = ArrayAdapter
				.createFromResource(OrderActivity.this, R.array.side_array,
						android.R.layout.simple_spinner_item);
		sideAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sideSpinner.setAdapter(sideAdapter);

		// addButton onClickListener
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(mealSpinner.getSelectedItem() == null)) {
					Double priceInDouble = Double
							.parseDouble(((Meal) mealSpinner.getSelectedItem())
									.getMealPrice());
					Log.e("PriceInDouble", "" + priceInDouble);

					BigDecimal totalPrice = sidePrices.get(
							sideSpinner.getSelectedItem().toString()).add(
							BigDecimal.valueOf(priceInDouble)).add(basePrice);

					Log.e("totalPrice", "" + totalPrice);
					DecimalFormat decim = new DecimalFormat("0.00");
					String s = decim.format(totalPrice);
					Log.e("s", "s is '" + s + "'");

					meal = new Meal(((Meal) mealSpinner.getSelectedItem())
							.getMealName()
							+ " "
							+ sideSpinner.getSelectedItem().toString(), "" + s);
					mealList.add(meal);
					mAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(OrderActivity.this, "No meals in database",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// checkoutButton onClickListener
		checkoutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// passes values into new activity
				Log.e("Order", "Check out");
			}
		});

		// listView onClickListener
		lv.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v,
					final int position, long l) {

				try {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								// Add notes button clicked

								final EditText input = new EditText(
										OrderActivity.this);
								new AlertDialog.Builder(OrderActivity.this)
										.setTitle("Update Status")
										.setMessage(
												"Please input any extra details: ")
										.setView(input)
										.setPositiveButton(
												"Ok",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
														String value = input
																.getText()
																.toString();

														// makes new Meal Object
														// with current meal object's data
														// plus extra notes
														mealList.set(
																position,
																new Meal(
																		mealList.get(
																				position)
																				.getMealName(),
																		mealList.get(
																				position)
																				.getMealPrice(),
																		value));

														mAdapter.notifyDataSetChanged();

													}
												})
										.setNegativeButton(
												"Cancel",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
														// Do nothing.
													}
												}).show();

								break;

							case DialogInterface.BUTTON_NEGATIVE:
								// Delete button clicked
								// Remembers the selected Index
								Log.e("Order", mealList.get(position)
										.getMealName() + " deleted");
								mealList.remove(position);
								mAdapter.notifyDataSetChanged();
								break;

							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							OrderActivity.this);
					builder.setMessage(
							"What do you want to do with "
									+ mealList.get(position).getMealName()
									+ "? \n"
									+ "Extra notes: "
									+ mealList.get(position)
											.getMealExtraNotes())
							.setPositiveButton("Add Notes", dialogClickListener)
							.setNegativeButton("Delete", dialogClickListener)
							.show();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.order_menu, menu);
		return true;
	}// onCreateOptionsMenu

	// Menu button to allow user to be able to clear all menu items currently in
	// the meallist
	// Useful is customer decides to cancel all his order while in the middle of
	// ordering
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		switch (item.getItemId()) {
		case R.id.clear:
			mealList.clear();
			mAdapter.notifyDataSetChanged();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}// switch
		return true;

	}// onOptionsItemSelected

	private class InitialiseMealSpinnner extends
			AsyncTask<String, Void, ArrayAdapter<Meal>> {
		private static final String URL = "http://soba.cs.man.ac.uk/~sup9/AnRa/php/getMenuList.php";
		private ProgressDialog mProgressDialog = null;
		private final Spinner spinner;

		// Constructor to get spinner
		public InitialiseMealSpinnner(Spinner s) {
			spinner = s;
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(OrderActivity.this,
					"Please Wait ...", "Getting menu ...", true);
			super.onPreExecute();
		}

		@Override
		protected ArrayAdapter<Meal> doInBackground(String... params) {
			// JsonArray that stores all the meal types
			JsonElement jsonElement = null;
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
				final Meal[] array_spinner = new Meal[jsonArray.size()];

				int i = 0;
				for (final JsonElement je : jsonArray) {
					final JsonObject jo = je.getAsJsonObject();
					final String name = jo.getAsJsonPrimitive("name")
							.getAsString();
					final String price = jo.getAsJsonPrimitive("price")
							.getAsString();
					menu.put(name, price);
					array_spinner[i] = new Meal(name, price);
					i++;
				}// for

				ArrayAdapter<Meal> adapter = new ArrayAdapter<Meal>(
						OrderActivity.this,
						android.R.layout.simple_spinner_item, array_spinner);

				adapter.setDropDownViewResource(R.layout.spinner);
				return adapter;

			} catch (IllegalStateException e) {
				e.printStackTrace();
				return null;
			}// catch
		}// doInBackground

		@Override
		protected void onPostExecute(ArrayAdapter<Meal> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			mealAdapter = result;
			mealAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(mealAdapter);
		}

	}

	public void setSidePrices() {
		sidePrices.put("Boiled Rice", BigDecimal.valueOf(0.00));
		sidePrices.put("Chips", BigDecimal.valueOf(0.00));
		sidePrices.put("Fried Rice", BigDecimal.valueOf(0.50));
		sidePrices.put("Boiled Rice and Chips", BigDecimal.valueOf(0.0));
		sidePrices.put("Fried Rice and Chips", BigDecimal.valueOf(0.50));
		sidePrices.put("Salt and Pepper Chips", BigDecimal.valueOf(1.00));
		sidePrices.put("Chow Mein", BigDecimal.valueOf(0.70));
	}
	
	
	//gets base price of meals
	private final class GetBasePrice extends BasePrice {
		public GetBasePrice(Context c) {
			super(c);
		}//Constructor

		@Override
		protected void onPostExecute(HttpResponse r) {
			super.onPostExecute(r);
			basePrice = getPrice();
		}//onPostExecute
	}// getBasePrice
}
