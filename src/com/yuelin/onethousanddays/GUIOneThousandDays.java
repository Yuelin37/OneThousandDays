package com.yuelin.onethousanddays;
import java.io.IOException;
import java.sql.SQLException;

import com.yuelin.onethousanddays.beans.Quote;
import com.yuelin.onethousanddays.db.ConnectionManager;
import com.yuelin.onethousanddays.db.DBType;
import com.yuelin.onethousanddays.db.QuotesDBConnectionManager;
import com.yuelin.onethousanddays.db.tables.QuoteManager;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class GUIOneThousandDays extends Application {


    
    @Override
    public void start(Stage primaryStage) throws SQLException, IOException {
    	
    	QuotesDBConnectionManager.getInstance().setDevDB(false);
		QuotesDBConnectionManager.getInstance().setDBType(DBType.MYSQL);
		
		
        Label response = new Label("Select a college or university:");
        
        ListView<String> lvColleges;
            
        
        Text title = new Text("One Thousand Days");
        title.setFill(Paint.valueOf("#2A5058"));
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        
        VBox root = new VBox();
        
//        root.setAlignment(Pos.CENTER);
        
        ObservableList<String> colleges = 
                FXCollections.observableArrayList("Penn State", "Drexel",  
                "Widener", "Shippensburg", "Bloomsburg", "Penn Tech", 
                "Lockhaven", "Kutztown");
        
        lvColleges = new ListView<String>(colleges);
        lvColleges.setPrefSize(300,150);
        
        MultipleSelectionModel<String> lvSelModel = 
                lvColleges.getSelectionModel();
        lvSelModel.selectedItemProperty().
        addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, 
                    String oldVal, String newVal)
            {
                response.setText("You selected " + newVal);
            }
                    
        });
        
        Label lblQuote = new Label();
        lblQuote.setText(getQuote().getQuote());
        
        Button btnNewQuote = new Button("New Quote");
        btnNewQuote.setOnAction(event->
        {
			try {
				Quote quote = new Quote();
				quote = getQuote();
				if(quote.getAuthor()!=null)
					lblQuote.setText(quote.getQuote()+ "\n--- " + quote.getAuthor());
				else
					lblQuote.setText(quote.getQuote());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        
        
        
        Button btn = new Button();
        btn.setText("Open Dialog");
        btn.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    final Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(primaryStage);
                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    Scene dialogScene = new Scene(dialogVbox, 300, 200);
                    dialog.setScene(dialogScene);
                    dialog.show();
                }
             });
        
        
        root.getChildren().add(title);
        root.getChildren().add(lvColleges);
        root.getChildren().add(response);
        root.getChildren().addAll(lblQuote, btnNewQuote, btn);
        
        Parent root1 = FXMLLoader.load(getClass().getResource("simpleApp.fxml"));
        Scene scene = new Scene(root1, 800, 1000);
        
        primaryStage.setTitle("One Thousand Days");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @Override
    public void stop(){
    	System.out.println("Stage is closing...");
    	QuotesDBConnectionManager.getInstance().close();
    }

    /**
     * @param args the command line arguments
     */
	static boolean dev = false;
    public static void main(String[] args) {
    	if (args.length > 0 && args[0].equals("--dev"))
			dev = true;
    	
    	ConnectionManager.getInstance().setDevDB(dev);
        launch(args);
    }
    
    public static Quote getQuote() throws SQLException{
    	Quote quote = new Quote();
    
		quote = QuoteManager.getRandomQuote();
		
		System.out.println(quote.getQuote());
		if (quote.getAuthor() != null)
			System.out.println("--- " + quote.getAuthor());			
		return quote;
		
    }
}