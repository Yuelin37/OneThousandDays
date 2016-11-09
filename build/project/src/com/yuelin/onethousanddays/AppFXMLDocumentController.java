package com.yuelin.onethousanddays;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import stopwatch.Watch;

/**
 *
 * @author ylyan
 */
public class AppFXMLDocumentController implements Initializable {

	private static String firstDay = "10/05/2016";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	static long day = 0;
	java.util.Date today = new java.util.Date();
	private double hours = 0;
	private int categoryId = 0;
	private java.sql.Date sqlDate;
	private String description;
	private int dayOfWeek = 0;

	private Watch watch;

	@FXML
	private Group stopwatchgroup;

	@FXML
	private AnchorPane anchorpane;

	@FXML
	private VBox root;

	@FXML
	private Label lblDay;

	@FXML
	private Label lblRandomQuote;

	@FXML
	private TextField hoursTF;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Button stopwatchBtn;

	@FXML
	private Label lblPercentage;

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
	private StackedBarChart<String, Number> hoursBarChart;

	@FXML
	private StackPane stopwatchPane;

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
		selectDate();
		getHours();
		getDescritpion();
		getDay();

		if (categoryId == 0) {
			Alert alert = new Alert(AlertType.WARNING, "Please select a category you want to log hours for.");
			alert.showAndWait().ifPresent(response -> {
			});
			return;
		}

		if (hours == 0) {
			Alert alert = new Alert(AlertType.WARNING, "Please enter hours you want to log.");
			alert.showAndWait().ifPresent(response -> {
				// if (response == ButtonType.OK) {
				// System.out.println("WARNING...");
				// }
			});
			return;
		}

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
		updateHoursBarChart();

		datePicker.setValue(LocalDate.now());
		categoryCB.setValue("Category");
		hoursTF.setText("0.00");
		txtAreaDescription.setText("");

	}

	private void getDay() {
		// System.out.println(datePicker.getValue().toString());
		day = getDayCount(firstDay, simpleDateFormat.format(java.sql.Date.valueOf(datePicker.getValue()))) + 1;

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		File file = new File("application.properties");
		if (!file.exists()) {
			setFirstDay();
		}

		Properties properties = new Properties();
		Path propFile = Paths.get("application.properties");
		try {
			properties.load(Files.newBufferedReader(propFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		firstDay = properties.getProperty("FirstDay");

		firstDay = "10/05/2016";
		lblRandomQuote.setText(firstDay);

		day = getDayCount(firstDay, simpleDateFormat.format(today)) + 1;
		lblDay.setText(Long.toString(day));

		DecimalFormat df2 = new DecimalFormat("###.##");
		double progress = day / 1000.00;
		progressBar.setProgress(progress);
		lblPercentage.setText(String.valueOf(df2.format(progress * 100)) + "%");
		updateRandomQuote();
		updateActivitySummary();
		updateActivityDetails();
		updateCategoryCB();
		datePicker.setValue(LocalDate.now());

		Pattern validDoubleText = Pattern.compile("-?((\\d*)|(\\d+\\.\\d*))");

		TextFormatter<Double> textFormatter = new TextFormatter<Double>(new DoubleStringConverter(), 0.0, change -> {
			String newText = change.getControlNewText();
			if (validDoubleText.matcher(newText).matches()) {
				return change;
			} else
				return null;
		});

		URL location = AppFXMLDocumentController.class.getProtectionDomain().getCodeSource().getLocation();
		System.out.println(location.getFile());

		Image imageDecline = new Image(
				AppFXMLDocumentController.class.getResourceAsStream("resources/stopwatch_icon.png"));
		ImageView im = new ImageView(imageDecline);
		im.setFitWidth(20);
		im.setFitHeight(20);
		stopwatchBtn.setGraphic(im);

		hoursTF.setTextFormatter(textFormatter);
		// hoursTF.setText("5.555");

		updateHoursBarChart();

		watch = new Watch();
		watch.setLayoutX(1);
		watch.setLayoutY(10);
		stopwatchPane.setVisible(false);
		stopwatchPane.getChildren().add(watch);
		stopwatchPane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 20, 0.5, 0.0, 0.0);"
				+ "-fx-background-color: null;"); // Shadow effect

		stopwatchPane.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				stopwatchPane.setVisible(false);
				DecimalFormat df = new DecimalFormat("#.##");
				hours = watch.getTime();
				hours = Math.round(hours * 100) / 100.00;
				hoursTF.setText(df.format(hours));
				watch.startStop();
				watch.hardReset();
			}

		});
	}

	private void setFirstDay() {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("One Thousand Days");
		dialog.setHeaderText("Please select your first day.");
		dialog.setResizable(false);

		// Widgets
		Label label2 = new Label("First Day: ");
		DatePicker dp = new DatePicker();

		// Create layout and add to dialog
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 35, 20, 35));
		grid.add(label2, 1, 1);
		grid.add(dp, 2, 1);
		dialog.getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

		// Result converter for dialog
		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType b) {

				if (b == buttonTypeOk) {
					return simpleDateFormat.format(java.sql.Date.valueOf(dp.getValue()));
				}

				return null;
			}
		});

		// Show dialog
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {

			System.out.println("Result: " + result.get());

			Properties properties = new Properties();
			properties.setProperty("FirstDay", result.get());
			try {
				properties.store(new FileOutputStream("application.properties"), null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void updateHoursBarChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		hoursBarChart.setAnimated(false);
		hoursBarChart.getData().clear();
		hoursBarChart.setTitle("Hours Summary");
		yAxis.setLabel("Hours");

		ArrayList<String> dates = new ArrayList<String>();
		for (String category : CategoryManager.getCategories()) {
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(category);

			ArrayList<Activity> activities = ActivityManager.getActivitiesForDate(today, 7);
			for (Activity activity : activities) {
				if (dates.indexOf(activity.getDate().toString()) < 0) {
					dates.add(activity.getDate().toString());
				}
				if (activity.getCategoryName().equals(category))
					series.getData().add(new XYChart.Data<>(activity.getDate().toString(), activity.getHours()));
				// else
				// series.getData().add(new
				// XYChart.Data<>(activity.getDate().toString(), 0.0));
			}

			hoursBarChart.getData().add(series);
		}

		xAxis.setCategories(FXCollections.<String>observableArrayList(dates));
	}

	public void updateCategoryCB() {
		String[] categories = CategoryManager.getCategories().toArray(new String[0]);
		categoryCB.getItems().addAll(categories);
	}

	public void updateRandomQuote() {
		try {
			Quote quote = new Quote();
			quote = QuoteManager.getRandomQuote();
			if (quote.getAuthor() != null) {
				lblRandomQuote.setText(quote.getQuote() + "\n--- " + quote.getAuthor());
			} else {
				lblRandomQuote.setText(quote.getQuote());
			}
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

	}

	public void updateActivityDetails() {

		ArrayList<Activity> activits = ActivityManager.getAllRows();
		ObservableList<Activity> activityList = FXCollections.observableArrayList(activits);
		tvActivityDetails.setItems(activityList);
		tvActivityDetails.getColumns().clear();

		TableColumn<Activity, String> id = new TableColumn<>("ID");
		id.setCellValueFactory(new PropertyValueFactory<>("id"));
		id.prefWidthProperty().bind(tvActivityDetails.widthProperty().multiply(0.066));

		TableColumn<Activity, String> date = new TableColumn<>("Date");
		date.setCellValueFactory(new PropertyValueFactory<>("date"));
		date.prefWidthProperty().bind(tvActivityDetails.widthProperty().multiply(0.12));

		TableColumn<Activity, String> dayOfWeekEnumValue = new TableColumn<>("Day Of Week");
		dayOfWeekEnumValue.setCellValueFactory(new PropertyValueFactory<>("dayOfWeekEnumValue"));
		dayOfWeekEnumValue.prefWidthProperty().bind(tvActivityDetails.widthProperty().multiply(0.14));

		TableColumn<Activity, String> day = new TableColumn<>("Day");
		day.setCellValueFactory(new PropertyValueFactory<>("day"));
		day.prefWidthProperty().bind(tvActivityDetails.widthProperty().multiply(0.07));

		TableColumn<Activity, String> hours = new TableColumn<>("Hours");
		hours.setCellValueFactory(new PropertyValueFactory<>("hours"));
		hours.prefWidthProperty().bind(tvActivityDetails.widthProperty().multiply(0.07));

		TableColumn<Activity, String> categoryName = new TableColumn<>("Category");
		categoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
		categoryName.prefWidthProperty().bind(tvActivityDetails.widthProperty().multiply(0.13));

		TableColumn<Activity, String> description = new TableColumn<>("Description");
		description.setCellValueFactory(new PropertyValueFactory<>("description"));
		description.prefWidthProperty().bind(tvActivityDetails.widthProperty().multiply(0.4));

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
		try {
			hours = Double.parseDouble(hoursTF.getText());
			System.out.println(hours);
		} catch (Exception e) {
		}

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

	@FXML
	public void showStopwatch() {
		stopwatchPane.setVisible(true);
	}

}