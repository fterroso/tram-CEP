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
package tranvicep.tools;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class TrainsProvider {
    
    static Logger LOG = Logger.getLogger(TrainsProvider.class);
    
    private static final Map<String,String> numPlateForID;  
    private static final Map<String,String> IDForNumPlate;
    
    private static final Map<String,Train> trainForNumPlate;
   
    static{
        numPlateForID = new HashMap<String,String>();
        numPlateForID.put("", "");

        
        IDForNumPlate = new HashMap<String,String>();
        IDForNumPlate.put("","");
        
        trainForNumPlate = new HashMap<String,Train>();
        trainForNumPlate.put("", new Train("",""));
 
        
    }
    
    public static String getNumPlateForID(String trainID){
        return numPlateForID.get(trainID);
    }
    
    public static String getIDForNumPlate(String trainNumPlate){
        return IDForNumPlate.get(trainNumPlate);
    }
    
    /**
     * This method REDUCES the load of a train when it arrives at a stop.
     *
     * @param trainId
     * @param stopId
     * @param timestamp
     */
    public static void reduceTrainLoad(
            String trainId, 
            String stopId, 
            long timestamp){
        
        String stopName = StopsProvider.getStopNameById(stopId);
        trainForNumPlate.get(trainId).reduceLoad(stopName, timestamp);
        
    }
    
    public static void resetTrainLoad(String trainId){
        trainForNumPlate.get(trainId).resetAllLoad();
    }
    
    public static void updateTrainLoad(
            String trainId,
            String stopId,
            long load){   
        
        trainForNumPlate.get(trainId).setLoadForStop(stopId, (double)load);

    }
}
