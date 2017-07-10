package it.niedermann.easypdf.merge;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.easypdf.merge.itemdetails.ItemDetails;
import it.niedermann.easypdf.merge.itemlistcell.ItemListCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

public class MergeTab extends BorderPane {

	private static ExecutorService service = Executors.newCachedThreadPool();
	private CompletableFuture<Void> cf = null;

	@FXML
	Button mergeButton;

	@FXML
	Button cancelButton;

	@FXML
	ProgressBar progressBar;

	@FXML
	ItemDetails itemDetails;

	@FXML
	ListView<PDFPart> listView;
	ObservableList<PDFPart> listItems = FXCollections.observableArrayList();

	public MergeTab() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MergeTab.fxml"));
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
			this.setCenter(fxmlLoader.getRoot());
			listView.setItems(listItems);
			listView.setCellFactory(param -> new ItemListCell(listItems, (itemCell) -> {
				itemDetails.setDetails(itemCell.getItem());
			}));
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@FXML
	private void merge(MouseEvent event) {
		mergeButton.setDisable(true);
		//cancelButton.setDisable(false);
		progressBar.setDisable(false);
		
		cf = CompletableFuture.runAsync(MergeService.getMergeRunnable(listItems,
				new File("/home/stefan/Schreibtisch/output.pdf"), (processed) -> {
					Platform.runLater(() -> {
						progressBar.setProgress((double) processed / listItems.size());
					});
				}), service).thenRun(() -> {
					Platform.runLater(() -> {
						setNotRunning();
					});
				});
	}

	@FXML
	private void cancel(MouseEvent e) {
		System.out.println("Cancel method");
		if (cf != null && !cf.isCancelled()) {
			System.out.println("Trying to cancel");
			if (cf.cancel(true)) {
				System.out.println("Successfully canceled");
				setNotRunning();
			}
		}
	}

	/**
	 * Configures the user interface to show the user that currently no merge is
	 * in progress.
	 */
	private void setNotRunning() {
		//cancelButton.setDisable(true);
		mergeButton.setDisable(false);
		progressBar.setProgress(0);
		progressBar.setDisable(true);
	}

	@FXML
	private void dragover(DragEvent e) {
		e.acceptTransferModes(TransferMode.ANY);
		e.consume();
	}

	@FXML
	private void add(DragEvent e) {
		Dragboard db = e.getDragboard();
		boolean success = false;
		if (db.hasFiles()) {
			success = true;
			for (File file : db.getFiles()) {
				PDFPart pdfPart = new PDFPart(file);
				listItems.add(pdfPart);
				itemDetails.setDetails(pdfPart);
			}
		}
		e.setDropCompleted(success);
		e.consume();
	}
}
