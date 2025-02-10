package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            // INSERT YOUR CODE HERE
            // Initialize Json objects and Json arrays
            //JsonObject jsonObject = new JsonObject();
            // Created jsonObject as LinkedHashMap instead to match the ordering of the file. 
            LinkedHashMap<String, Object> jsonObject = new LinkedHashMap<>();
            JsonArray prodNums = new JsonArray();
            JsonArray colHeadings = new JsonArray();
            JsonArray data = new JsonArray();
            
            // Create a CSVReader object by passing csvString as a StringReader object to parse the csvString.
            // Use the CSVReader's readAll() to read each line as an array and store it in a List. 
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> csvList = reader.readAll();
            
            // Populate colHeadings arrray
            Iterator<String[]> iterator = csvList.iterator();// Create Iterator object loop through elements in csvList.
            String[] heading = iterator.next();
            for (String field:heading){
                colHeadings.add(field.trim());
            }
            
            // Populate prodNums and data array
            while (iterator.hasNext()){
                String[] csvRecord = iterator.next();// Stores each line of CSV file as a string array.
                JsonArray csvData = new JsonArray();// Creates a new Jason array to later populate "data" array in the correct format.
                prodNums.add(csvRecord[0].trim());// Store first enty of each line in ProdNums.
                csvData.add(csvRecord[1]);// Store next entry in csvData.
                
                // Loop through next two entries and convert them to integers. Add them to csvData.
                for(int i=2; i<4; ++i){
                    csvData.add(Integer.valueOf(csvRecord[i]));
                }
                // Loop through remaining entries and add them to csVData.
                for(int j=4; j<csvRecord.length; ++j) {
                    csvData.add(csvRecord[j].trim());
                }
                /*data.add(csvRecord[1].trim());
                for(int i=2; i<4; ++i){
                    data.add(Integer.valueOf(csvRecord[i]));
                }
                for(int j=4; j<csvRecord.length; ++j) {
                    data.add(csvRecord[j].trim());
                }*/
                data.add(csvData);//add each CSV array to data.  
            }
            
            // Add json arrays to jsonobject
            jsonObject.put("ProdNums", prodNums);
            jsonObject.put("ColHeadings", colHeadings);
            jsonObject.put("Data", data);
            
            // Serialize json object and store them in result
            result = Jsoner.serialize(jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(result.trim());
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            // INSERT YOUR CODE HERE
            // Declare/Initialze varaibles
            // Create JsonObject to deserialze jsonString
            JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());
            // Grab JsonArrays from jsonObject. 
            JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray data = (JsonArray) jsonObject.get("Data");
           
            // Create a StringWriter object and use it to initialize a CSVWriter.
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);
            
            // Create String array for the key values from "ColHeadings".
            String[] header = new String[colHeadings.size()];
            for (int i =0; i<colHeadings.size(); ++i){
                 header[i] = colHeadings.getString(i);  
            }
            csvWriter.writeNext(header);// add header to CSVWriter.
            
            // Create a String array for the remaining row values.
            String[] rowValues = new String[colHeadings.size()];
            
            // Populate rowValues with with the remaining key values.
            for (int i=0; i<data.size(); ++i){
                // Create JsonArray for arrays in "data".
                JsonArray dataArrays = (JsonArray) data.get(i);
                rowValues[0] = prodNums.getString(i); // Add "ProdNums" values to row
                // Loop trough entries in dataArrays and add them to rowValues
                for (int j=0; j <dataArrays.size(); ++j){
                    if(j == 2){
                        // Convert the Episode entry to the correct format
                        rowValues[j+1] = String.format("%02d", Integer.parseInt(dataArrays.get(j).toString()));
                    } else{
                        rowValues[j+1] = dataArrays.getString(j);
                    } 
                }
                csvWriter.writeNext(rowValues);// Add row values to csvWriter.
            }
          
            result = writer.toString();// export final csvString and store it in result. 
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(result);
        return result.trim();
        
    }
    
}
