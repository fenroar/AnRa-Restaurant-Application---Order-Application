package project.AnRa.Order;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutActivity extends Activity {
	private ArrayList<Meal> checkoutList = new ArrayList<Meal>();
	private CheckoutMealAdapter mAdapter;
	private String order_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		BigDecimal total = new BigDecimal("0");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);

		final ListView lv = (ListView) findViewById(R.id.list);
		final TextView totalText = (TextView) findViewById(R.id.total);
		checkoutList = getIntent().getParcelableArrayListExtra("Meal list");

		mAdapter = new CheckoutMealAdapter(CheckoutActivity.this,
				R.layout.checkout_row, checkoutList);
		lv.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		new GetOrderID(CheckoutActivity.this).execute();

		for (int i = 0; i < checkoutList.size(); i++) {
			Meal meal = checkoutList.get(i);
			Double itemPrice = Double.parseDouble(meal.getMealPrice());
			total = total.add(new BigDecimal(itemPrice));
		}

		DecimalFormat decim = new DecimalFormat("0.00");
		String s = decim.format(total);
		totalText.setText("Total price is £" + s);

		final Button completeButton = (Button) findViewById(R.id.complete_button);

		completeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < checkoutList.size(); i++) {
					new AddMealOrder(CheckoutActivity.this).execute(
							checkoutList.get(i).getMealName(), checkoutList
									.get(i).getMealExtraNotes(), checkoutList
									.get(i).getMealID(), order_id);
				}
				Toast.makeText(CheckoutActivity.this,
						"Your Order ID is: " + order_id, Toast.LENGTH_SHORT)
						.show();

			}
		});
	}

	private final class GetOrderID extends NewOrder {
		public GetOrderID(Context c) {
			super(c);
		}

		@Override
		protected void onPostExecute(HttpResponse result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			order_id = getID();
		}
	}
}
