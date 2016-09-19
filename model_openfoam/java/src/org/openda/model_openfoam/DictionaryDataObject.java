/* MOD_V1.0
 * Copyright (c) 2013 OpenDA Association
 * All rights reserved.
 *
 * This file is part of OpenDA.
 *
 * OpenDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * OpenDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDA.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openda.model_openfoam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.joda.time.DateTime;
import org.openda.exchange.timeseries.TimeUtils;
import org.openda.interfaces.IDataObject;
import org.openda.exchange.DoubleExchangeItem;
import org.openda.interfaces.IPrevExchangeItem;
import org.openda.interfaces.IExchangeItem;

import java.text.MessageFormat;
import java.io.*;
import java.lang.String;
import java.util.*;


/**
 *
 * Read an OpenFOAM dictionary file that contains lines of the format:
 *
 * keyword value;\\#exchangeItemID
 *
 * and
 *
 * keyword (value1 value2 ...);\\#exchangeItemID
 *
 * For each value found in a line indicated by \\#exchangeItemID a DoubleExchangeItem is created.
 * All other lines are kept as-is.
 *
 * A referenceDate (ISO 8601 format) can be specified as a second argument. When a referenceDate is specified,
 * the value for exchangeItemId's "oda:startTime" and "oda:endTime" is converted from seconds to modified Julian days.
 *
 *  @author Werner Kramer (VORtech)
 */

@SuppressWarnings("unused")
public class DictionaryDataObject implements IDataObject{

//    ResourceBundle messages =
//        ResourceBundle.getBundle("LogMessageBundle");

    private static final Logger logger = LoggerFactory.getLogger(DictionaryDataObject.class);
    MessageFormat logFormatter = new MessageFormat("");

    private String fileName = null;
	private static final String keyWordPrefix =";//#";
	private static final String multiplexId ="@";
	private HashMap<String,IExchangeItem> items = new LinkedHashMap<>();
    private HashMap<String,Integer> multiplexColumn = new LinkedHashMap<>();
	private ArrayList<String> fileContent = new ArrayList<>();
	private double referenceMjd = 0.0;
	private static final double SECONDS_TO_DAYS = 1.0 / 24.0 / 60.0 / 60.0;
	private final List<String> timeExchangeItemIds =  Arrays.asList("oda:startTime","oda:endTime");
	private boolean convertTime = false;
	static private final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

    //private String arrayBrackets = "\\(\\)";
	//private String arrayDelimiter = " ";


    /**
     * Reads OpenFoam results generated by the sample utility.
     *
     * @param workingDir the working directory.
     * @param arguments list of other arguments:
     * <ol>
     * <li>The name of the file containing the data
     *      for this IoObject (relative to the working directory).</li>
     * <li>Optional, a referenceDate in ISO 8601 notatation, e.g
     *      for this IoObject (relative to the working directory).</li>
     *
     * </ol>
     */
	public void initialize(File workingDir, String[] arguments) {

		if ( arguments.length == 0 ) {
			throw new RuntimeException("No arguments are given when initializing.");
		} else if (arguments.length == 2) {
			Date date = new DateTime( arguments[1] ).toDate();
			this.referenceMjd = TimeUtils.date2Mjd(date);
			this.convertTime = true;
		}
		this.fileName = arguments[0];

		logger.debug("Searching for file:" + this.fileName + " in directory " + workingDir);
		File inputFile;
		// check file
		try{
			inputFile = new File(workingDir,fileName);
			if(!inputFile.isFile()){
				throw new IOException("Can not find file " +  inputFile);
			}
			this.fileName = inputFile.getCanonicalPath();
		}catch (Exception e) {
			throw new RuntimeException("Trouble opening file " + this.fileName);
		}
		//read file and parse to hash
		try {
			Scanner scanner = new Scanner(inputFile);
			scanner.useLocale(Locale.US);

			//FileInputStream in = new FileInputStream(inputFile);
			//BufferedReader buff = new BufferedReader(new InputStreamReader(in));

			String line;
            logger.debug("Reading file");
			while (scanner.hasNext()) {

				line = scanner.nextLine();
				fileContent.add(line);
				int locationIndex = line.indexOf(keyWordPrefix);
				//Scanner lineScanner = new Scanner(line);
				if (locationIndex > 0) {
					logger.trace("Read line: "+ line);
					String valueString="";
					String key = line.substring(locationIndex + keyWordPrefix.length() );
					logger.debug("Found keyword '" + key + "'") ;
					line = line.substring(0,locationIndex);

					String[] parts = line.split(String.format(WITH_DELIMITER, "\\(|\\)|\\s"));
					Vector<Double> values = new Vector<>();
					Vector<Integer> column = new Vector<>();
					for ( int index=0; index < parts.length ;index++) {
					//for ( String part : parts) {
						if (!parts[index].isEmpty()) {
							try {
								Double value = Double.parseDouble(parts[index]);
								values.add(value);
								column.add(index);
								logger.debug("Found value part " + parts[index]);
							} catch (NumberFormatException e) {
                                logger.trace("Skipping " + parts[index]);
							}
						}
					}
					if ( values.size() == 1 ) {
						double value =  values.firstElement();
						if (timeExchangeItemIds.contains(key) && convertTime) {
							logger.debug("Converting to MJD: " + key);
							value = value * SECONDS_TO_DAYS + referenceMjd;
						}
						IExchangeItem exchangeItem = new DoubleExchangeItem(key,value);
						items.put(key, exchangeItem);
						multiplexColumn.put(key,column.firstElement());
					} else {
						if (timeExchangeItemIds.contains(key)) {
							throw new RuntimeException("A line designated by keyword '" + key + "' cannot contain multiple values: " + line);
						}
						for (int index=0 ; index < values.size() ; index++  ) {
							String id = key + this.multiplexId + (index+1);
							IExchangeItem exchangeItem = new DoubleExchangeItem(id, values.elementAt(index));
							items.put(id, exchangeItem);
							multiplexColumn.put(id,column.elementAt(index));
						}
					}
				}
			}
			scanner.close();
		} catch (Exception e) {
			throw new RuntimeException("Problem reading from file " + fileName+" : "+e.getClass());
		}
        //logger.info(logFormatter.format(messages.getString("org.openda.exchange.dataobjects.initialized"), Arrays.toString( this.getExchangeItemIDs())  ) );

    }

	/** {@inheritDoc}
	 */
	public IPrevExchangeItem[] getExchangeItems() {

		int n = this.items.size();
		Set<String> keys = this.items.keySet();
		IPrevExchangeItem[] result=new IPrevExchangeItem[n];
		int i=0;
		for(String key : keys){
			result[i]=this.items.get(key);
			i++;
		}
		return result;
	}

	/** {@inheritDoc}
	 */
	public IExchangeItem getDataObjectExchangeItem(String exchangeItemID) {
		return items.get(exchangeItemID);
	}

	/** {@inheritDoc}
	 */
	public String[] getExchangeItemIDs() {
		return items.keySet().toArray(new String[items.size()]);
	}

	/** {@inheritDoc}
	 */
	public String[] getExchangeItemIDs(IPrevExchangeItem.Role role) {
		//TODO: select on role
		return items.keySet().toArray(new String[items.size()]);
	}

	/** {@inheritDoc}
	 */
	public void finish() {
        	//write to file
		File outputFile = new File(fileName);
		try{
			if(outputFile.isFile()){
				if ( ! outputFile.delete() ) throw new RuntimeException("Cannot delete " + outputFile);
			}
		}catch (Exception e) {
			logger.error("DictionaryDataObject: trouble removing file " + this.fileName +" :\n" + e.getMessage());
		}
		try {
			FileWriter writer = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(writer);
            for (String line: fileContent){
				int locationIndex = line.indexOf(keyWordPrefix);
				//Scanner lineScanner = new Scanner(line);
				if (locationIndex > 0) {
					logger.trace("Read line: " + line);
					String valueString = "";
					String key = line.substring(locationIndex + keyWordPrefix.length());
					logger.debug("Found keyword '" + key + "'");
					line = line.substring(0, locationIndex);
					String[] parts = line.split(String.format(WITH_DELIMITER, "\\(|\\)|\\s"));
					if (multiplexColumn.containsKey(key)) {
						int index = multiplexColumn.get(key);
                        Double paramValue = (Double) items.get(key).getValues();
                        if (timeExchangeItemIds.contains(key) && convertTime) {
                            logger.debug("Converting to MJD: " + key);
                            paramValue = (paramValue - referenceMjd ) / SECONDS_TO_DAYS;
                        }
                        logger.debug("Store value: " + paramValue);
                        parts[index] = Double.toString(paramValue);
					}
					int nr = 1;
					while (multiplexColumn.containsKey(key + multiplexId + nr)) {
						String id = key + multiplexId + nr;
						int index = multiplexColumn.get(id);
						Double paramValue = (Double) items.get(id).getValues();
						logger.debug("Store value part: " + paramValue);
                        parts[index] = Double.toString(paramValue);
						nr++;
					}
					StringBuilder builder = new StringBuilder();
					for(String part : parts) {
						builder.append(part);
					}
					String outputLine = builder.toString() + keyWordPrefix + key + "\n";
					logger.trace("Write line: " + outputLine);
					out.write(outputLine);
				}
                else {
                    //Write Line
                    out.write(line + "\n");
                }
            }
			out.close();
		} catch (Exception e) {
			throw new RuntimeException("DictionaryDataObject: Problem writing to file " + this.fileName+" :\n" + e.getMessage());
		}
//        logger.info(logFormatter.format(messages.getString("org.openda.exchange.dataobjects.finished"), this.fileName ) );


//        logger.info("Dataobject '" +  "' is finished. Exchange items are written to file: " + this.fileName);
    }
}
