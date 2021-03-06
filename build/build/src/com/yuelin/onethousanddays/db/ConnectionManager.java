package com.yuelin.onethousanddays.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static ConnectionManager instance = null;

	private final String USERNAME = "dbuser";
	private final String PASSWORD = "dbpassword";
	private final String H_CONN_STRING = "jdbc:hsqldb:data/onethousanddays";
	private final String M_CONN_STRING = "jdbc:mysql://localhost:3307/onethousanddays?useUnicode=true&characterEncoding=utf-8";
	private final String M_DEV_CONN_STRING = "jdbc:mysql://localhost:3307/onethousanddays_dev?useUnicode=true&characterEncoding=utf-8";

	private DBType dbType = DBType.MYSQL;
	private boolean devDB = false;

	private Connection conn = null;

	private ConnectionManager() {
	}

	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}

	public void setDBType(DBType dbType) {
		this.dbType = dbType;
	}

	public void setDevDB(boolean dev) {
		this.devDB = dev;
	}

	private boolean openConnection() {
		try {
			switch (dbType) {

			case MYSQL:
				if (devDB)
					conn = DriverManager.getConnection(M_DEV_CONN_STRING, USERNAME,
							PASSWORD);
				else
					conn = DriverManager.getConnection(M_CONN_STRING, USERNAME,
							PASSWORD);
				return true;

			case HSQLDB:
				conn = DriverManager.getConnection(H_CONN_STRING, USERNAME,
						PASSWORD);
				return true;

			default:
				return false;
			}
		} catch (SQLException e) {
			System.err.println(e);
			return false;
		}

	}

	public Connection getConnection() {
		if (conn == null) {
			if (openConnection()) {
				System.out.println("Connection opened");
				return conn;
			} else {
				return null;
			}
		}
		return conn;
	}

	public void close() {
		System.out.println("Closing connection");
		try {
			conn.close();
			conn = null;
		} catch (Exception e) {
		}
	}

}