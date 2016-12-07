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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class ItineraryFeature implements Serializable{
    
    final ItineraryFeatureKey key;
    Map<Object, Integer> nRepetitionsForValue;

    public ItineraryFeature(ItineraryFeatureKey key) {
        this.key = key;
        nRepetitionsForValue = new HashMap<Object,Integer>();
    }

    public ItineraryFeatureKey getKey() {
        return key;
    }

    public int getRepetitionsForValue(Object value){
        if(nRepetitionsForValue.containsKey(value)){
            return nRepetitionsForValue.get(value);
        }
        
        return 0;
    }
    
    public void setNumRepetitionsForValue(Object value, int nRep){
        nRepetitionsForValue.put(value, nRep);
    }
    
    public void incNumRepetitionsForValue(Object value){
        int n = getRepetitionsForValue(value)+1;
        setNumRepetitionsForValue(value,n);
    }
    
    @Override
    public int hashCode() {
        
        int hash = key.hashCode();
//        switch(getKey()){
//            //Add more features here.
//            case HOUR_INTERVAL:
//                for(Object v : getIntValues()){
//                    Double s1 = (Double) v;
//                    hash += s1.hashCode();
//                }
//                break;
//            default:
//                hash += getIntValues().hashCode();
//                break;
//        }
        
        return  hash;        
    }

    @Override
    public boolean equals(Object obj) {
        
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        ItineraryFeature other = (ItineraryFeature) obj;
        if(!other.getKey().equals(getKey())){
            return false;
        }
        
//        switch(getKey()){
//            //Add more features here.
//            case HOUR_INTERVAL:
//                String s1 = (String) getValue();
//                String s2 = (String) other.getValue();
//                if(!s1.equals(s2)) return false;
//            default:
//                if(!other.getValue().equals(getValue())){
//                    return false;
//                }
//                break;                
//        }                
        return true;
    }
    
    public String toCSV(){
        StringBuilder sb = new StringBuilder();
        
        for(Object val : key.getIntValues()){
            sb.append(",");
            sb.append(nRepetitionsForValue.containsKey(val) ? nRepetitionsForValue.get(val) : "0");            
        }    
        
        return sb.toString();
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(",{");       
        
        for(Object val : nRepetitionsForValue.keySet()){
            sb.append(getLabelForValue(key,val));
            sb.append(":");
            sb.append(nRepetitionsForValue.get(val));
            sb.append(",");
        }
        sb.append("}");        
        
        return sb.toString();
    }

    public static String getLabelForValue(ItineraryFeatureKey fkey, Object value){
        
        switch(fkey){
            case HOUR_INTERVAL:
                int intVal = (Integer) value;
                switch(intVal){
                    case 1:
                        return "EMor";                        
                    case 2:
                        return "Mor";
                    case 3:
                        return "Mid";
                    case 4:
                        return "Lun";
                    case 5:
                        return "AN";
                    case 6:
                        return "Ni";                                
                }
                break;
        }
        
        return "null";
    }
    
    public enum ItineraryFeatureKey implements Serializable{        
        //Add more features here.
        HOUR_INTERVAL (Arrays.asList(0,1,2,3,4,5)),
        DAY_OF_THE_WEEK (Arrays.asList(1,2,3,4)); 

        List<Integer> intValues;

        private ItineraryFeatureKey(List<Integer> values) {
            this.intValues = values;
        }

        public List<Integer> getIntValues() {
            return intValues;
        }
    }
}
