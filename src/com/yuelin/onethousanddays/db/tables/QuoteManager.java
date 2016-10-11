package com.yuelin.onethousanddays.db.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.yuelin.onethousanddays.beans.Quote;
import com.yuelin.onethousanddays.db.QuotesDBConnectionManager;

public class QuoteManager {

	private static Connection conn = QuotesDBConnectionManager.getInstance()
			.getConnection();

	public static Quote getRandomQuote() throws SQLException {

		String sql = "select * from wp_quotescollection where quote_id = "
				+ "(select ceil (rand() * (select count(*) from wp_quotescollection))) limit 1;";
		ResultSet rs = null;

		try (PreparedStatement stmt = conn.prepareStatement(sql);) {
			boolean quoteFound = false;
			while (!quoteFound) {
				rs = stmt.executeQuery();
				if (rs.next()) {
					Quote bean = new Quote();
					bean.setQuote(rs.getString("quote"));
					bean.setAuthor(rs.getString("author"));
					quoteFound = true;
					return bean;
				} else {
					// System.out.println("NO QUOTE RETURNED...");
				}

			}

		} catch (SQLException e) {
			System.err.println(e);
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return null;

	}
}
