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
 * 
 */

package tranvicep.cep.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import tranvicep.tools.StopsProvider;

/**
 *
 * @author Fer
 */
public class NoValidationAlarmEventPOJO {
    
    long timestamp;
   
    String cardId;
    
    String initStopId;
    String finalStopId;
    
    String description;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    } 
    
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getInitStopId() {
        return initStopId;
    }

    public void setInitStopId(String initStopId) {
        this.initStopId = initStopId;
    }

    public String getFinalStopId() {
        return finalStopId;
    }

    public void setFinalStopId(String finalStopId) {
        this.finalStopId = finalStopId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return "NoValidationAlarm{ cardId="+ cardId + ", timestamp=" + format.format(timestamp) + ", initStop=" + StopsProvider.getStopNameById(initStopId) + ", finalStop=" + StopsProvider.getStopNameById(finalStopId) + ", description=" + description + '}';
    }
    
    
}
