package com.yuelin.onethousanddays.db.tables;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.yuelin.onethousanddays.beans.Activity;
import com.yuelin.onethousanddays.beans.Category;
import com.yuelin.onethousanddays.db.ConnectionManager;

public class ActivityManager {
	private static Connection conn = ConnectionManager.getInstance()
			.getConnection();

	public static Category getRow(int catId) throws SQLException {

		String sql = "SELECT * FROM categories WHERE catId = ?";
		ResultSet rs = null;

		try (
		// Connection conn = DBUtil.getConnection(DBType.MYSQL);
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

	public static boolean insert(Activity bean) throws SQLException {
		String sql = "INSERT into activities (categoryId, date, hours, description) "
				+ "VALUES (?, ?, ?, ?)";
		ResultSet keys = null;
		try (// Connection conn = DBUtil.getConnection(DBType.MYSQL);
		PreparedStatement stmt = conn.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);) {

			stmt.setInt(1, bean.getCategoryId());
			stmt.setDate(2, (Date) bean.getDate());
			stmt.setDouble(3, bean.getHours());
			stmt.setString(4, bean.getDescription());
			int affected = stmt.executeUpdate();

			if (affected == 1) {
				keys = stmt.getGeneratedKeys();
				keys.next();
				int newKey = keys.getInt(1);
				bean.setId(newKey);
			} else {
				System.err.println("No rows affected");
				return false;
			}
		} catch (SQLException e) {
			System.err.println(e);
			return false;
		} finally {
			if (keys != null)
				keys.close();
		}
		return true;

	}
}
