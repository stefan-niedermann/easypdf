package it.niedermann.easypdf.merge;

import java.io.File;

import javafx.scene.image.Image;

public class PDFPart {
	
	public PDFPart(File file) {
		this.file = file;
	}
	
	private File file;
	private Image image = null;
	private int pageCount = 0;
	private int pageFrom;
	private int pageTo;
	
	public File getFile() {
		return file;
	}
	
	public Image getImage() {
		if(image == null) {
			image = MergeService.getImageOfPage(file, 0, 500, 500);
		}
		return image;
	}
	
	public int getPageCount() {
		if(pageCount == 0) {
			pageCount = MergeService.getPageCount(file);
		}
		return pageCount;
	}
		
	public void setPageFrom(int pageFrom) {
		this.pageFrom = pageFrom;
	}
	
	public int getPageFrom() {
		return this.pageFrom;
	}
	
	public void setPageTo(int pageTo) {
		this.pageTo = pageTo;
	}
	
	public int getPageTo() {
		return this.pageTo;
	}
}
