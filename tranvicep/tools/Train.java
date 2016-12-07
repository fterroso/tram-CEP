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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import tranvicep.itinerary.ItineraryProfile;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class Train {
    
    static Logger LOG = Logger.getLogger(Train.class);
    
    String id;
    String numberPlate;
    
    Map<String,double[]> loadFromStop;

    public Train(String id, String numberPlate) {
        this.id = id;
        this.numberPlate = numberPlate;
        loadFromStop = new HashMap<String,double[]>();
    }

    public String getId() {
        return id;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setLoadForStop(String stopId, double load){
        double[] n = (loadFromStop.containsKey(stopId)) ? loadFromStop.get(stopId) : new double[]{0.0,0.0};
        n[0] += load;
        n[1] = 0;
        loadFromStop.put(stopId, n);
//        LOG.debug("Train{" + "trainId=" + id + ", numberPlate=" + numberPlate + ", stop="+StopsProvider.getStopNameByKey(stopId)+"-"+stopId+", loads=" + Arrays.toString(loadFromStop.get(stopId)) + '}');
    }
    
    private void resetLoadForStop(String stopId){
        loadFromStop.remove(stopId);
    }
    
    public void resetAllLoad(){
        loadFromStop = new HashMap<String,double[]>();
//        LOG.debug("Reset trainId=" + id);
    }
    
    public void reduceLoad(String arrivingStop, long timestamp){
        
        resetLoadForStop(arrivingStop);
        
        Set<String> stopsId = new HashSet<String>(loadFromStop.keySet());
        for(String stopId : stopsId){
                
            double redRate = ItineraryProfile.probabilityOfItinerary(stopId, arrivingStop, timestamp);
                        
            if(redRate > 0){
                double[] n = (loadFromStop.containsKey(stopId)) ? loadFromStop.get(stopId) : new double[]{0.0,0.0};
                double aux = n[0]*(1-n[1]);
                n[1]+=redRate;
                double newLoad = n[0]*(1-n[1]);
                if(newLoad > 0){
                    loadFromStop.put(stopId, n); 
                }else{
                    resetLoadForStop(stopId);
                }
//                LOG.debug("Reduce trainId=" + id +", stop="+StopsProvider.getStopNameByKey(stopId)+", load="+String.format(Locale.US, "%.2f", aux)+"->"+String.format(Locale.US, "%.2f",(n[0]*(1-n[1]))));
            }                            
        }                
    }

    @Override
    public String toString() {
        return "Train{" + "trainId=" + id + ", numberPlate=" + numberPlate + ", loads=" + loadFromStop + '}';
    }
    
    
                
}
