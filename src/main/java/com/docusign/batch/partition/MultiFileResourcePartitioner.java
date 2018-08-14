/**
 * 
 */
package com.docusign.batch.partition;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import com.docusign.batch.domain.AppConstants;

/**
 * @author Amit.Bist
 *
 */
public class MultiFileResourcePartitioner implements Partitioner {

	final static Logger logger = Logger.getLogger(MultiFileResourcePartitioner.class);

	private String inboundDir;

	public String getInboundDir() {
		return inboundDir;
	}

	public void setInboundDir(String inboundDir) {
		this.inboundDir = inboundDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.batch.core.partition.support.Partitioner#partition(
	 * int)
	 */
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		Map<String, ExecutionContext> partitionMap = new HashMap<String, ExecutionContext>();
		File dir = new File(inboundDir);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();

			for (File file : files) {
				ExecutionContext context = new ExecutionContext();
				logger.info("MultiFileResourcePartitioner.partition()- " + file.toURI().toString() + " FileName- "
						+ file.getName());
				context.put(AppConstants.FILE_RESOURCE_PARAM_NAME, file.toURI().toString());
				context.put(AppConstants.FILE_PARAM_NAME, file.getName());
				partitionMap.put(file.getName(), context);
			}
		}
		return partitionMap;
	}

}