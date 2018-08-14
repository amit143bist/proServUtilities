/**
 * 
 */
package com.docusign.jwt.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.docusign.jwt.test.domain.EnvelopeData;
import com.docusign.jwt.test.util.CSVTestFileWriter;

/**
 * @author Amit.Bist
 *
 */
public class CreateInputCsv {

	@Test
	public void test() {

		System.out.println("Reading one input file CreateInputCsv.test() at " + Calendar.getInstance().getTime());
		String line = "";
		List<EnvelopeData> envelopeDataList = new ArrayList<EnvelopeData>();
		List<List<EnvelopeData>> envelopeDataDataList = new ArrayList<List<EnvelopeData>>();
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\SWSetup\\Sabre\\mydata\\input\\properData.csv"))) {

			int count = 0;
            while ((line = br.readLine()) != null) {
            	
            	EnvelopeData envelopeData = new EnvelopeData();
            	envelopeData.setEnvelopeId(line);
            	envelopeData.setReminderEnabled("");
            	envelopeData.setReminderDelay("");
            	envelopeData.setReminderFrequency("");
            	envelopeData.setExpireEnabled("true");
            	envelopeData.setExpireAfter("360");
            	envelopeData.setExpireWarn("30");
            	
            	envelopeDataList.add(envelopeData);
            	count++;
            	
            	if(count%950 == 0){
            		envelopeDataDataList.add(envelopeDataList);
            		envelopeDataList = new ArrayList<EnvelopeData>();
            	}

            }
            
            if(null != envelopeDataList && !envelopeDataList.isEmpty()){
            	envelopeDataDataList.add(envelopeDataList);
            }

            int i = 1;
            for(List<EnvelopeData> envelopeDataListLoop: envelopeDataDataList){
            	
            	CSVTestFileWriter.writeEnvelopeCsv("C:\\SWSetup\\Sabre\\mydata\\input\\" + "envelopeInput_" + i + ".csv", envelopeDataListLoop);
            	i++;
            }
            
            System.out.println("All files written and splitted in CreateInputCsv.test() at " + Calendar.getInstance().getTime());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
