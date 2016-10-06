package com.yuelin.onethousanddays;

import java.sql.SQLException;
import com.yuelin.onethousanddays.beans.Activity;
import com.yuelin.onethousanddays.beans.Category;
import com.yuelin.onethousanddays.db.ConnectionManager;
import com.yuelin.onethousanddays.db.DBType;
import com.yuelin.onethousanddays.db.tables.ActivityManager;
import com.yuelin.onethousanddays.db.tables.CategoryManager;
import com.yuelin.onethousanddays.util.InputHelper;


public class OneThousandDays {
	
	public static void main(String[] args) throws SQLException {
		int category;
		double hours;
		String description;
		System.out.println("Welcome To One Thousand Days!");
		System.out.println();
		
		
		
		ConnectionManager.getInstance().setDBType(DBType.MYSQL);
		
		CategoryManager.displayAllRows();
		category = InputHelper.getIntInput("Please select a category: ");
		
		Category cat = CategoryManager.getRow(category);
		while(cat == null) {
			System.err.println("No rows were found");
			category = InputHelper.getIntInput("Please select a category: ");
		} 
		
		hours = InputHelper.getDoubleInput("How many hours do you want to log?");
		description = InputHelper.getInput("Please enter your description for this activity.");
		
		java.util.Date utilDate = new java.util.Date();
	    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		
		Activity activity = new Activity();
		activity.setCategoryId(category);
		activity.setDate(sqlDate);
		activity.setHours(hours);
		activity.setDescription(description);
		
		ActivityManager.insert(activity);
		ConnectionManager.getInstance().close();
	}
}
