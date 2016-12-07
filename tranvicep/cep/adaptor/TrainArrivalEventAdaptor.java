/*
 * Copyright 2015 University of Murcia (Fernando Terroso-Saenz (fterroso@um.es), Mercedes Valdes-Vela, Antonio F. Skarmeta)
 * 
 * This file is part of Tram-CEP.
 * 
 * Tram-CEP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Tram-CEP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see http://www.gnu.org/licenses/.
 * 
 */
package tranvicep.cep.adaptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Auxiliar parser class to mOdify the 'historico_TD.csv' for a suitable
 * format for the Esper engine.
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class TrainArrivalEventAdaptor {
    
    static Logger LOG = Logger.getLogger(TrainArrivalEventAdaptor.class);
    private static final String APENDIX_NAME = "_modified";
    
    public static void formatFile(String filePath){
        try{
                        
            File f = new File(filePath);
            
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            
            String newPath = filePath.replace(".csv", APENDIX_NAME+".csv");
            PrintWriter writer = new PrintWriter(newPath);
            
            //timestamp,trainId,initial_origin,final_destination,driverId,plannedDepartureTime,plannedArrivalTime,realDepartureTime,realArrivalTime,lineId,origin,destination,description,length,estimated_time
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            String header = br.readLine();
            writer.println(header);
            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                String currentDate = parts[0];                
                Date d = inputDateFormat.parse(currentDate); 
                
                String part1 = parts[1]+","+parts[2]+","+parts[3]+","+parts[4];
                
                long plannedDeparture = Long.valueOf(parts[5]);
                plannedDeparture = d.getTime() + (plannedDeparture * 1000);
    
                long plannedArrival = Long.valueOf(parts[6]);
                plannedArrival = d.getTime() + (plannedArrival * 1000);
                
              
                long realDeparture = Long.valueOf(parts[7]);
                realDeparture = d.getTime() + (realDeparture * 1000);
                
                long realArrival = Long.valueOf(parts[8]);
                realArrival = (realArrival > 0) ? d.getTime() + (realArrival * 1000) : plannedArrival;
                                
                String part2 = parts[9]+","+parts[10]+","+parts[11]+","+parts[12]+","+parts[13]+","+parts[14];
                
                StringBuilder newLine = new StringBuilder();
                newLine.append(outputDateFormat.format(realArrival));
                newLine.append(",");
                newLine.append(part1);
                newLine.append(",");
                newLine.append(outputDateFormat.format(plannedDeparture));
                newLine.append(",");
                newLine.append(outputDateFormat.format(plannedArrival));
                newLine.append(",");
                newLine.append(outputDateFormat.format(realDeparture));
                newLine.append(",");
                newLine.append(outputDateFormat.format(realArrival));
                newLine.append(",");
                newLine.append(part2);
                
                writer.println(newLine);
                writer.flush();
                           
                System.out.println(newLine+"\n"+line);
                System.out.println("------");
            }
                        
            writer.close();
            LOG.info("Conversion completed sucessfully");
        }catch(Exception e){
            LOG.error("Error ",e);
        }
    }
    
}
