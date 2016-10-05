package com.yuelin.onethousanddays;

import java.sql.SQLException;

import com.yuelin.onethousanddays.beans.Category;
import com.yuelin.onethousanddays.db.tables.CategoryManager;
import com.yuelin.onethousanddays.util.InputHelper;


public class OneThousandDays {
	
	public static void main(String[] args) throws SQLException {
		int category;
		System.out.println("Welcome To One Thousand Days!");
		System.out.println();
		
		
		CategoryManager.displayAllRows();
		category = InputHelper.getIntInput("Please select a category: ");
		
		Category cat = CategoryManager.getRow(category);
		while(cat == null) {
			System.err.println("No rows were found");
			category = InputHelper.getIntInput("Please select a category: ");
		} 
	}
}
