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
package tranvicep.cep.listeners;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.util.EnumMap;
import java.util.Map;
import org.apache.log4j.Logger;
import tranvicep.itinerary.ItineraryFeature;
import tranvicep.itinerary.ItineraryFeature.ItineraryFeatureKey;
import tranvicep.itinerary.ItineraryFeatureGenerator;
import tranvicep.itinerary.ItineraryProfile;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class CompleteReturnTripListener implements UpdateListener{
    
    static Logger LOG = Logger.getLogger(CompleteReturnTripListener.class);
    
    @Override
    public void update(EventBean[] ebs, EventBean[] ebs1) {
        for(EventBean eb : ebs){
            String originStopId = (String) eb.get("initialStopId");
            String finalStopId = (String) eb.get("finalStopId");
            String cardId = (String) eb.get("cardId");
            long timestamp = (Long) eb.get("startTime");
            
            Map<ItineraryFeature.ItineraryFeatureKey,Object> features = new EnumMap<ItineraryFeature.ItineraryFeatureKey,Object>(ItineraryFeature.ItineraryFeatureKey.class);
            features.put(ItineraryFeatureKey.HOUR_INTERVAL, ItineraryFeatureGenerator.getHourIntervalValue(timestamp)); 
            
            ItineraryProfile.insertNewItinerary(originStopId, finalStopId, features);
            
//            StringBuilder sb = new StringBuilder();
//        
//            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//
//            sb.append("Trip comp. cardId=");
//            sb.append(cardId);
//            sb.append(" [");
//            sb.append(StopsProvider.getStopByKey(originStopId).getStopName());
//            sb.append(",");
//            sb.append(format.format(timestamp));
//            sb.append("]->[");
//            sb.append(StopsProvider.getStopByKey(finalStopId).getStopName());
//            sb.append("]");
//
//            sb.append(",{");
//            for(ItineraryFeatureKey fkey : features.keySet()){
//                sb.append(fkey);
//                sb.append(":");
//                sb.append(ItineraryFeature.getLabelForValue(fkey, features.get(fkey)));
//                sb.append(",");
//            }
//            sb.append("}");
//            
//            LOG.debug(sb);
        }
    }
    
}
