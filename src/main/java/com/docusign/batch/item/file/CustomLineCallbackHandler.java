package com.docusign.batch.item.file;

import org.apache.log4j.Logger;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class CustomLineCallbackHandler implements LineCallbackHandler {

	final static Logger logger = Logger.getLogger(CustomLineCallbackHandler.class);

	private CustomFlatFileHeaderCallback customFlatFileHeaderCallback;

	private DelimitedLineTokenizer lineTokenizer;

	private String delimiter;

	private String successColumns;

	public String getSuccessColumns() {
		return successColumns;
	}

	public void setSuccessColumns(String successColumns) {
		this.successColumns = successColumns;
	}

	public CustomFlatFileHeaderCallback getCustomFlatFileHeaderCallback() {
		return customFlatFileHeaderCallback;
	}

	public void setCustomFlatFileHeaderCallback(CustomFlatFileHeaderCallback customFlatFileHeaderCallback) {
		this.customFlatFileHeaderCallback = customFlatFileHeaderCallback;
	}

	public DelimitedLineTokenizer getLineTokenizer() {
		return lineTokenizer;
	}

	public void setLineTokenizer(DelimitedLineTokenizer lineTokenizer) {
		this.lineTokenizer = lineTokenizer;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public void handleLine(String line) {

		logger.debug("DynamicLineCallbackHandler.handleLine() " + line + " delimiter- " + this.delimiter);

		String outputHeaderLine = line + this.getDelimiter() + this.successColumns;

		customFlatFileHeaderCallback.setHeaderLine(outputHeaderLine);

		customFlatFileHeaderCallback.setDelimiter(this.getDelimiter());

		String[] values = line.split(this.delimiter);

		lineTokenizer.setNames(values);

	}

}