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
package tranvicep.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import tranvicep.itinerary.ItineraryFeature.ItineraryFeatureKey;
import tranvicep.itinerary.ItineraryFeatureGenerator;
import tranvicep.tools.TimeUnit;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class XMLConfigReader{

    static Logger LOG = Logger.getLogger(XMLConfigReader.class);
    
    static final String MIN_TIME_BETWEEN_VALIDATIONS_LABEL = "min_time_between_validations";
    static final String MAX_TIME_BETWEEN_VALIDATIONS_LABEL = "max_time_between_validations";
    static final String MIN_PROB_VALID_USER_DEST_PRED_LABEL = "min_probability_for_valid_user_dest_pred";

    static final String EXECUTION_MODE_LABEL = "mode";
    
    static final String FEATURES_LABEL = "features";
    static final String HOUR_INTERVALS_LABEL = "hour_intervals";
    static final String INTERVALS_LABEL = "interval";
 
    //Environment
    static String rootPath;  
           
    /** 
     * This method reads the xml file provided by the incoming parameter 
     * in order to set the configuration attributes of the application.
     *
     * @param xmlPath Path where the XML configuration file is located.
     */
    public static void read(String xmlPath){

        try{
            rootPath = ConfigProvider.getRootPath();
            
            SAXBuilder builder=new SAXBuilder(false);
            Document doc= builder.build(xmlPath);
            Element root =doc.getRootElement();  
            
            ConfigProvider.mode = ExecutionMode.valueOf(root.getChild(EXECUTION_MODE_LABEL).getText());
            
            Element input = root.getChild("input");
            parseInputElement(input);

            Element output = root.getChild("output");
            parseOutputElement(output); 
            
            parseParamsElement(root.getChild("params"));

            LOG.info("Config file parsed...OK");
        }catch(Exception e){
            LOG.error("Parse fail ", e);
        }
    }
    

    private static void parseInputElement(Element inputElement){
        Element filesElement = inputElement.getChild("files");
        parseInputFilesElement(filesElement);
       
    }
    
    private static void parseOutputElement(Element outputElement){
        ConfigProvider.outputPath = outputElement.getChild("path").getText();
    }

    private static void parseInputFilesElement(Element inputFilesElement){
        ConfigProvider.validationEventsFilePath = inputFilesElement.getChild("validations").getText();    
        ConfigProvider.sellEventsFilePath = inputFilesElement.getChild("sells").getText();
        ConfigProvider.trainTimeEventsFilePath = inputFilesElement.getChild("train_times").getText();
        ConfigProvider.clientsFilePath = inputFilesElement.getChild("clients").getText();
    }
    
    private static void parseParamsElement(Element paramsElement){
        
        String minTimeParam = paramsElement.getChild(MIN_TIME_BETWEEN_VALIDATIONS_LABEL).getText();
        
        String[] val = minTimeParam.split(",");        
        TimeUnit tUnit = TimeUnit.valueOf(val[1].toUpperCase());
        
        ConfigProvider.minTimeBetweenValidations = Long.valueOf(val[0]) * tUnit.toMilliseconds();
        
        String maxTimeParam = paramsElement.getChild(MAX_TIME_BETWEEN_VALIDATIONS_LABEL).getText();
        
        val = maxTimeParam.split(",");        
        tUnit = TimeUnit.valueOf(val[1].toUpperCase());                
        
        ConfigProvider.maxTimeBetweenValidations = Long.valueOf(val[0]) * tUnit.toMilliseconds();                
        
        String minUserDestProb = paramsElement.getChild(MIN_PROB_VALID_USER_DEST_PRED_LABEL).getText();
        ConfigProvider.minProbabilityForValidUserDestPred = Double.valueOf(minUserDestProb);
            
        parseItineraryFeatureElement(paramsElement.getChild(FEATURES_LABEL));
        
    }
    
    private static void parseItineraryFeatureElement(Element featuresElement){
        
        Element hourIntervalsE = featuresElement.getChild(HOUR_INTERVALS_LABEL);
        List<Element> intervals = hourIntervalsE.getChildren(INTERVALS_LABEL);
        
        Map<ItineraryFeatureKey, Object> featureParams = new HashMap<ItineraryFeatureKey, Object> ();
        
        double[][] hourIntervals = new double[intervals.size()][2];
        int i = 0;
        for(Element interval : intervals){
            String intervalSt = interval.getText();
            String[] intV = intervalSt.split(",");
            
            for(int j=0; j <2; j++){
                String[] hour = intV[j].split(":");
                hourIntervals[i][j]= (Double.parseDouble(hour[0]) * 60) + Double.parseDouble(hour[1]);
            }                        
            
            i++;
        }
        featureParams.put(ItineraryFeatureKey.HOUR_INTERVAL, hourIntervals);
        
        ItineraryFeatureGenerator.init(featureParams);
    }
}
