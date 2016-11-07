package com.yuelin.onethousanddays.db.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.yuelin.onethousanddays.beans.Category;
import com.yuelin.onethousanddays.db.ConnectionManager;

public class CategoryManager {

	private static Connection conn = ConnectionManager.getInstance().getConnection();

	public static void displayAllRows() throws SQLException {

		String sql = "SELECT catId, catName FROM categories ORDER BY catid";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {

			System.out.println("Categories:");
			System.out.println("==========================");
			while (rs.next()) {
				StringBuffer bf = new StringBuffer();
				bf.append(rs.getInt("catId") + ": ");
				bf.append(rs.getString("catName"));
				System.out.println(bf.toString());
			}
			System.out.println("==========================");
		}
	}

	public static ArrayList<Category> getAllRows() {

		String sql = "SELECT catId, catName FROM categories ORDER BY catid";
		ArrayList<Category> categories = new ArrayList<Category>();
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {

			while (rs.next()) {
				Category category = new Category();
				category.setCatId(rs.getInt("catId"));
				category.setCatName(rs.getString("catName"));
				categories.add(category);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categories;
	}

	public static ArrayList<String> getCategories() {

		String sql = "SELECT catId, catName FROM categories ORDER BY catid";
		ArrayList<String> categories = new ArrayList<String>();
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {

			while (rs.next()) {

				// category.setCatId(rs.getInt("catId"));
				categories.add(rs.getString("catName"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categories;
	}

	public static Category getRow(int catId) throws SQLException {

		String sql = "SELECT * FROM categories WHERE catId = ?";
		ResultSet rs = null;

		try (PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.setInt(1, catId);
			rs = stmt.executeQuery();

			if (rs.next()) {
				Category bean = new Category();
				bean.setCatId(catId);
				bean.setCatName(rs.getString("catName"));
				return bean;
			} else {
				return null;
			}

		} catch (SQLException e) {
			System.err.println(e);
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

	}

	public static int getId(String catName) {
		int id = 0;

		String sql = "SELECT catId FROM categories WHERE catName = ?";
		ResultSet rs = null;

		try (PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.setString(1, catName);
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("catId");
			}

		} catch (SQLException e) {
			System.err.println(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return id;

	}
}
