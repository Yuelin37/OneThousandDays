package com.yuelin.onethousanddays.db.tables;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;

import com.yuelin.onethousanddays.beans.Activity;
import com.yuelin.onethousanddays.beans.ActivitySummary;
import com.yuelin.onethousanddays.beans.Category;
import com.yuelin.onethousanddays.db.ConnectionManager;

public class ActivityManager {
	private static Connection conn = ConnectionManager.getInstance().getConnection();

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
		String sql = "INSERT into activities (categoryId, date, dayOfWeek, hours, description, day) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		ResultSet keys = null;
		try (// Connection conn = DBUtil.getConnection(DBType.MYSQL);
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

			stmt.setInt(1, bean.getCategoryId());
			stmt.setDate(2, (Date) bean.getDate());
			stmt.setInt(3, bean.getDayOfWeek());
			stmt.setDouble(4, bean.getHours());
			stmt.setString(5, bean.getDescription());
			stmt.setInt(6, (int) bean.getDay());
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

	public static void displayAllRows() throws SQLException {
		// String sql =
		// "SELECT id, categoryId, date, hours, description FROM activities
		// ORDER BY id";
		String sql = "SELECT id, day, catName, date, dayOfWeek, hours, description FROM activities "
				+ "INNER JOIN categories on categoryId=catid ORDER BY id DESC LIMIT 10";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {

			System.out.println("Activities:");
			System.out.println(
					"==========================================================================================");
			System.out.format("%1$4s: %2$-13s %3$-13s %4$-9s %5$-11s    %6$-16s %7$s\n", "ID", "Date", "Weekday", "Day",
					"Hours", "Category", "Descritpion");
			System.out.println(
					" --------------------------------------------------------------------------------------- ");
			while (rs.next()) {
				// StringBuffer bf = new StringBuffer();
				// bf.append(rs.getInt("id") + ":\t");
				// bf.append(rs.getDate("date") + "\t");
				// bf.append(rs.getDouble("hours") + " hour(s)\t");
				// bf.append(rs.getString("catName") + "\t");
				// bf.append(rs.getString("description"));
				// System.out.println(bf.toString());

				System.out.format("%1$4s: %2$-13s %3$-13s Day %4$-4s %5$4s Hour(s)    %6$-16s %7$s\n", rs.getInt("id"),
						rs.getDate("date"), DayOfWeek.SUNDAY.plus(rs.getInt("dayOfWeek") - 1), rs.getInt("day"),
						rs.getDouble("hours"), rs.getString("catName"), rs.getString("description"));
			}
			System.out.println(
					"==========================================================================================");
		}
	}

	public static ArrayList<Activity> getAllRows() {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		String sql = "SELECT id, day, catName, date, dayOfWeek, hours, description FROM activities "
				+ "INNER JOIN categories on categoryId=catid ORDER BY id DESC";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {

			while (rs.next()) {
				Activity activity = new Activity();
				activity.setId(rs.getInt("id"));
				activity.setDate(rs.getDate("date"));
				// activity.setDayOfWeek(DayOfWeek.SUNDAY.plus(rs.getInt("dayOfWeek")
				// - 1));
				activity.setDay(rs.getInt("day"));
				activity.setHours(rs.getDouble("hours"));
				activity.setCategoryName(rs.getString("catName"));
				activity.setDescription(rs.getString("description"));
				activities.add(activity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return activities;
	}

	public static void displayHoursSummary(long day) throws SQLException {
		String sql = "select categoryId, catName, sum(hours) as totalHours from activities "
				+ "inner join categories on categoryId = categories.catId group by categoryId";

		System.out.println("==========================================");
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			System.out.println("Hours Summary:");
			while (rs.next()) {
				System.out.format("%1$-12s: %2$-4s Hours | %3$-4.2f Hours/Day\n", rs.getString("catName"),
						rs.getDouble("totalHours"), rs.getDouble("totalHours") / day);
			}
		}
		System.out.println("==========================================");
	}

	public static ArrayList<ActivitySummary> getActivitySummary(long day) {
		ArrayList<ActivitySummary> activitySummary = new ArrayList<ActivitySummary>();
		String sql = "select categoryId, catName, sum(hours) as totalHours from activities "
				+ "inner join categories on categoryId = categories.catId group by categoryId";
		DecimalFormat df = new DecimalFormat("#.00");
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
				ActivitySummary as = new ActivitySummary();
				as.setCategoryName(rs.getString("catName"));
				as.setHours(rs.getDouble("totalHours"));

				// as.setAverageHours(rs.getDouble("totalHours") / day);
				as.setAverageHours(Double.parseDouble(df.format(rs.getDouble("totalHours") / day)));
				activitySummary.add(as);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return activitySummary;
	}
}
