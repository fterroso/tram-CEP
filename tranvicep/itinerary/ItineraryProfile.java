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
package tranvicep.itinerary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import tranvicep.cep.events.PossibleItineraryEventPOJO;
import tranvicep.config.ConfigProvider;
import tranvicep.itinerary.ItineraryFeature.ItineraryFeatureKey;
import tranvicep.tools.Stop;
import tranvicep.tools.StopsProvider;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class ItineraryProfile{
    
    static Logger LOG = Logger.getLogger(ItineraryProfile.class);
    private static final String ITINERARY_PATTERNS_FILE_NAME = "itinerary_patterns.dat";
    private static final String INVALID_ITINERARIES_FILE_NAME = "invalid_itinerary.dat";
    
    private static final String ERROR_TAG = "no_validation_at_los_Rectores";

    static String profileID;
    
    private static Map<String,Set<DestinationStats>> originDestTuples;
    
    private static Map<String, Map<String,Set<DestinationStats>>> invalidTuples;
    
    private static Map<String,Integer[]> stopStats;
        
    private static PrintWriter writerDataClustering;
    
    public static void init(String profile) {

        profileID = profile;
        
        stopStats = new HashMap<String,Integer[]>();
        
        try {
            FileInputStream fis = new FileInputStream(ITINERARY_PATTERNS_FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fis);
            originDestTuples = (Map<String,Set<DestinationStats>>) in.readObject();
            in.close();            
            
            LOG.info("Itinerary patterns recovered from file.");
        } catch (Exception ex) {
            LOG.error("Error while getting itinerary patterns. A new one will be generated ", ex);
            originDestTuples = new HashMap<String,Set<DestinationStats>>();
        }  
        
        try {
            
            FileInputStream fis = new FileInputStream(INVALID_ITINERARIES_FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fis);
            invalidTuples = (Map<String, Map<String,Set<DestinationStats>>>) in.readObject();
            in.close();  
            
            LOG.info("Invalid itinerary patterns recovered from file.");
        } catch (Exception ex) {
            LOG.error("Error while getting invalid itinerary patterns. A new one will be generated ", ex);
            invalidTuples = new HashMap<String, Map<String,Set<DestinationStats>>>();
        }
        
        try{
            writerDataClustering = new PrintWriter(new File(ConfigProvider.getOutputPath()+File.separator+"data_for_clustering.csv"));
        }catch (Exception ex) {
            LOG.error("Error while creating writer of clustering data ", ex);
        }
    }
    
    public static void serialize(){
        try {
            FileOutputStream fos = new FileOutputStream(ITINERARY_PATTERNS_FILE_NAME);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(originDestTuples);
            out.close();          

            LOG.info("Itinerary Patterns persisted.");
        } catch (Exception ex) {
            LOG.error("Exception ", ex);
        }
    }
    
    public static void insertNewItinerary(PossibleItineraryEventPOJO itinerary){
        
        Map<ItineraryFeature.ItineraryFeatureKey,Object> itFeatures = itinerary.getFeatures();
        
        insertNewTuple(
                originDestTuples,
                itinerary.getInitialStopId(), 
                itinerary.getFinalStopId(), itFeatures);
        
        if(!itinerary.getValid()){
            Map<String,Set<DestinationStats>> repository = (invalidTuples.containsKey(ERROR_TAG)) ? 
                    invalidTuples.get(ERROR_TAG) : new HashMap<String,Set<DestinationStats>>();
            
            insertNewTuple(
                repository,
                itinerary.getInitialStopId(), 
                itinerary.getFinalStopId(), itFeatures);
            
            invalidTuples.put(ERROR_TAG, repository);            
        }else{
            String itineraryInCSV = itinerary.toCVS();
            if(itineraryInCSV.length() > 0){
                writerDataClustering.println(itinerary.toCVS());
            }
        }
        
        String oStopName = StopsProvider.getStopNameById(itinerary.getInitialStopId());
        Integer[] oStopStats = (stopStats.containsKey(oStopName)) ? stopStats.get(oStopName) : new Integer[]{0,0};
        oStopStats[0]++;
        stopStats.put(oStopName, oStopStats);
        
        String dStopName = StopsProvider.getStopNameById(itinerary.getFinalStopId());
        Integer[] dStopStats = (stopStats.containsKey(dStopName)) ? stopStats.get(dStopName) : new Integer[]{0,0};
        dStopStats[1]++;
        stopStats.put(dStopName, dStopStats);
    }
    
     public static void insertNewItinerary(    
            String originStop,
            String finalStopId,            
            Map<ItineraryFeature.ItineraryFeatureKey,Object> itFeatures){
         
         insertNewTuple(
                originDestTuples,
                originStop, 
                finalStopId, 
                itFeatures);
    
     }
     
    protected static void insertNewTuple(
            Map<String,Set<DestinationStats>> repository,
            String originStop,
            String finalStopId,            
            Map<ItineraryFeature.ItineraryFeatureKey,Object> itFeatures){
                
        
        Set<DestinationStats> destinations = null;
        DestinationStats dest = null;
        
        Stop destStop = StopsProvider.getStopById(finalStopId);
        
        if(repository.containsKey(originStop)){
            destinations = repository.get(originStop);
            for(DestinationStats d : destinations){
                if(d.getStop().equals(destStop)){
                    dest = d;
                    break;
                }
            }
            if(dest == null){
                dest = new DestinationStats(destStop);
            }
        }else{
            destinations = new HashSet<DestinationStats>();
            dest = new DestinationStats(destStop);
        }
                
        for(ItineraryFeatureKey fKey : itFeatures.keySet()){
            dest.incRepetitionsForFeature(fKey, itFeatures.get(fKey));
        }
        dest.incTotalRepetitions();
        
        destinations.add(dest);
        repository.put(originStop, destinations);
    }
    
    public static ItineraryPrediction predictDestination(
            String originKey, 
            Map<ItineraryFeatureKey,Object> features){
        
        if(originDestTuples.containsKey(originKey)){
            Set<DestinationStats> destinations = originDestTuples.get(originKey);
                
            double maxProbability = 0;
            int norm = 0;
            DestinationStats predictedDest = null; 
            
            for(DestinationStats dest : destinations){
                double probability = 0;              
                                  
                for(ItineraryFeatureKey featureKey : features.keySet()){
                    //This must be modified in case of more itinerary feature types.
                    probability += dest.getRepetitionsForFeatureWithValue(featureKey, features.get(featureKey));
                }
                
                norm += probability;
                
                if(probability > maxProbability){
                    maxProbability = probability;
                    predictedDest = dest;
                }
            }
            
            if(predictedDest != null){
                double prob = maxProbability/norm;
                 if(prob >= ConfigProvider.getMinProbabilityForValidUserDestPred()){
                    return new ItineraryPrediction(predictedDest.getStop(), prob);
                 }
            }
        }
        
        return null;        
    }
    
    public static double probabilityOfItinerary(
            String originStop, 
            String destinationStop,
            long timestamp){
        return probabilityOfItinerary(originStop, destinationStop, getFeatures(timestamp));
    }
    
    public static double probabilityOfItinerary(
            String originStop, 
            String destinationStop,
            Map<ItineraryFeatureKey,Object> features){
        
        double prob = 0;       
        
        if(originDestTuples.containsKey(originStop)){
            
            Set<DestinationStats> destinations = originDestTuples.get(originStop);
                
            int norm = 0;
            
            for(DestinationStats dest : destinations){
                double probability = 0;              
                                  
                for(ItineraryFeatureKey featureKey : features.keySet()){
                    //This must be modified in case of more itinerary feature types.
                    probability += dest.getRepetitionsForFeatureWithValue(featureKey, features.get(featureKey));
                }
                
                norm += probability;
                
                if(dest.getStop().getName().equals(destinationStop)){
                   
                    prob = probability;
                }
            }
            
            if(norm > 0){
                prob /= norm;
            }else{
                prob = 0;
            }
        }

        return prob;
    }
    
    public static ItineraryPrediction predictDestination(
            String originKey,
            long originTimestamp){
  
        return predictDestination(originKey,getFeatures(originTimestamp));
    }
    
    protected static Map<ItineraryFeatureKey,Object> getFeatures(long timestamp){
        
        int hourInterval = ItineraryFeatureGenerator.getHourIntervalValue(timestamp);
        Map<ItineraryFeatureKey,Object> features = new EnumMap<ItineraryFeatureKey,Object>(ItineraryFeatureKey.class);
        features.put(ItineraryFeatureKey.HOUR_INTERVAL, hourInterval);
        
        return features;
    }
    
    public static ItineraryPrediction predictDestination(String originKey){
        
        if(originDestTuples.containsKey(originKey)){
            Set<DestinationStats> destinations = originDestTuples.get(originKey);
            int maxRepetitions = 0;
            int totalRepetitions = 0;
            DestinationStats predictedDest = null;
            for(DestinationStats dest : destinations){
                if(dest.getTotalRepetitions() > maxRepetitions){
                    predictedDest = dest;
                    maxRepetitions = dest.getTotalRepetitions();
                }
                totalRepetitions += dest.getTotalRepetitions();
            }
            
            if(predictedDest != null){
                double prob = (double)maxRepetitions/(double)totalRepetitions;
                if(prob >= ConfigProvider.getMinProbabilityForValidUserDestPred())
                    return new ItineraryPrediction(predictedDest.getStop(), prob);
            }

        }
        
        return null;
    }
    
    public static String toPlainText(){
        StringBuilder sb = new StringBuilder();
        
        sb.append(profileID);
        sb.append("\n");
        
        List<String> origins = new ArrayList(originDestTuples.keySet());
        Collections.sort(origins);
        
        for(String origin : origins){
            sb.append(StopsProvider.getStopById(origin).getName());
            sb.append("-");
            sb.append(origin);
            sb.append("\n");
            Set<DestinationStats> destinations = originDestTuples.get(origin);
            for(DestinationStats dest : destinations){
                sb.append(dest);
                sb.append("\n");
            }
            sb.append("-----------------------\n");
        }

        return sb.toString();
    }
    
    public static void toCSV(){
        toCSV("itinerary_profiles.csv",originDestTuples);
        
        for(String key : invalidTuples.keySet()){
            toCSV(key+".csv", invalidTuples.get(key));
        }
        writerDataClustering.flush();
        writerDataClustering.close();
        
        try{
            PrintWriter w = new PrintWriter(new File(ConfigProvider.getOutputPath()+File.separator+"stop_stats.txt"));
            for(String stopName : stopStats.keySet()){
                Integer[] stats = stopStats.get(stopName);
//                LOG.debug(stopName+ " "+StopsProvider.getStopByName(stopName));
                Stop s = StopsProvider.getStopByName(stopName);
                if(s!= null)
                    w.println(s.getNumSeq()+"\t"+stats[0]+"\t"+stats[1]);
            }
            w.flush();
            w.close();
        }catch(Exception e){
            LOG.error("Error in stop stats ",e);
        }
    }
    
    private static void toCSV(
            String fileName,
            Map<String,Set<DestinationStats>> repository){                
        
        StringBuilder sb = new StringBuilder();

        sb.append("origin_name,origin_code,destination_name,total_repetitions");
        
        ItineraryFeatureKey[] fkeys = ItineraryFeatureKey.values();
        
        for(ItineraryFeatureKey fKey : fkeys){
            List<Integer> values = fKey.getIntValues();
            for(int i : values){
                sb.append(",");
                sb.append(ItineraryFeature.getLabelForValue(fKey, i));
            }            
        }
        
        sb.append("\n");
        
        List<String> origins = new ArrayList(repository.keySet());
        Collections.sort(origins);
        
        for(String origin : origins){
            StringBuilder originLine = new StringBuilder();
            originLine.append(StopsProvider.getStopById(origin).getName());
            originLine.append(",");
            originLine.append(origin);
            originLine.append(",");
            
            Set<DestinationStats> destinations = repository.get(origin);
            for(DestinationStats dest : destinations){
                sb.append(originLine.toString());
                sb.append(dest.toCSV());
                sb.append("\n");
            }
        }
        
        try{
            PrintWriter writer = new PrintWriter(new File(ConfigProvider.getOutputPath()+File.separator+fileName));

            writer.println(sb.toString());
            writer.flush();
            writer.close();
            
            LOG.debug("Itinerary profiles serialized in CSV.");
        }catch(Exception e){
            LOG.error("Error while generating CSV file",e);
        }
        
    }
    
    private static class DestinationStats implements Serializable{
    
        final Stop stop;
        int totalRepetitions;

        List<ItineraryFeature> features;

        public DestinationStats(Stop stop) {
            this.stop = stop;
            features = new ArrayList<ItineraryFeature>();
        }

        public Stop getStop() {
            return stop;
        }

        public int getTotalRepetitions() {
            return totalRepetitions;
        }

        public int getRepetitionsForFeatureWithValue(
                ItineraryFeatureKey fKey, 
                Object value){

            for(ItineraryFeature f:features){
                if(f.getKey().equals(fKey)){
                    return f.getRepetitionsForValue(value);
                }
            }      
            return 0;
        }

        private void setRepetitionsForFeatureWithValue(
                ItineraryFeatureKey fKey, 
                Object value, 
                int nRepetitions){
            
            boolean set = false;
            for(ItineraryFeature f:features){
                if(f.getKey().equals(fKey)){
                    f.setNumRepetitionsForValue(value, nRepetitions);
                    set = true;
                    break;
                }
            }
            
            if(!set){
                ItineraryFeature f = new ItineraryFeature(fKey);
                f.setNumRepetitionsForValue(value, nRepetitions);
                features.add(f);
            }
        }
        
        public void incRepetitionsForFeature(ItineraryFeatureKey fKey, 
                Object value){
            int n = getRepetitionsForFeatureWithValue(fKey, value)+1;
            setRepetitionsForFeatureWithValue(fKey,value,n);
        }

        public void incTotalRepetitions(){
            totalRepetitions++;
        }  
        
        public String toCSV(){
            
            StringBuilder sb = new StringBuilder();
            sb.append(stop.getName());
            sb.append(",");
            sb.append(totalRepetitions);            

            for(ItineraryFeature f : features){
                sb.append(f.toCSV());
            }

            return sb.toString();
        }
        
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(stop.getName());
            sb.append(",");
            sb.append(totalRepetitions);
            
            sb.append(",{");
            for(ItineraryFeature f : features){
                sb.append(f);
            }
            sb.append("}");
            
            return sb.toString();
        }
    }

}
