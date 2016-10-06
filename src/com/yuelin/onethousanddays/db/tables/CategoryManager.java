package com.yuelin.onethousanddays.db.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.yuelin.onethousanddays.beans.Category;
import com.yuelin.onethousanddays.db.ConnectionManager;
import com.yuelin.onethousanddays.db.DBType;

public class CategoryManager {
	
	private static Connection conn = ConnectionManager.getInstance()
			.getConnection();

	public static void displayAllRows() throws SQLException {

		String sql = "SELECT catId, catName FROM categories";
		try (//Connection conn = DBUtil.getConnection(DBType.MYSQL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);) {

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

	public static Category getRow(int catId) throws SQLException {

		String sql = "SELECT * FROM categories WHERE catId = ?";
		ResultSet rs = null;

		try (//Connection conn = DBUtil.getConnection(DBType.MYSQL);
				PreparedStatement stmt = conn.prepareStatement(sql);) {
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
}
