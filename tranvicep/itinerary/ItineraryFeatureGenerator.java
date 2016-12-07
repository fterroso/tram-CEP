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

import java.util.Calendar;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;
import java.util.Date;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.Map;
import models.ConsequentType;
import models.FiringType;
import models.tskModel.TSKModel;
import models.tskModel.TrapezoidalTSKModel;
import tranvicep.cep.events.PossibleItineraryEventPOJO;
import tranvicep.itinerary.ItineraryFeature.ItineraryFeatureKey;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class ItineraryFeatureGenerator {
    
    private static TSKModel hourTSKModel;
    
    private static final double hourSlope = 30; //minutes
    
    public static void init(Map<ItineraryFeatureKey, Object> featureParams){
        
        double[][] v = (double[][])featureParams.get(ItineraryFeatureKey.HOUR_INTERVAL);
        
        double[][] params_a = new double[v.length][1];
        double[][] params_b = new double[v.length][1];
        double[][] params_c = new double[v.length][1];
        double[][] params_d = new double[v.length][1];
        double[][] params_p = new double[v.length][2];
        
        double initialH = v[0][0];
        double finalH = v[0][1];
        
        params_a[0][0] = initialH;            
        params_b[0][0] = initialH;
        params_c[0][0] = finalH;
        params_d[0][0] = finalH+hourSlope;
        params_p[0][0] = 0;
        params_p[0][1] = 0;
        
        for(int i= 1; i< v.length-1; i++){
            initialH = v[i][0];
            finalH = v[i][1];
            
            params_a[i][0] = initialH-hourSlope;            
            params_b[i][0] = initialH;
            params_c[i][0] = finalH;
            params_d[i][0] = finalH+hourSlope;                       
            params_p[i][0] = 0;                        
            params_p[i][1] = i;                        
        }
        
        initialH = v[v.length-1][0];
        finalH = v[v.length-1][1];
        
        params_a[v.length-1][0] = initialH-hourSlope;            
        params_b[v.length-1][0] = initialH;
        params_c[v.length-1][0] = finalH;
        params_d[v.length-1][0] = finalH;
        params_p[v.length-1][0] = 0;
        params_p[v.length-1][1] = v.length-1;
        
        hourTSKModel = new TrapezoidalTSKModel(
                1, 
                ConsequentType.SINGLETON,
                FiringType.EACH_RULE_ITS_FIRE,
                params_a,
                params_b,
                params_c,
                params_d,
                params_p
                );        
    }
    
    public static Map<ItineraryFeatureKey, Object> generateFeatures(PossibleItineraryEventPOJO event){
        
        Map<ItineraryFeature.ItineraryFeatureKey,Object> features = new EnumMap<ItineraryFeature.ItineraryFeatureKey,Object>(ItineraryFeature.ItineraryFeatureKey.class);
        
        for(ItineraryFeatureKey fkey : ItineraryFeatureKey.values()){
            switch(fkey){
                case HOUR_INTERVAL:
                    long t = event.getStartTime();
                    features.put(fkey, getHourIntervalValue(t));                    
                    break;
                case DAY_OF_THE_WEEK:
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date(event.getStartTime()));
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    switch(dayOfWeek){
                        case MONDAY:
                        case TUESDAY:
                        case WEDNESDAY:
                        case THURSDAY:
                            features.put(fkey, 1);
                            break;
                        case FRIDAY:
                            features.put(fkey, 2);
                            break;
                        case SATURDAY:
                            features.put(fkey, 3);
                            break;
                        case SUNDAY:
                            features.put(fkey, 4);
                            break;                           
                    }
                    break;
            }
        }
                
        return features;
    }
    
    public static int getHourIntervalValue(long t){
        Date date = new Date(t);   
        Calendar calendar = GregorianCalendar.getInstance(); 
        calendar.setTime(date);   
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        int v = (hours * 60) + minutes;
        int interval = (int) Math.round(hourTSKModel.makeInference(new double[]{v}));

        return interval;
    }
}
