package project.AnRa.Order;

import android.os.Parcel;
import android.os.Parcelable;

public class Meal implements Parcelable {

	final private String meal_id;
	final private String mealName;
	final private String mealPrice;
	private String extraNotes = "None";

	public String getMealName() {
		return mealName;
	}

	public String getMealPrice() {
		return mealPrice;
	}

	public String getMealExtraNotes() {
		return extraNotes;
	}
	
	public String getMealID() {
		return meal_id;
	}

	public Meal(final String id, final String name, final String price) {
		meal_id = id;
		mealName = name;
		mealPrice = price;
	}

	public Meal(final String id, final String name, final String price, final String extra) {
		meal_id = id;
		mealName = name;
		mealPrice = price;
		extraNotes = extra;
	}

	public Meal(Parcel in) {
		// TODO Auto-generated constructor stub
		meal_id = in.readString();
		mealName = in.readString();
		mealPrice = in.readString();
		extraNotes = in.readString();
	}

	public String toString() {
		return mealName;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(meal_id);
		dest.writeString(mealName);
		dest.writeString(mealPrice);
		dest.writeString(extraNotes);
	}
	
	public static final Parcelable.Creator<Meal> CREATOR
    = new Parcelable.Creator<Meal>() 
   {
         public Meal createFromParcel(Parcel in) 
         {
             return new Meal(in);
         }

         public Meal[] newArray (int size) 
         {
             return new Meal[size];
         }
    };


}
