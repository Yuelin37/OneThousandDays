package com.yuelin.onethousanddays.view;

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

import org.controlsfx.control.ToggleSwitch;

import com.yuelin.onethousanddays.GUIOneThousandDays;
import com.yuelin.onethousanddays.beans.Activity;
import com.yuelin.onethousanddays.beans.ActivitySummary;
import com.yuelin.onethousanddays.beans.Quote;
import com.yuelin.onethousanddays.db.tables.ActivityManager;
import com.yuelin.onethousanddays.db.tables.CategoryManager;
import com.yuelin.onethousanddays.db.tables.QuoteManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

/**
 *
 * @author ylyan
 */
public class MainAppController implements Initializable {

	private static String firstDay = "10/05/2016";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	static long day = 0;
	java.util.Date today = new java.util.Date();
	private double hours = 0;
	private int categoryId = 0;
	private java.sql.Date sqlDate;
	private String description;
	private int dayOfWeek = 0;

	private ArrayList<Activity> activities;

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
	private Pagination pagination;

	@FXML
	private StackedBarChart<String, Number> hoursBarChart;

	@FXML
	private StackPane stopwatchPane;

	@FXML
	private ToggleButton toggleswitch1;
	
	@FXML
	private ToggleSwitch toggleswitch;

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

		// firstDay = "10/05/2016";
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

		// URL location =
		// MainAppController.class.getProtectionDomain().getCodeSource().getLocation();
		// System.out.println(location.getFile());

		Image imageDecline = new Image(getClass().getResourceAsStream("resources/stopwatch_icon.png"));
		ImageView im = new ImageView(imageDecline);
		im.setFitWidth(20);
		im.setFitHeight(20);
		stopwatchBtn.setGraphic(im);

		hoursTF.setTextFormatter(textFormatter);

		updateHoursBarChart();
		pagination.setVisible(false);
		toggleswitch.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (toggleswitch.isSelected()) {
					pagination.setVisible(true);
					root.getScene().getWindow().setWidth(1350);
					// root.getScene().getWindow().setWidth(root.getScene().getWindow().getWidth()
					// + 700);
				} else {
					// toggleswitch.setText("Bad");
					pagination.setVisible(false);
					root.getScene().getWindow().setWidth(648);
					// root.getScene().getWindow().setWidth(root.getScene().getWindow().getWidth()
					// - 700);
				}
			}
		});
		
		toggleswitch1.setSelected(true);

	}

	public int rowsPerPage() {
		return 24;
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

		activities = ActivityManager.getAllRows();
		ObservableList<Activity> activityList = FXCollections.observableArrayList(activities);
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

		pagination.setPageCount(activities.size() / rowsPerPage() + 1);
		// pagination.setStyle("-fx-border-color:red;");
		pagination.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer pageIndex) {
				if (pageIndex > activities.size() / rowsPerPage() + 1) {
					return null;
				} else {
					return createPage(pageIndex);
				}
			}
		});

	}

	public VBox createPage(int pageIndex) {
		int lastIndex = 0;
		int displace = activities.size() % rowsPerPage();
		if (displace > 0) {
			lastIndex = activities.size() / rowsPerPage();
		} else {
			lastIndex = activities.size() / rowsPerPage() - 1;

		}

		VBox box = new VBox(5);
		int page = pageIndex * itemsPerPage();

		for (int i = page; i < page + itemsPerPage(); i++) {
			TableView<Activity> table = new TableView<Activity>();

			TableColumn<Activity, String> id = new TableColumn<>("ID");
			id.setCellValueFactory(new PropertyValueFactory<>("id"));
			id.prefWidthProperty().bind(pagination.widthProperty().multiply(0.066));

			TableColumn<Activity, String> date = new TableColumn<>("Date");
			date.setCellValueFactory(new PropertyValueFactory<>("date"));
			date.prefWidthProperty().bind(pagination.widthProperty().multiply(0.12));

			TableColumn<Activity, String> dayOfWeekEnumValue = new TableColumn<>("Day Of Week");
			dayOfWeekEnumValue.setCellValueFactory(new PropertyValueFactory<>("dayOfWeekEnumValue"));
			dayOfWeekEnumValue.prefWidthProperty().bind(pagination.widthProperty().multiply(0.14));

			TableColumn<Activity, String> day = new TableColumn<>("Day");
			day.setCellValueFactory(new PropertyValueFactory<>("day"));
			day.prefWidthProperty().bind(pagination.widthProperty().multiply(0.07));

			TableColumn<Activity, String> hours = new TableColumn<>("Hours");
			hours.setCellValueFactory(new PropertyValueFactory<>("hours"));
			hours.prefWidthProperty().bind(pagination.widthProperty().multiply(0.07));

			TableColumn<Activity, String> categoryName = new TableColumn<>("Category");
			categoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
			categoryName.prefWidthProperty().bind(pagination.widthProperty().multiply(0.13));

			TableColumn<Activity, String> description = new TableColumn<>("Description");
			description.setCellValueFactory(new PropertyValueFactory<>("description"));
			description.prefWidthProperty().bind(pagination.widthProperty().multiply(0.4));

			table.getColumns().addAll(id, date, dayOfWeekEnumValue, day, hours, categoryName, description);
			if (lastIndex == pageIndex) {
				table.setItems(FXCollections.observableArrayList(
						activities.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + displace)));
			} else {
				table.setItems(FXCollections.observableArrayList(
						activities.subList(pageIndex * rowsPerPage(), pageIndex * rowsPerPage() + rowsPerPage())));
			}
			table.setPrefHeight(600);
			box.getChildren().add(table);
		}
		return box;
	}

	public int itemsPerPage() {
		return 1;
	}

	@FXML
	public void getHours() {
		try {
			hours = Double.parseDouble(hoursTF.getText());
			System.out.println(hours);
		} catch (Exception e) {
		}

	}

	public void setHours(String hours) {
		hoursTF.setText(hours);
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

	@FXML
	private void showStopWatchDialog() {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(GUIOneThousandDays.class.getResource("view/StopwatchDialog.fxml"));
			StackPane page = (StackPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(root.getScene().getWindow());
			// It's important to set TRANSPANRENT to both the stage style and
			// the scene color
			dialogStage.initStyle(StageStyle.TRANSPARENT);
			Scene scene = new Scene(page, 320, 320, Color.TRANSPARENT);
			dialogStage.setScene(scene);
			dialogStage.setX(root.getScene().getWindow().getX() + root.getScene().getWindow().getWidth() / 2 - 160);
			dialogStage.setY(root.getScene().getWindow().getY() + root.getScene().getWindow().getHeight() / 2 - 160);

			// Set the person into the controller.
			StopwatchController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainAppController(this);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
