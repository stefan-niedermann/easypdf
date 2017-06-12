package it.niedermann.pdftool;

import java.io.IOException;

import it.niedermann.pdftool.merge.MergeTab;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	@FXML
	MergeTab mergeTab;
	// @FXML
	// Tab split;

	@Override
	public void start(Stage stage) {
		BorderPane root;
		try {
			root = (BorderPane) FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.getIcons().add(new Image(Main.class.getResourceAsStream("./file-pdf-box.png")));
			stage.setTitle("PDF Tool");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
