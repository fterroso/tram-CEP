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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Holder of the information about all the stopInfoForPlatform of the target line.
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class StopsProvider {
    
    static Logger LOG = Logger.getLogger(StopsProvider.class);
    
//    private static final long AVG_TIME_BETWEEN_STOPS = 81; //seconds
//    private static final long TIME_BETWEEN_STOPS_VARIATION = 15; //seconds
    
    static Map<String,Stop> stopInfoForPlatform;
    static Map<String,Integer> numSeqForPlatform;
    
    static Map<String,Integer> finishNumSeqForLine;
    static Map<String,Integer> startNumSeqForLine;
    static Map<String,Integer> middleNumSeqForLine;
        
    
    public static Stop getStopByName(String name){
        Collection<Stop> stops = stopInfoForPlatform.values();
        Iterator<Stop> itStops = stops.iterator();
        
        while(itStops.hasNext()){
            Stop s = itStops.next();
            if(s.getName().equals(name)){
                return s;
            }
        }
        return null;
    }
    
    public static Stop getStopById(String stopId){
      
        if(stopInfoForPlatform.containsKey(stopId))  
            return stopInfoForPlatform.get(stopId);
        return new Stop(stopId,"NOT DEFINED-"+stopId,-1, 0,0,Stop.StopType.NOT_DEFINED, Stop.StopArea.NOT_DEFINED);        
    }
    
    public static String getStopNameById(String stopId){
        return getStopById(stopId).getName();
    }   
    
    public static int getStopNumSeq(String stopId, String lineId){        
        return numSeqForPlatform.get(stopId+"-"+lineId);
    }
    
    public static int getFinishNumSeqForLine(String lineId){
        return finishNumSeqForLine.get(lineId);
    }
    
    public static int getStartNumSeqForLine(String lineId){
        return startNumSeqForLine.get(lineId);
    }
    
    public static int getMiddleNumSeqForLine(String lineId){
        return middleNumSeqForLine.get(lineId);
    }
    
    public static boolean lineAndStopMatch(String lineId, String stopId){
        if(lineId != null){
            String aux = stopId+"-"+lineId;
            return numSeqForPlatform.containsKey(aux);
        }
        
        return false;
    }
    
    public static String getLineFromStopId(String stopId){
        String aux = stopId + "-1";
        if(numSeqForPlatform.containsKey(aux)){
            return "1";
        }
 
        return "2";
    }
    
    public static int getMinNumSeqForStop(String stopId, String lineId){
        Stop stop = getStopById(stopId);
        return stop.getMinNumSeqForLine(lineId);
    }
    
    public static int getMaxNumSeqForStop(String stopId, String lineId){
        Stop stop = getStopById(stopId);
        return stop.getMaxNumSeqForLine(lineId);
    }
       
//    public static long[] getTravelTimeRange(Stop origin, Stop destination){
//        if(!origin.equals(destination)){
//            int oNumSeq = origin.getNumSeq();
//            int dNumSeq = destination.getNumSeq();
//            if(oNumSeq != dNumSeq){
//                int diff = Math.abs(oNumSeq - dNumSeq);
//                return new long[]{diff*(AVG_TIME_BETWEEN_STOPS-TIME_BETWEEN_STOPS_VARIATION),diff*(AVG_TIME_BETWEEN_STOPS+TIME_BETWEEN_STOPS_VARIATION)};
//            }else{
//                switch(oNumSeq){
//                    case 20:
//                        
//                }
//            }
//        }else{
//            return new long[]{0.0,0.0};
//        }
//    }
       
    
    public static void init(){
        
        stopInfoForPlatform = new HashMap<String,Stop>();
        numSeqForPlatform = new HashMap<String,Integer>();
                
        finishNumSeqForLine = new HashMap<String,Integer>();
        finishNumSeqForLine.put("1", 24); //Residencia Uni.
        finishNumSeqForLine.put("2", 47); //UCAM
        
        startNumSeqForLine = new HashMap<String,Integer>();
        startNumSeqForLine.put("1", 1);
        startNumSeqForLine.put("2", 43);
        
        middleNumSeqForLine = new HashMap<String,Integer>();
        middleNumSeqForLine.put("1", 20);
        middleNumSeqForLine.put("2", 47); //UCAM        
        
        // Init stops data.
        Stop stop = new Stop("114", "153", "Est. nueva cond.", 1, 0, 0,Stop.StopType.LEISURE_ONLY, Stop.StopArea.SHOPPING_MALLS);  
        stop.setMinNumSeqForLine("1", 1);
        stop.setMaxNumSeqForLine("1", 1);
        
        stopInfoForPlatform.put("114", stop);        
        stopInfoForPlatform.put("153", stop);
        numSeqForPlatform.put("114-1",1); 
        numSeqForPlatform.put("153-1",1); 
        
        stop = new Stop("118", "149", "La ladera", 2, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.SHOPPING_MALLS);
        stop.setMinNumSeqForLine("1", 2);
        stop.setMaxNumSeqForLine("1", 42);
        
        stopInfoForPlatform.put("118", stop);        
        stopInfoForPlatform.put("149", stop);
        numSeqForPlatform.put("118-1",2);
        numSeqForPlatform.put("149-1",42);                               
        
        stop = new Stop("122", "147", "Infantas", 3, 0, 0, Stop.StopType.LEISURE_ONLY, Stop.StopArea.SHOPPING_MALLS);
        stop.setMinNumSeqForLine("1", 3);
        stop.setMaxNumSeqForLine("1", 41);
        
        stopInfoForPlatform.put("122", stop);
        stopInfoForPlatform.put("147", stop);
        numSeqForPlatform.put("122-1",3);
        numSeqForPlatform.put("147-1",41);          
        
        stop = new Stop("123", "139", "Princ. Felipe", 4, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.SHOPPING_MALLS);
        stop.setMinNumSeqForLine("1", 4);
        stop.setMaxNumSeqForLine("1", 40);        
        
        stopInfoForPlatform.put("123", stop);        
        stopInfoForPlatform.put("139", stop); 
        numSeqForPlatform.put("123-1",4);
        numSeqForPlatform.put("139-1",40);         
        
        stop = new Stop("125", "144", "Churra", 5, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_1); 
        stop.setMinNumSeqForLine("1", 5);
        stop.setMaxNumSeqForLine("1", 39);
        
        stopInfoForPlatform.put("125", stop);        
        stopInfoForPlatform.put("144", stop);
        numSeqForPlatform.put("125-1",5);
        numSeqForPlatform.put("144-1",39);  
        
        stop = new Stop("126","143", "Alameda", 6, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_1);
        stop.setMinNumSeqForLine("1", 6);
        stop.setMaxNumSeqForLine("1", 38);        
        
        stopInfoForPlatform.put("126", stop);
        stopInfoForPlatform.put("143", stop);
        numSeqForPlatform.put("126-1",6);
        numSeqForPlatform.put("143-1",38);
        
        stop = new Stop("128", "141", "Los cubos", 7, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_1);
        stop.setMinNumSeqForLine("1", 7);
        stop.setMaxNumSeqForLine("1", 37);
        
        stopInfoForPlatform.put("128", stop);
        stopInfoForPlatform.put("141", stop);
        numSeqForPlatform.put("128-1",7);
        numSeqForPlatform.put("141-1",37); 
        
        stop = new Stop("129", "140", "Sant. y zar.", 8, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_1);
        stop.setMinNumSeqForLine("1", 8);
        stop.setMaxNumSeqForLine("1", 36);
        
        stopInfoForPlatform.put("129", stop);
        stopInfoForPlatform.put("140", stop);
        numSeqForPlatform.put("129-1",8);
        numSeqForPlatform.put("140-1",36);

        stop = new Stop("130", "146", "Princ. Asturias", 9, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_1);
        stop.setMinNumSeqForLine("1", 9);
        stop.setMaxNumSeqForLine("1", 35);
        
        stopInfoForPlatform.put("130", stop);
        stopInfoForPlatform.put("146", stop);
        numSeqForPlatform.put("130-1",9);        
        numSeqForPlatform.put("146-1",35); 
        
        stop = new Stop("131", "138", "Abenarabi", 10, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.CITY_CENTER);
        stop.setMinNumSeqForLine("1", 10);
        stop.setMaxNumSeqForLine("1", 34);
        
        stopInfoForPlatform.put("131", stop); 
        stopInfoForPlatform.put("138", stop);  
        numSeqForPlatform.put("131-1",10);        
        numSeqForPlatform.put("138-1",34);                
        
        stop = new Stop("132", "137", "Mar. Española", 11, 0, 0, Stop.StopType.RESIDENCE_OFFICE_ACADEMIC, Stop.StopArea.CITY_CENTER);
        stop.setMinNumSeqForLine("1", 11);
        stop.setMaxNumSeqForLine("1", 33);
        
        stopInfoForPlatform.put("132", stop);         
        stopInfoForPlatform.put("137", stop); 
        numSeqForPlatform.put("132-1",11);        
        numSeqForPlatform.put("137-1",33);        

        stop = new Stop("134", "135", "Plaza Circ.", 12, 0, 0, Stop.StopType.LEISURE_RESIDENCE, Stop.StopArea.CITY_CENTER);
        stop.setMinNumSeqForLine("1", 12);
        stop.setMaxNumSeqForLine("1", 32);
        
        stopInfoForPlatform.put("134", stop);                          
        stopInfoForPlatform.put("135", stop);                            
        numSeqForPlatform.put("134-1",12);        
        numSeqForPlatform.put("135-1",32);        
                
        stop = new Stop("155", "186", "J. Carlos I", 13, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.CITY_CENTER);
        stop.setMinNumSeqForLine("1", 13);
        stop.setMaxNumSeqForLine("1", 31);
        
        stopInfoForPlatform.put("155", stop);         
        stopInfoForPlatform.put("186", stop); 
        numSeqForPlatform.put("155-1",13);        
        numSeqForPlatform.put("186-1",31);        
        
        stop = new Stop("157", "184", "Bib. Reg.", 14, 0, 0, Stop.StopType.RESIDENCE_OFFICE_ACADEMIC, Stop.StopArea.CITY_CENTER);
        stop.setMinNumSeqForLine("1", 14);
        stop.setMaxNumSeqForLine("1", 30);
        
        stopInfoForPlatform.put("157", stop);         
        stopInfoForPlatform.put("184", stop); 
        numSeqForPlatform.put("157-1",14);        
        numSeqForPlatform.put("184-1",30);                
        
        stop = new Stop("158", "183", "Send. Granada", 15, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.CITY_CENTER);
        stop.setMinNumSeqForLine("1", 15);
        stop.setMaxNumSeqForLine("1", 29);
        
        stopInfoForPlatform.put("158", stop); 
        stopInfoForPlatform.put("183", stop); 
        numSeqForPlatform.put("158-1",15);        
        numSeqForPlatform.put("183-1",29);                
        
        stop = new Stop("159", "182", "Parq. Empres.", 16, 0, 0, Stop.StopType.RESIDENCE_OFFICE_ACADEMIC, Stop.StopArea.OUTSKIRTS_2);
        stop.setMinNumSeqForLine("1", 16);
        stop.setMaxNumSeqForLine("1", 28);
        
        stopInfoForPlatform.put("159", stop); 
        stopInfoForPlatform.put("182", stop); 
        numSeqForPlatform.put("159-1",16);   
        numSeqForPlatform.put("182-1",28);                
        
        stop = new Stop("161","180", "El puntal", 17, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_2);
        stop.setMinNumSeqForLine("1", 17);
        stop.setMaxNumSeqForLine("1", 27);
        
        stopInfoForPlatform.put("161", stop);         
        stopInfoForPlatform.put("180", stop); 
        numSeqForPlatform.put("161-1",17);    
        numSeqForPlatform.put("180-1",27);  
        
        stop = new Stop("162", "179", "Espinardo", 18, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_2);
        stop.setMinNumSeqForLine("1", 18);
        stop.setMaxNumSeqForLine("1", 26);
        
        stopInfoForPlatform.put("162", stop); 
        stopInfoForPlatform.put("179", stop); 
        numSeqForPlatform.put("162-1",18);        
        numSeqForPlatform.put("179-1",26);  
        
        stop = new Stop("164", "177", "Los Rect.", 19, 0, 0, Stop.StopType.LEISURE_RESIDENCE, Stop.StopArea.OUTSKIRTS_2);
        stop.setMinNumSeqForLine("1", 19);
        stop.setMaxNumSeqForLine("1", 25);
        stop.setMinNumSeqForLine("2", 43);
        stop.setMaxNumSeqForLine("2", 48);        
        
        stopInfoForPlatform.put("164", stop); 
        stopInfoForPlatform.put("177", stop);            
        numSeqForPlatform.put("164-1",19);        
        numSeqForPlatform.put("177-1",25);        
        numSeqForPlatform.put("164-2",43);        
        numSeqForPlatform.put("177-2",48);          
        
        stop = new Stop("204", "Guadalupe", 25, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_3); 
        stop.setMinNumSeqForLine("2", 44);
        stop.setMaxNumSeqForLine("2", 44);
        
        stopInfoForPlatform.put("204", stop);         
        numSeqForPlatform.put("204-2",44);        
        
        stop = new Stop("169", "Rey. Catolicos", 26, 0, 0, Stop.StopType.RESIDENCE_ONLY, Stop.StopArea.OUTSKIRTS_3);
        stop.setMinNumSeqForLine("2", 45);
        stop.setMaxNumSeqForLine("2", 45);
        
        stopInfoForPlatform.put("169", stop);         
        numSeqForPlatform.put("169-2",45);        
        
        stop = new Stop("170", "El porton", 27, 0, 0, Stop.StopType.LEISURE_RESIDENCE, Stop.StopArea.OUTSKIRTS_3);
        stop.setMinNumSeqForLine("2", 46);
        stop.setMaxNumSeqForLine("2", 46);
        
        stopInfoForPlatform.put("170", stop);                 
        numSeqForPlatform.put("170-2",46);        

        stop = new Stop("172", "192", "UCAM", 28, 0, 0, Stop.StopType.OFFICE_ACADEMIC_ONLY, Stop.StopArea.UNIVERSITY_2);
        stop.setMinNumSeqForLine("2", 47);
        stop.setMaxNumSeqForLine("2", 47);
        
        stopInfoForPlatform.put("172", stop);           
        stopInfoForPlatform.put("192", stop);   
        numSeqForPlatform.put("172-2",47);        
        numSeqForPlatform.put("192-2",47);                
        
        stop = new Stop("187", "Uni. Murcia", 20, 0, 0, Stop.StopType.OFFICE_ACADEMIC_ONLY, Stop.StopArea.UNIVERSITY_1);
        stop.setMinNumSeqForLine("1", 20);
        stop.setMaxNumSeqForLine("1", 20);
        
        stopInfoForPlatform.put("187", stop); 
        numSeqForPlatform.put("187-1",20);        
        
        stop = new Stop("188", "Serv. Inv.", 21, 0, 0, Stop.StopType.OFFICE_ACADEMIC_ONLY, Stop.StopArea.UNIVERSITY_1);
        stop.setMinNumSeqForLine("1", 21);
        stop.setMaxNumSeqForLine("1", 21);
        
        stopInfoForPlatform.put("188", stop); 
        numSeqForPlatform.put("188-1",21);        

        stop = new Stop("189", "Cen. Social", 22, 0, 0, Stop.StopType.OFFICE_ACADEMIC_ONLY, Stop.StopArea.UNIVERSITY_1);
        stop.setMinNumSeqForLine("1", 22);
        stop.setMaxNumSeqForLine("1", 22);
        
        stopInfoForPlatform.put("189", stop); 
        numSeqForPlatform.put("189-1",22);        

        stop = new Stop("190", "Bib. general", 23, 0, 0, Stop.StopType.RESIDENCE_OFFICE_ACADEMIC, Stop.StopArea.UNIVERSITY_1);
        stop.setMinNumSeqForLine("1", 23);
        stop.setMaxNumSeqForLine("1", 23);
        
        stopInfoForPlatform.put("190", stop); 
        numSeqForPlatform.put("190-1",23);                

        stop = new Stop("191", "Residencia uni.", 24, 0, 0, Stop.StopType.RESIDENCE_OFFICE_ACADEMIC, Stop.StopArea.UNIVERSITY_1);
        stop.setMinNumSeqForLine("1", 24);
        stop.setMaxNumSeqForLine("1", 24);
        
        stopInfoForPlatform.put("191", stop); 
        numSeqForPlatform.put("191-1",24);                
        
        stop = new Stop("65535", "Cocheras", 2, 0, 0, Stop.StopType.NOT_DEFINED, Stop.StopArea.NOT_DEFINED);
        stopInfoForPlatform.put("65535", stop);         
        numSeqForPlatform.put("65535-1",Integer.MAX_VALUE);  
        
        LOG.info("Stops loaded");
    }
    
}
