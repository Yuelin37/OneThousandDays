package com.yuelin.onethousanddays;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yuelin.onethousanddays.beans.Activity;
import com.yuelin.onethousanddays.beans.Category;
import com.yuelin.onethousanddays.db.ConnectionManager;
import com.yuelin.onethousanddays.db.DBType;
import com.yuelin.onethousanddays.db.tables.ActivityManager;
import com.yuelin.onethousanddays.db.tables.CategoryManager;
import com.yuelin.onethousanddays.util.InputHelper;

public class OneThousandDays {

	private static final String FIRSTDAY = "10/05/2016";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"MM/dd/yyyy");

	public static void main(String[] args) throws SQLException, ParseException {
		boolean dev = false;

		long day = 0;
		int category;
		double hours;
		String date;
		String description;

		if (args.length > 0 && args[0].equals("--dev"))
			dev = true;

		ConnectionManager.getInstance().setDevDB(dev);

		System.out.println("Welcome To One Thousand Days!\n");

		java.util.Date today = new java.util.Date();

		day = getDayCount(FIRSTDAY, simpleDateFormat.format(today)) + 1;
		System.out.println("Today is " + simpleDateFormat.format(today) + ". And it's Day " + day
				+ ".\n");

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
			java.util.Date utilDate = new java.util.Date();
			sqlDate = new java.sql.Date(utilDate.getTime());
		} else {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			java.util.Date parsed = format.parse(date);
			sqlDate = new java.sql.Date(parsed.getTime());
			day = getDayCount(FIRSTDAY, date) + 1;
		}

		Activity activity = new Activity();
		activity.setDay(day);
		activity.setCategoryId(category);
		activity.setDate(sqlDate);
		activity.setHours(hours);
		activity.setDescription(description);

		ActivityManager.insert(activity);

		ActivityManager.displayAllRows();

		ConnectionManager.getInstance().close();
	}

	public static long getDayCount(String start, String end) {
		long diff = -1;
		try {
			Date dateStart = simpleDateFormat.parse(start);
			Date dateEnd = simpleDateFormat.parse(end);

			// time is always 00:00:00 so rounding should help to ignore the
			// missing hour when going from winter to summer time as well as the
			// extra hour in the other direction
			diff = Math.round((dateEnd.getTime() - dateStart.getTime())
					/ (double) 86400000);
		} catch (Exception e) {
			// handle the exception according to your own situation
		}
		return diff;
	}
}
