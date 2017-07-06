package it.niedermann.easypdf.merge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class MergeService {

	private MergeService() {
		super();
	}

	public static Runnable getMergeRunnable(List<File> files, File output, Consumer<Integer> progressConsumer) {
		return () -> {
			// Check if output is source and target
			File outputTmp = null;
			if (files.contains(output)) {
				outputTmp = new File(output.getParent() + "/" + output.getName() + ".tmp");
				if (output.renameTo(outputTmp)) {
					Collections.replaceAll(files, output, outputTmp);
				} else {
					throw new IllegalArgumentException(output.getName() + " is source and target and "
							+ output.getName() + ".tmp already exists.");
				}
			}
			Document document = new Document();
			try {
				output.createNewFile();
				FileOutputStream outputStream = new FileOutputStream(output, false);
				PdfCopy copy = new PdfCopy(document, outputStream);
				document.open();
				Integer finishedFiles = 0;
				for (File file : files) {
					PdfReader reader = new PdfReader(file.getAbsolutePath());
					copy.addDocument(reader);
					reader.close();
					progressConsumer.accept(++finishedFiles);
				}
				outputStream.flush();
				document.close();
				copy.close();
				// cleanup the temporary file if needed
				if (outputTmp != null && outputTmp.delete()) {
					Collections.replaceAll(files, outputTmp, output);
				}
			} catch (InvalidPdfException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
	}

	public static Runnable getImageOfPage(File file, int page, int width, int height, Consumer<Image> imageConsumer) {
		return () -> {
			Image img = null;
			try {
				if (file.exists()) {
					PDDocument document = PDDocument.load(file);
					PDFRenderer pdfRenderer = new PDFRenderer(document);
					for (int i = 0; i < document.getDocumentCatalog().getPages().getCount(); i++) {
						if (i == page) {
							img = SwingFXUtils.toFXImage(pdfRenderer.renderImage(page), null);
						}
					}
					document.close();
					imageConsumer.accept(img);
				} else {
					System.err.println(file.getName() + " File not exists");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}
