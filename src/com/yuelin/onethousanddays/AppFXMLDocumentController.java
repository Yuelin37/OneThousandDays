package com.yuelin.onethousanddays;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import com.yuelin.onethousanddays.beans.Activity;
import com.yuelin.onethousanddays.beans.ActivitySummary;
import com.yuelin.onethousanddays.beans.Quote;
import com.yuelin.onethousanddays.db.tables.ActivityManager;
import com.yuelin.onethousanddays.db.tables.CategoryManager;
import com.yuelin.onethousanddays.db.tables.QuoteManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author ylyan
 */
public class AppFXMLDocumentController implements Initializable {

	private static final String FIRSTDAY = "10/05/2016";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	static long day = 0;
	java.util.Date today = new java.util.Date();
	private double hours = 0;
	private int categoryId = 0;
	private java.sql.Date sqlDate;
	private String description;
	private int dayOfWeek = 0;

	@FXML
	private TextArea lblQuote;

	@FXML
	private TextField hoursTF;

	@FXML
	private TableView<ActivitySummary> tvActivitySummary;

	@FXML
	private TableView<Activity> tvActivityDetails;

	@FXML
	private DatePicker datePicker;

	@FXML
	private ComboBox<String> categoryCB;

	@FXML
	private TextArea txtAreaDescription;

	@FXML
	private void handleButtonAction(ActionEvent event) {
		updateRandomQuote();
	}

	@FXML
	private void selectDate() {

		sqlDate = java.sql.Date.valueOf(datePicker.getValue());

		Calendar c = Calendar.getInstance();
		c.setTime(sqlDate);
		dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	}

	@FXML
	private void selectCategory() {
		categoryId = CategoryManager.getId(categoryCB.getValue());
	}

	@FXML
	private void logHours() {
		getHours();
		getDescritpion();

		Activity activity = new Activity();
		activity.setCategoryId(categoryId);
		activity.setDate(sqlDate);
		activity.setDay(day);
		activity.setDayOfWeek(dayOfWeek);
		activity.setHours(hours);
		activity.setDescription(description);

		try {
			ActivityManager.insert(activity);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateActivitySummary();
		updateActivityDetails();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		day = getDayCount(FIRSTDAY, simpleDateFormat.format(today)) + 1;
		updateRandomQuote();
		updateActivitySummary();
		updateActivityDetails();
		updateCategoryCB();
		datePicker.setValue(LocalDate.now());
	}

	public void updateCategoryCB() {
		String[] categories = CategoryManager.getCategories().toArray(new String[0]);
		categoryCB.getItems().addAll(categories);
	}

	public void updateRandomQuote() {
		try {
			Quote quote = new Quote();
			quote = QuoteManager.getRandomQuote();
			if (quote.getAuthor() != null)
				lblQuote.setText(quote.getQuote() + "\n--- " + quote.getAuthor());
			else
				lblQuote.setText(quote.getQuote());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateActivitySummary() {

		ArrayList<ActivitySummary> activitySummary = ActivityManager.getActivitySummary(day);
		ObservableList<ActivitySummary> activityList = FXCollections.observableArrayList(activitySummary);
		tvActivitySummary.setItems(activityList);
		tvActivitySummary.getColumns().clear();

		TableColumn<ActivitySummary, String> catName = new TableColumn<>("Category Name");
		catName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
		tvActivitySummary.getColumns().add(catName);

		TableColumn<ActivitySummary, String> hours = new TableColumn<>("Total Hours");
		hours.setCellValueFactory(new PropertyValueFactory<>("hours"));
		tvActivitySummary.getColumns().add(hours);

		TableColumn<ActivitySummary, String> averageHours = new TableColumn<>("Hours/Day");
		averageHours.setCellValueFactory(new PropertyValueFactory<>("averageHours"));
		tvActivitySummary.getColumns().add(averageHours);
		
		
		tvActivitySummary.setPrefWidth(287);
		tvActivitySummary.setPrefHeight(100);

	}

	public void updateActivityDetails() {

		ArrayList<Activity> activits = ActivityManager.getAllRows();
		ObservableList<Activity> activityList = FXCollections.observableArrayList(activits);
		tvActivityDetails.setItems(activityList);
		tvActivityDetails.getColumns().clear();

		TableColumn<Activity, String> id = new TableColumn<>("ID");
		id.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Activity, String> date = new TableColumn<>("Date");
		date.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<Activity, String> dayOfWeekEnumValue = new TableColumn<>("Day Of Week");
		dayOfWeekEnumValue.setCellValueFactory(new PropertyValueFactory<>("dayOfWeekEnumValue"));

		TableColumn<Activity, String> day = new TableColumn<>("Day");
		day.setCellValueFactory(new PropertyValueFactory<>("day"));

		TableColumn<Activity, String> hours = new TableColumn<>("Hours");
		hours.setCellValueFactory(new PropertyValueFactory<>("hours"));

		TableColumn<Activity, String> categoryName = new TableColumn<>("Category");
		categoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

		TableColumn<Activity, String> description = new TableColumn<>("Description");
		description.setCellValueFactory(new PropertyValueFactory<>("description"));

		tvActivityDetails.getColumns().add(id);
		tvActivityDetails.getColumns().add(date);
		tvActivityDetails.getColumns().add(dayOfWeekEnumValue);
		tvActivityDetails.getColumns().add(day);
		tvActivityDetails.getColumns().add(hours);
		tvActivityDetails.getColumns().add(categoryName);
		tvActivityDetails.getColumns().add(description);

	}

	@FXML
	public void getHours() {
		hours = Double.parseDouble(hoursTF.getText());
	}

	public void getDescritpion() {
		description = txtAreaDescription.getText();
	}

	public static long getDayCount(String start, String end) {
		long diff = -1;
		try {
			Date dateStart = simpleDateFormat.parse(start);
			Date dateEnd = simpleDateFormat.parse(end);

			// time is always 00:00:00 so rounding should help to ignore the
			// missing hour when going from winter to summer time as well as the
			// extra hour in the other direction
			diff = Math.round((dateEnd.getTime() - dateStart.getTime()) / (double) 86400000);
		} catch (Exception e) {
			// handle the exception according to your own situation
		}
		return diff;
	}

}
