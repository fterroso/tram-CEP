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

package tranvicep.cep.listeners;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.apache.log4j.Logger;
import tranvicep.cep.events.NoValidationAlarmEventPOJO;

/**
 *
 * @author Fer
 */
public class NoValidationAlarmListener implements UpdateListener{
    
    static Logger LOG = Logger.getLogger(NoValidationAlarmListener.class);
    
     @Override
    public void update(EventBean[] ebs, EventBean[] ebs1) {
        
        for(EventBean eb : ebs){
            NoValidationAlarmEventPOJO nvae = (NoValidationAlarmEventPOJO) eb.getUnderlying();
            
            LOG.warn(nvae);
        }   
        
    }
    
}
