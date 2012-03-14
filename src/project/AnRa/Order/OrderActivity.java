package project.AnRa.Order;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class OrderActivity extends Activity {
	private static final int REQUEST_CODE = 101;
	private static final String URL_1 = "http://soba.cs.man.ac.uk/~sup9/AnRa/php/getAllMealMain.php";
	private static final String URL_2 = "http://soba.cs.man.ac.uk/~sup9/AnRa/php/getAllMealType.php";
	public ArrayList<Meal> mealList = new ArrayList<Meal>();
	private MealAdapter mAdapter;
	// menu: value[0] = id, value[1] = price
	private HashMap<String, String[]> menu = new HashMap<String, String[]>();
	private HashMap<String, BigDecimal> sidePrices = new HashMap<String, BigDecimal>();
	private BigDecimal basePrice;

	// Checks if there is a connection with the Internet/mobile network
	// Check is done in a separate thread form the UI thread
	private class Check extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
			return false;
		}

		@Override
		protected void onCancelled() {

			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			final ListView lv = (ListView) findViewById(R.id.list);

			final Button addButton = (Button) findViewById(R.id.add_button);
			final Button checkoutButton = (Button) findViewById(R.id.checkout_button);
			final Spinner mainSpinner = (Spinner) findViewById(R.id.main_spinner);
			final Spinner typeSpinner = (Spinner) findViewById(R.id.type_spinner);
			final Spinner sideSpinner = (Spinner) findViewById(R.id.side_spinner);

			if (result) {
				// sets up values for sides and gets base price
				setSidePrices();
				new GetBasePrice(OrderActivity.this).execute();

				mAdapter = new MealAdapter(OrderActivity.this, R.layout.row,
						mealList);
				lv.setAdapter(mAdapter);

				mainSpinner.setEnabled(true);
				typeSpinner.setEnabled(true);
				sideSpinner.setEnabled(true);
				addButton.setEnabled(true);
				checkoutButton.setEnabled(true);

				// Initialize menu (All current items in menu with their
				// respective
				// price)
				new InitialiseMenuItems(OrderActivity.this).execute();

				// Initializing adapters to populate spinner
				new InitialiseSpinnner(OrderActivity.this, mainSpinner)
						.execute(URL_1, "main_name");
				new InitialiseSpinnner(OrderActivity.this, typeSpinner)
						.execute(URL_2, "type_name");

				// Initializing sideSpinner adapter
				ArrayAdapter<CharSequence> sideAdapter = ArrayAdapter
						.createFromResource(OrderActivity.this,
								R.array.side_array,
								android.R.layout.simple_spinner_item);
				sideAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sideSpinner.setAdapter(sideAdapter);

				// addButton onClickListener
				addButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							if ((!(mainSpinner.getSelectedItem() == null))
									&& (!(typeSpinner.getSelectedItem() == null))) {

								String mealName = mainSpinner.getSelectedItem()
										.toString()
										+ " "
										+ typeSpinner.getSelectedItem()
												.toString();
								Log.e("mealName", mealName);

								if (menu.containsKey(mealName)) {

									String id = menu.get(mealName)[0];
									Log.e("Meal ID:", id);

									Double priceInDouble = Double
											.parseDouble(menu.get(mealName)[1]);
									Log.e("PriceInDouble", "" + priceInDouble);

									BigDecimal totalPrice = sidePrices
											.get(sideSpinner.getSelectedItem()
													.toString())
											.add(BigDecimal
													.valueOf(priceInDouble))
											.add(basePrice);

									Log.e("totalPrice", "" + totalPrice);
									DecimalFormat decim = new DecimalFormat(
											"0.00");
									String s = decim.format(totalPrice);
									Log.e("s", "s is '" + s + "'");

									Log.e("Side", sideSpinner.getSelectedItem()
											.toString());
									Meal meal;
									if (sideSpinner.getSelectedItem()
											.toString().equals("None")) {
										meal = new Meal(id, mealName, s);
									} else {
										meal = new Meal(id, mealName
												+ "\n"
												+ sideSpinner.getSelectedItem()
														.toString(), s);
									}
									mealList.add(meal);
									mAdapter.notifyDataSetChanged();

								} else
									Toast.makeText(OrderActivity.this,
											mealName + " doesn't exists",
											Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(OrderActivity.this,
										"You need to select a main and a type",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				// checkoutButton onClickListener
				checkoutButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (mealList.size() > 0) {
							// passes values into new activity
							Log.e("Order", "Check out");
							Intent intent = new Intent().setClass(
									getBaseContext(), CheckoutActivity.class);
							ArrayList<Meal> newMealList = new ArrayList<Meal>();

							for (int i = 0; i < mealList.size(); i++) {
								newMealList.add(mealList.get(i));
							}

							intent.putParcelableArrayListExtra("Meal list",
									newMealList);
							startActivityForResult(intent, REQUEST_CODE);
						} else {
							Toast.makeText(OrderActivity.this,
									"You have not placed any orders",
									Toast.LENGTH_SHORT).show();
						}

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
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case DialogInterface.BUTTON_POSITIVE:
										// Add notes button clicked

										final EditText input = new EditText(
												OrderActivity.this);

										input.setHint(mealList.get(position)
												.getMealExtraNotes());
										new AlertDialog.Builder(
												OrderActivity.this)
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

																// makes new
																// Meal Object
																// with current
																// meal
																// object's data
																// plus extra
																// notes
																mealList.set(
																		position,
																		new Meal(
																				mealList.get(
																						position)
																						.getMealID(),
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
										// Does nothing
										break;

									case DialogInterface.BUTTON_NEUTRAL:
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
											+ mealList.get(position)
													.getMealName()
											+ "? \n"
											+ "Extra notes: "
											+ mealList.get(position)
													.getMealExtraNotes())
									.setPositiveButton("Add Notes",
											dialogClickListener)
									.setNeutralButton("Delete",
											dialogClickListener)
									.setNegativeButton("Cancel",
											dialogClickListener).show();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} else {
				// disables buttons

				addButton.setEnabled(false);
				checkoutButton.setEnabled(false);
				mainSpinner.setEnabled(false);
				typeSpinner.setEnabled(false);
				sideSpinner.setEnabled(false);

				// dialog message to say that no connection to internet
				Toast.makeText(
						OrderActivity.this,
						"Please connect to the internet to use this application.",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (menu.isEmpty())
			if (check == null) {
				check = new Check();
				check.execute();
			}
		if (mAdapter != null) {
			mealList.clear();
			mAdapter.notifyDataSetChanged();
		}
	}

	Check check = null;

	@Override
	protected void onPause() {
		// interrupt check
		if (check != null) {
			check.cancel(true);
			check = null;
		}
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);
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
			
		case R.id.complete_orders:
			//new activity that shows all orders that are completed
			break;

		default:
			return super.onOptionsItemSelected(item);
		}// switch
		return true;

	}// onOptionsItemSelected

	public void setSidePrices() {
		sidePrices.put("Boiled Rice", BigDecimal.valueOf(0.00));
		sidePrices.put("Chips", BigDecimal.valueOf(0.00));
		sidePrices.put("Fried Rice", BigDecimal.valueOf(0.50));
		sidePrices.put("Boiled Rice and Chips", BigDecimal.valueOf(0.0));
		sidePrices.put("Fried Rice and Chips", BigDecimal.valueOf(0.50));
		sidePrices.put("Salt and Pepper Chips", BigDecimal.valueOf(1.00));
		sidePrices.put("Chow Mein", BigDecimal.valueOf(0.70));
		sidePrices.put("None", BigDecimal.valueOf(-0.30));
	}

	// gets base price of meals
	private final class GetBasePrice extends BasePrice {
		public GetBasePrice(Context c) {
			super(c);
		}// Constructor

		@Override
		protected void onPostExecute(HttpResponse r) {
			super.onPostExecute(r);
			basePrice = getPrice();
		}// onPostExecute
	}// getBasePrice

	// gets menu items
	private final class InitialiseMenuItems extends InitialiseMenu {
		public InitialiseMenuItems(Context c) {
			super(c);
		}

		@Override
		protected void onPostExecute(HttpResponse result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			menu = getMenu();
		}
	}
}
