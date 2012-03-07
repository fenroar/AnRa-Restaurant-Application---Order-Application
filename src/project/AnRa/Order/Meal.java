package project.AnRa.Order;

public class Meal {
	
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
	
	public Meal(final String name, final String price){
		mealName = name;
		mealPrice = price;
	}
	
	public Meal(final String name, final String price, final String extra){
		mealName = name;
		mealPrice = price;
		extraNotes = extra;
	}
	
	public String toString()
	{
		return mealName;
	}

}
