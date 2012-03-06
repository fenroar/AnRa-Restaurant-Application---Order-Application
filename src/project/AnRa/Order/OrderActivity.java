package project.AnRa.Order;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class OrderActivity extends Activity {
	private ArrayList<Meal> mealList = new ArrayList<Meal>();
	private MealAdapter mAdapter;
	private Meal name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);

		// Sets up adapter for list of items being ordered
		mAdapter = new MealAdapter(this, R.layout.row, mealList);
		final ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(mAdapter);

		final Button addButton = (Button) findViewById(R.id.add_button);
		final Button checkoutButton = (Button) findViewById(R.id.checkout_button);
		final EditText testEdit = (EditText) findViewById(R.id.test_edit);

		addButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				name = new Meal(testEdit.getText().toString(), "0");
				mealList.add(name);
				mAdapter.notifyDataSetChanged();

			}
		});

		checkoutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//passes values into new activity
				Log.e("Order", "Check out");
			}
		});

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
								// Yes button clicked

								// Remembers the selected Index
								Log.e("Order", mealList.get(position - 1)
										.getMealName() + " deleted");
								mealList.remove(position - 1);
								mAdapter.notifyDataSetChanged();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								Log.e("Order", mealList.get(position - 1)
										.getMealName() + " not deleted");
								// No button clicked
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							OrderActivity.this);
					builder.setMessage(
							"Are you sure you want to remove "
									+ mealList.get(position - 1).getMealName()
									+ "?")
							.setPositiveButton("Yes", dialogClickListener)
							.setNegativeButton("No", dialogClickListener)
							.show();

				} catch (Exception e) {
					System.out.println("cannot delete");
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
}
