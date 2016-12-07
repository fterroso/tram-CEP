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
package tranvicep.cep.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import tranvicep.itinerary.ItineraryFeature;
import tranvicep.itinerary.ItineraryFeature.ItineraryFeatureKey;
import tranvicep.itinerary.ItineraryFeatureGenerator;
import tranvicep.tools.Client;
import tranvicep.tools.ClientsProvider;
import tranvicep.tools.StopsProvider;

/**
 * Class that comprises the inner structure of a PossibleItineraryEvent that
 * represents a <origin,destination> tuple of a user.
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class PossibleItineraryEventPOJO {
    
    long startTime;
    long endTime;

    String cardId;
    String cardSerialNum;

    String initialStopId;
    String finalStopId;

    String initialLineId;
    String finalLineId;
    
    boolean valid;
    
    Map<ItineraryFeature.ItineraryFeatureKey,Object> features;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getInitialStopId() {
        return initialStopId;
    }

    public void setInitialStopId(String initialStopId) {
        this.initialStopId = initialStopId;
    }

    public String getFinalStopId() {
        return finalStopId;
    }

    public void setFinalStopId(String finalStopId) {
        this.finalStopId = finalStopId;
    }

    public String getInitialLineId() {
        return initialLineId;
    }

    public void setInitialLineId(String initialLineId) {
        this.initialLineId = initialLineId;
    }

    public String getFinalLineId() {
        return finalLineId;
    }

    public void setFinalLineId(String finalLineId) {
        this.finalLineId = finalLineId;
    }

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean isValid) {
        this.valid = isValid;
    }

    public String getCardSerialNum() {
        return cardSerialNum;
    }

    public void setCardSerialNum(String cardSerialNum) {
        this.cardSerialNum = cardSerialNum;
    }
    
    public Map<ItineraryFeature.ItineraryFeatureKey,Object> getFeatures(){
        if (features == null){
            features = ItineraryFeatureGenerator.generateFeatures(this);
        }
        
        return features;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        
        sb.append("Trip cardId=");
        sb.append(cardId);
        sb.append(" [");
        sb.append(StopsProvider.getStopById(initialStopId).getName());
        sb.append(",");
        sb.append(format.format(startTime));
        sb.append(",");
        sb.append(initialLineId);
        sb.append("]->[");
        sb.append(StopsProvider.getStopById(finalStopId).getName());
        sb.append(",");
        sb.append(format.format(endTime));
        sb.append(",");
        sb.append(finalLineId);        
        sb.append("]");
        
        sb.append(",{");
        for(ItineraryFeatureKey fkey : getFeatures().keySet()){
            sb.append(fkey);
            sb.append(":");
            sb.append(ItineraryFeature.getLabelForValue(fkey, getFeatures().get(fkey)));
            sb.append(",");
        }
        sb.append("}");
        
        return sb.toString();
    }

    public String toCVS(){
        StringBuilder sb = new StringBuilder();
        
        Client c = ClientsProvider.getClientBySerialNum(Long.parseLong(cardId));
        if(c!= null){
            Calendar c1 = Calendar.getInstance();
            c1.setTime(c.getBirthDate());
            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(System.currentTimeMillis());
            int years = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
            if(years > 85){
                return "";
            }
            sb.append(years);
            sb.append(",");
            sb.append(StopsProvider.getStopById(initialStopId).getArea().getValue());//.getType().getValue());
            sb.append(",");    
            sb.append(StopsProvider.getStopById(finalStopId).getArea().getValue());//.getType().getValue());
            sb.append(",");
            sb.append(StopsProvider.getStopById(initialStopId).getName());//.getType().getValue());
            sb.append(",");    
            sb.append(StopsProvider.getStopById(finalStopId).getName());            
            for(ItineraryFeatureKey fkey : getFeatures().keySet()){
                sb.append(",");
                sb.append((Integer) getFeatures().get(fkey));            
            }        
        }else{
            sb.append("");
        }
        
        return sb.toString();
    }
    
}
