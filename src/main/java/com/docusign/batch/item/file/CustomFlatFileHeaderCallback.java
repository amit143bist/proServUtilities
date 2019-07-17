package com.docusign.batch.item.file;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class CustomFlatFileHeaderCallback implements FlatFileHeaderCallback {

	final static Logger logger = Logger.getLogger(CustomFlatFileHeaderCallback.class);
	
	private String headerLine;
	
	private String delimiter;
	
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public String getHeaderLine() {
		return headerLine;
	}
	public void setHeaderLine(String headerLine) {
		this.headerLine = headerLine;
	}
	
	@Override
	public void writeHeader(Writer writer) throws IOException {

		logger.debug("CustomFlatFileHeaderCallback.writeHeader()");
		
		writer.write(headerLine);
	}

}