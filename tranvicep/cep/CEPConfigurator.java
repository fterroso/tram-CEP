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
package tranvicep.cep;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.deploy.DeploymentOptions;
import com.espertech.esper.client.deploy.DeploymentOrder;
import com.espertech.esper.client.deploy.DeploymentOrderOptions;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.deploy.Module;
import com.espertech.esperio.AdapterCoordinatorImpl;
import com.espertech.esperio.AdapterInputSource;
import com.espertech.esperio.csv.CSVInputAdapter;
import com.espertech.esperio.csv.CSVInputAdapterSpec;
import com.espertech.esperio.ext.BasicTypeCoercerDateFormat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import tranvicep.cep.listeners.CompleteReturnTripListener;
import tranvicep.cep.listeners.PossibleItineraryListener;
import tranvicep.config.ConfigProvider;
import tranvicep.itinerary.ItineraryProfile;
import tranvicep.tools.StopsProvider;
import tranvicep.tools.TrainsProvider;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class CEPConfigurator {
    
    static Logger LOG = Logger.getLogger(CEPConfigurator.class);
    
    private static EPServiceProvider CEPEngine = null;
    
    public static EPServiceProvider startCEPEngine() throws Exception{
        
        if(CEPEngine == null){
            configureCEPEngine();
        }
        
        return CEPEngine;
        
    }
    
    public static void start() throws Exception{
       configureCEPEngine();
       startProcessingEvents();              
    }
        
    private static void configureCEPEngine() throws Exception{
        Configuration configuration = new Configuration();
        
        configuration.addEventType("RawValidationEvent", getValidationEventProps());
        configuration.addEventType("SellEvent", getSellEventProps());
        configuration.addEventType("TrainPlanEvent", getTrainPlanEventProps());
        
        configuration.addPlugInSingleRowFunction("predictDestination", ItineraryProfile.class.getName(), "predictDestination");
        configuration.addPlugInSingleRowFunction("getStopNameById", StopsProvider.class.getName(), "getStopNameById");        
        configuration.addPlugInSingleRowFunction("getStopNumSeq", StopsProvider.class.getName(), "getStopNumSeq");        
        
        configuration.addPlugInSingleRowFunction("getFinishNumSeqForLine", StopsProvider.class.getName(), "getFinishNumSeqForLine");        
        configuration.addPlugInSingleRowFunction("getStartNumSeqForLine", StopsProvider.class.getName(), "getStartNumSeqForLine");        
        configuration.addPlugInSingleRowFunction("getMiddleNumSeqForLine", StopsProvider.class.getName(), "getMiddleNumSeqForLine"); 
        configuration.addPlugInSingleRowFunction("getMinNumSeqForStop", StopsProvider.class.getName(), "getMinNumSeqForStop"); 
        configuration.addPlugInSingleRowFunction("getMaxNumSeqForStop", StopsProvider.class.getName(), "getMaxNumSeqForStop"); 

        
        configuration.addPlugInSingleRowFunction("getLineFromStopId", StopsProvider.class.getName(), "getLineFromStopId");        
        configuration.addPlugInSingleRowFunction("lineAndStopMatch", StopsProvider.class.getName(), "lineAndStopMatch");        

        configuration.addPlugInSingleRowFunction("getNumPlateForID", TrainsProvider.class.getName(), "getNumPlateForID");        
        
        configuration.getEngineDefaults().getThreading().setInternalTimerEnabled(false); 
        
        CEPEngine = EPServiceProviderManager.getProvider("TranviCEP", configuration);
        
        configureCEPModules();
    }
    
    private static void configureCEPModules() throws Exception{
        EPDeploymentAdmin deployAdmin = CEPEngine.getEPAdministrator().getDeploymentAdmin();
        
        List<Module> modules = new ArrayList<Module>();
        for(String modulePath : ConfigProvider.getCEPModules()){
            Module module = deployAdmin.read(new File(ConfigProvider.getRootPath() + modulePath));
            modules.add(module);
        }
        
        DeploymentOrder order = deployAdmin.getDeploymentOrder(modules, new DeploymentOrderOptions());
        
        for (Module mymodule : order.getOrdered()) { 
            deployAdmin.deploy(mymodule, new DeploymentOptions()); 
        }
        registerProfileGenerationQueries();

        LOG.info("All modules have been deployed.");
    }
    
    private static void registerProfileGenerationQueries(){
        CEPEngine.getEPAdministrator().getStatement("Find possible itinerary query").addListener(new PossibleItineraryListener());
        CEPEngine.getEPAdministrator().getStatement("Find possible itinerary II query").addListener(new PossibleItineraryListener());
        CEPEngine.getEPAdministrator().getStatement("Find possible itinerary III query").addListener(new PossibleItineraryListener());

        CEPEngine.getEPAdministrator().getStatement("Find possible itinerary with transbord query").addListener(new PossibleItineraryListener());                
        CEPEngine.getEPAdministrator().getStatement("Complete return trip query").addListener(new CompleteReturnTripListener());                              
    }
    
    private static void startProcessingEvents() throws Exception{
        
        final BasicTypeCoercerDateFormat validationbtcf = new BasicTypeCoercerDateFormat(); 
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");         
        validationbtcf.addDateFormat("timestamp", sdf); 
        validationbtcf.addDateFormat("expirityDate", sdf);                
        
        Map<String, Object> otherTypes = new HashMap<String, Object>();
        otherTypes.put("title", String.class);
        otherTypes.put("cardId", String.class);
        otherTypes.put("cardSerialNum", String.class);
        otherTypes.put("stopId", String.class);                
        otherTypes.put("lineId", String.class);
        otherTypes.put("trainNumberPlate", String.class);
        
        validationbtcf.setPropertyTypes(otherTypes);                
        
        AdapterInputSource validationEventSource = new AdapterInputSource(new File(ConfigProvider.getRootPath()+ ConfigProvider.getValidationEventsFilePath())); 

        CSVInputAdapterSpec inputValidationSpec = new CSVInputAdapterSpec(validationEventSource, "RawValidationEvent");
        inputValidationSpec.setTimestampColumn("timestamp");
        inputValidationSpec.setUsingExternalTimer(true);
        inputValidationSpec.setPropertyTypes(getValidationEventProps());        
        
        CSVInputAdapter validationEventsAdapter = new CSVInputAdapter(inputValidationSpec);
        validationEventsAdapter.setCoercer(validationbtcf);        
        
        //-------------- sell event --------------------
        final BasicTypeCoercerDateFormat sellbtcf = new BasicTypeCoercerDateFormat(); 
        sellbtcf.addDateFormat("timestamp", sdf); 
        
        Map<String, Object> otherSellTypes = new HashMap<String, Object>();
        otherSellTypes.put("title", String.class);
        otherSellTypes.put("operation", String.class);
        otherSellTypes.put("cost", Long.class);
        otherSellTypes.put("paymentMode", String.class);                
        otherSellTypes.put("clientId", String.class);
        otherSellTypes.put("stopId", String.class);
        
        sellbtcf.setPropertyTypes(otherSellTypes); 
        
        AdapterInputSource sellEventSource = new AdapterInputSource(new File(ConfigProvider.getRootPath()+ ConfigProvider.getSellEventsFilePath())); 

        CSVInputAdapterSpec inputSellEventSpec = new CSVInputAdapterSpec(sellEventSource, "SellEvent");
        inputSellEventSpec.setTimestampColumn("timestamp");
        inputSellEventSpec.setUsingExternalTimer(true);
        inputSellEventSpec.setPropertyTypes(getSellEventProps());
        
        CSVInputAdapter sellEventsAdapter = new CSVInputAdapter(inputSellEventSpec);
        sellEventsAdapter.setCoercer(sellbtcf);
        
        //-------------- train plan event --------------------
        final BasicTypeCoercerDateFormat trainPlanbtcf = new BasicTypeCoercerDateFormat(); 
        trainPlanbtcf.addDateFormat("timestamp", sdf); 
        trainPlanbtcf.addDateFormat("plannedDepartureTime", sdf); 
        trainPlanbtcf.addDateFormat("plannedArrivalTime", sdf); 
        trainPlanbtcf.addDateFormat("realDepartureTime", sdf); 
        trainPlanbtcf.addDateFormat("realArrivalTime", sdf); 
        
        Map<String, Object> otherTrainPlanTypes = new HashMap<String, Object>();                    
        otherTrainPlanTypes.put("trainId", String.class);
        otherTrainPlanTypes.put("origin", String.class);
        otherTrainPlanTypes.put("destination", String.class);
        otherTrainPlanTypes.put("driverId", String.class);  
        
        trainPlanbtcf.setPropertyTypes(otherTrainPlanTypes); 
        
        AdapterInputSource trainPlanEventSource = new AdapterInputSource(new File(ConfigProvider.getRootPath()+ ConfigProvider.getTrainTimeEventsFilePath())); 

        CSVInputAdapterSpec inputTrainPlanEventSpec = new CSVInputAdapterSpec(trainPlanEventSource, "TrainPlanEvent");
        inputTrainPlanEventSpec.setTimestampColumn("timestamp");
        inputTrainPlanEventSpec.setUsingEngineThread(true);
        inputTrainPlanEventSpec.setPropertyTypes(getTrainPlanEventProps());
        
        CSVInputAdapter trainPlanEventsAdapter = new CSVInputAdapter(inputTrainPlanEventSpec);
        trainPlanEventsAdapter.setCoercer(trainPlanbtcf);
        
        AdapterCoordinatorImpl coordinator = new AdapterCoordinatorImpl(CEPEngine, true);
        coordinator.setUsingExternalTimer(true);
        coordinator.coordinate(trainPlanEventsAdapter);
        coordinator.coordinate(validationEventsAdapter);
        coordinator.coordinate(sellEventsAdapter);
        
        LOG.info("Starting the event delivery...");
        coordinator.start();
        
        ItineraryProfile.toCSV();
        
    }
    
    private static Map<String, Object> getValidationEventProps(){
        Map<String, Object> validationProps = new HashMap<String, Object>();
        validationProps.put("title", String.class);
        validationProps.put("cardId", String.class);
        validationProps.put("cardSerialNum", String.class);
        validationProps.put("stopId", String.class);                
        validationProps.put("lineId", String.class);
        validationProps.put("trainNumberPlate", String.class);
        validationProps.put("timestamp", Long.class);
        validationProps.put("expirityDate", Long.class);
        
        return validationProps;
    } 
    
    private static Map<String, Object> getSellEventProps(){
        Map<String, Object> sellEventProps = new HashMap<String, Object>();
        sellEventProps.put("title", String.class);
        sellEventProps.put("operation", String.class);
        sellEventProps.put("cost", Long.class);
        sellEventProps.put("paymentMode", String.class);                
        sellEventProps.put("clientId", String.class);
        sellEventProps.put("stopId", String.class);
        sellEventProps.put("timestamp", Long.class);
        
        return sellEventProps;
    } 
    
    private static Map<String, Object> getTrainPlanEventProps(){
        Map<String, Object> eventProps = new HashMap<String, Object>();
        
        eventProps.put("trainId", String.class);
        eventProps.put("origin", String.class);
        eventProps.put("destination", String.class);
        eventProps.put("driverId", String.class);  
        eventProps.put("timestamp", Long.class);
        eventProps.put("plannedDepartureTime", Long.class);
        eventProps.put("plannedArrivalTime", Long.class);
        eventProps.put("realDepartureTime", Long.class);
        eventProps.put("realArrivalTime", Long.class);
        eventProps.put("lineId", String.class);  

        return eventProps;
    } 
}
