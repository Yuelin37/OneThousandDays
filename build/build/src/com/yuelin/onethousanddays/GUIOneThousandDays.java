package com.yuelin.onethousanddays;

import java.io.IOException;
import java.sql.SQLException;

import com.yuelin.onethousanddays.db.ConnectionManager;
import com.yuelin.onethousanddays.db.DBType;
import com.yuelin.onethousanddays.db.QuotesDBConnectionManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIOneThousandDays extends Application {
	
	public double hours = 0;

	@Override
	public void start(Stage primaryStage) throws SQLException, IOException {

		QuotesDBConnectionManager.getInstance().setDevDB(false);
		QuotesDBConnectionManager.getInstance().setDBType(DBType.MYSQL);


		Parent root = FXMLLoader.load(getClass().getResource("view/MainApp.fxml"));
		Scene scene = new Scene(root, 1350, 800);

		primaryStage.setTitle("One Thousand Days");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	@Override
	public void stop() {
		QuotesDBConnectionManager.getInstance().close();
		ConnectionManager.getInstance().close();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	static boolean dev = false;

	public static void main(String[] args) {
		if (args.length > 0 && args[0].equals("--dev"))
			dev = true;

		ConnectionManager.getInstance().setDevDB(dev);
		launch(args);
	}

}