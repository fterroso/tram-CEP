package tranvicep;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import tranvicep.cep.CEPConfigurator;
import tranvicep.cep.adaptor.TrainArrivalEventAdaptor;
import tranvicep.config.ConfigProvider;
import tranvicep.itinerary.ItineraryProfile;
import tranvicep.tools.ClientsProvider;
import tranvicep.tools.StatsProvider;
import tranvicep.tools.StopsProvider;

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

/**
 * Starting point of the application.
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class Main {

    static Logger LOG = Logger.getLogger(Main.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
              
        try{
            LOG.info("Starting TranviCEP...");
            //Disable Esper log messagges. Too verbose.
            Logger.getLogger("com.espertech.esper").setLevel(Level.ERROR);
            
//             TrainArrivalEventAdaptor.formatFile("test/input/historico-itinerarios_TD.csv");
           
            ConfigProvider.init(args[0]);
            ItineraryProfile.init("simple_test");
            StopsProvider.init();
            ClientsProvider.init();
        
            CEPConfigurator.start();
            ItineraryProfile.serialize();
            StatsProvider.generate();
                                
        }catch(Exception e){
            LOG.error("Error in application", e);
        }
        LOG.info("TranviCEP is closed");
    }
}
