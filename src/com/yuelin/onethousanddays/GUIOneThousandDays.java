package com.yuelin.onethousanddays;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yuelin.onethousanddays.db.ConnectionManager;
import com.yuelin.onethousanddays.db.QuotesDBConnectionManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIOneThousandDays extends Application {
	// Define a static logger variable so that it references the
	// Logger instance named "GUIOneThousandDays".
	private static final Logger logger = LogManager.getLogger(GUIOneThousandDays.class);

	public double hours = 0;

	@Override
	public void start(Stage primaryStage) throws SQLException, IOException {

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
		logger.error("Starting...");
		if (args.length > 0 && args[0].equals("--dev"))
			dev = true;

		ConnectionManager.getInstance().setDevDB(dev);
		launch(args);
	}

}