package it.niedermann.easypdf.merge.itemlistcell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import it.niedermann.easypdf.merge.PDFPart;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

public class ItemListCell extends ListCell<PDFPart> {

	ObservableList<PDFPart> listItems = null;
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ItemListCell.fxml"));
	@FXML
	BorderPane cell;
	@FXML
	Label filename;

	public ItemListCell(ObservableList<PDFPart> listItems, Consumer<ListCell<PDFPart>> clicked) {
		this.listItems = listItems;
		initDragBehavior();
		setOnMouseClicked((e) -> {
			clicked.accept(this);
		});
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	protected void updateItem(PDFPart item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			// getChildren().removeAll();
			setGraphic(null);
			setText(null);
		} else {
			filename.setText(item.getFile().getAbsolutePath());
			setGraphic(cell);
		}
	}

	@FXML
	private void remove(MouseEvent e) {
		listItems.remove(getIndex());
	}

	private void initDragBehavior() {
		setOnDragDetected(event -> {
			if (getItem() == null) {
				return;
			}
			Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString(Integer.toString(getIndex()));
			dragboard.setContent(content);
			event.consume();
		});

		setOnDragOver(event -> {
			if (event.getGestureSource() != this && event.getDragboard().hasString()) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
			event.consume();
		});

		setOnDragEntered(event -> {
			if (event.getGestureSource() != this && event.getDragboard().hasString()) {
				setOpacity(0.3);
			}
		});

		setOnDragExited(event -> {
			if (event.getGestureSource() != this && event.getDragboard().hasString()) {
				setOpacity(1);
			}
		});

		setOnDragDropped(event -> {
			if (getItem() == null) {
				return;
			}

			Dragboard db = event.getDragboard();
			boolean success = false;

			if (db.hasFiles()) {
				// add(event);
				return;
			} else if (db.hasString()) {
				int sourceId = Integer.parseInt(db.getString());

				PDFPart draggedItem = listItems.get(sourceId);
				PDFPart currentItem = getItem();

				// getIndex() does always return the id of the first
				// appearance, even if there is the same file multiple times
				// in the list
				int targetId = -1;
				for (int i = 0; i < listItems.size(); i++) {
					if (listItems.get(i) == currentItem) {
						targetId = i;
						break;
					}
				}
				// Dieses Item an die Stelle des gezogenen Items setzen
				listItems.set(sourceId, currentItem);

				// Das gezogene Item an diese Stelle setzen
				listItems.set(targetId, draggedItem);

				getListView().getItems().setAll(new ArrayList<>(getListView().getItems()));

				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});

		setOnDragDone(DragEvent::consume);
	}
}
