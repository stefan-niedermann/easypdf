package it.niedermann.pdftool.merge.itemdetails;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.pdftool.merge.MergeService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ItemDetails extends Pane {

	@FXML
	ImageView previewImage;
	@FXML
	Label filename;
	@FXML
	Label path;

	private static ExecutorService service = Executors.newSingleThreadExecutor();

	public ItemDetails() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ItemDetails.fxml"));
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
			this.getChildren().add(fxmlLoader.getRoot());
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setDetails(File item) {
		if (item != null) {
			filename.setText(item.getName());
			path.setText(item.getAbsolutePath());
			ItemDetails.service.submit(MergeService.getImageOfPage(item, 0, 500, 500, (image) -> {
				Platform.runLater(() -> {
					previewImage.setImage(image);
				});
			}));
		}
	}
}
