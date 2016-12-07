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

import java.text.DecimalFormat;
import tranvicep.tools.Stop;

/**
 * Class which represents the prediction about the final stop of a user's 
 * itinerary.
 * 
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class ItineraryPrediction {
    
    Stop stop;
    double probability;

    public ItineraryPrediction(
            Stop stopKey, 
            double probability) {
        this.stop = stopKey;
        this.probability = probability;
    }

    public Stop getStopKey() {
        return stop;
    }

    public double getProbability() {
        return probability;
    }

    @Override
    public String toString() {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return "{" + "stop=" + stop+ ", prob=" + twoDForm.format(probability) + '}';
    }


}
