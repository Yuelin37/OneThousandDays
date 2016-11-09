package com.yuelin.onethousanddays.view;

import java.text.DecimalFormat;

import com.yuelin.onethousanddays.GUIOneThousandDays;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import stopwatch.Watch;

public class StopwatchController {

	private Stage dialogStage;
	private Watch watch;
	
	private double hours;

	@FXML
	private StackPane stopwatchPane;
	@FXML
	private Label firstNameLabel;
	@FXML
	private Label lastNameLabel;
	@FXML
	private Label streetLabel;
	@FXML
	private Label postalCodeLabel;
	@FXML
	private Label cityLabel;
	@FXML
	private Label birthdayLabel;

	// Reference to the main application.
	private MainAppController mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public StopwatchController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		watch = new Watch();
		watch.setLayoutX(1);
		watch.setLayoutY(10);
		
		stopwatchPane.getChildren().add(watch);
		stopwatchPane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 20, 0.5, 0.0, 0.0);"
				+ "-fx-background-color: null;"); // Shadow effect

		stopwatchPane.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				
				DecimalFormat df = new DecimalFormat("#.##");
				hours = watch.getTime();
				System.out.println("From StopwatchController...");
				System.out.println(hours);
				System.out.println("==========================\n");
				hours = Math.round(hours * 100) / 100.00;
				mainApp.setHours(df.format(hours));
				watch.startStop();
				watch.hardReset();
				dialogStage.close();
			}

		});

	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainAppController(MainAppController mainApp) {
		this.mainApp = mainApp;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;

	}
}