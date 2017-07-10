package it.niedermann.easypdf.merge.itemdetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.easypdf.merge.PDFPart;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ItemDetails extends BorderPane {

	@FXML
	ImageView previewImage;
	@FXML
	Label filename;
	@FXML
	Label path;
	@FXML
	ComboBox<Integer> pageFrom;
	@FXML
	ComboBox<Integer> pageTo;

	private static ExecutorService service = Executors.newSingleThreadExecutor();

	public ItemDetails() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ItemDetails.fxml"));
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
			this.setCenter(fxmlLoader.getRoot());
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setDetails(PDFPart item) {
		if (item != null) {
			filename.setText(item.getFile().getName());
			path.setText(item.getFile().getAbsolutePath());

			ItemDetails.service.submit(() -> {
				List<Integer> pageFromList = new ArrayList<Integer>();
				List<Integer> pageToList = new ArrayList<Integer>();
				int count = item.getPageCount();
				for (int i = 1; i <= count; i++) {
					pageFromList.add(i);
					pageToList.add(i);
				}
				pageFrom.valueProperty().addListener((obs, oldValue, newValue) -> {
					if (newValue != null)
						item.setPageFrom(newValue);
				});
				pageTo.valueProperty().addListener((obs, oldValue, newValue) -> {
					if (newValue != null)
						item.setPageTo(newValue);
				});
				Platform.runLater(() -> {
					pageFrom.getSelectionModel().select(item.getPageFrom() - 1);
					pageTo.getSelectionModel().select(item.getPageTo() - 1);
					pageFrom.setItems(FXCollections.observableList(pageFromList));
					pageTo.setItems(FXCollections.observableList(pageToList));
				});
			});
			ItemDetails.service.submit(() -> {
				Image img = item.getImage();
				Platform.runLater(() -> {
					previewImage.setImage(img);
				});
			});
		}
	}
}
