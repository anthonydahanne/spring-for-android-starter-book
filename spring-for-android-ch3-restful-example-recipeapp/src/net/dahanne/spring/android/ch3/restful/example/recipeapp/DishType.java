package net.dahanne.spring.android.ch3.restful.example.recipeapp;

public enum DishType {
	ENTREE, MAIN_DISH, DESSERT;

	public static DishType fromString(String type) {
		if (ENTREE.toString().equals(type)) {
			return ENTREE;
		} else if (MAIN_DISH.toString().equals(type)) {
			return MAIN_DISH;
		} else {
			return DESSERT;
		}
	}

}
