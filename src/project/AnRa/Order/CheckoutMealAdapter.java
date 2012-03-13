package project.AnRa.Order;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class CheckoutMealAdapter extends ArrayAdapter<Meal> {

	private ArrayList<Meal> items;
	private final Context mContext;

	public CheckoutMealAdapter(Context context, int textViewResourceId,
			ArrayList<Meal> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.checkout_row, null);
		}
		Meal m = items.get(position);
		if (m != null) {
			TextView lt = (TextView) v.findViewById(R.id.left_text);
			TextView rt = (TextView) v.findViewById(R.id.right_text);
			TextView bt = (TextView) v.findViewById(R.id.bottom_text);
			if (lt != null) {
				lt.setText(m.getMealName());
			}
			if (rt != null) {
				rt.setText(m.getMealPrice());
			}
			if (bt != null) {
				bt.setText("Extra Notes: " + m.getMealExtraNotes());
			}
		}
		return v;
	}

}
