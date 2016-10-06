package com.yuelin.onethousanddays;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.yuelin.onethousanddays.beans.Activity;
import com.yuelin.onethousanddays.beans.Category;
import com.yuelin.onethousanddays.db.ConnectionManager;
import com.yuelin.onethousanddays.db.DBType;
import com.yuelin.onethousanddays.db.tables.ActivityManager;
import com.yuelin.onethousanddays.db.tables.CategoryManager;
import com.yuelin.onethousanddays.util.InputHelper;

public class OneThousandDays {

	public static void main(String[] args) throws SQLException, ParseException {
		boolean dev = false;

		int category;
		double hours;
		String date;
		String description;

		if (args.length > 0 && args[0].equals("--dev"))
			dev = true;

		ConnectionManager.getInstance().setDevDB(dev);

		System.out.println("Welcome To One Thousand Days!");
		System.out.println();

		ConnectionManager.getInstance().setDBType(DBType.MYSQL);

		CategoryManager.displayAllRows();
		category = InputHelper.getIntInput("Please select a category:\n");

		Category cat = CategoryManager.getRow(category);
		while (cat == null) {
			System.err.println("No rows were found");
			category = InputHelper.getIntInput("Please select a category:\n");
		}

		date = InputHelper
				.getInput("Please input the date you want to log for. (MM/dd/yyyy)\n");
		
		hours = InputHelper
				.getDoubleInput("How many hours do you want to log?\n");
		description = InputHelper
				.getInput("Please enter your description for this activity.\n");

		java.sql.Date sqlDate;
		if (date.equals("")) {
			System.out.println("empty...");
			java.util.Date utilDate = new java.util.Date();
			sqlDate = new java.sql.Date(utilDate.getTime());
		} else {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	        java.util.Date parsed = format.parse(date);
	        sqlDate = new java.sql.Date(parsed.getTime());
			
		}

		Activity activity = new Activity();
		activity.setCategoryId(category);
		activity.setDate(sqlDate);
		activity.setHours(hours);
		activity.setDescription(description);

		ActivityManager.insert(activity);

		ActivityManager.displayAllRows();

		ConnectionManager.getInstance().close();
	}
}
