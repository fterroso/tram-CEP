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

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Class the returns the corrent configuration provider depending on the 
 * source (xml file, web service, etc.)
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class ConfigProvider {
    
    static Map<ExecutionMode,List<String>> CEPModules;
    static long minTimeBetweenValidations;
    static long maxTimeBetweenValidations;
    
    static String validationEventsFilePath;
    static String sellEventsFilePath;
    static String trainTimeEventsFilePath;
    static String clientsFilePath;
    
    static double minProbabilityForValidUserDestPred;
    
    static String outputPath;
    
    static ExecutionMode mode;
    
    public static void init(String configSource){
        
        CEPModules = new EnumMap<ExecutionMode,List<String>>(ExecutionMode.class);
        CEPModules.put(ExecutionMode.PROFILE_GENERATOR_MODE, Arrays.asList("cep/modules/predict_itineraries.epl"));
        CEPModules.put(ExecutionMode.LOAD_ESTIMATOR_MODE, Arrays.asList("cep/modules/calculate_load.epl"));
        CEPModules.put(ExecutionMode.FULL_MODE, Arrays.asList("cep/modules/calculate_load.epl","cep/modules/predict_itineraries.epl"));
        
        if(configSource.endsWith(".xml")){
            XMLConfigReader.read(configSource);                
        }
    }
    
    public static List<String> getCEPModules(){
        return CEPModules.get(mode);
    }

    public static long getMinTimeBetweenValidations() {
        return minTimeBetweenValidations;
    }

    public static long getMaxTimeBetweenValidations() {
        return maxTimeBetweenValidations;
    }

    public static String getValidationEventsFilePath() {
        return validationEventsFilePath;
    }

    public static double getMinProbabilityForValidUserDestPred() {
        return minProbabilityForValidUserDestPred;
    }

    public static String getSellEventsFilePath() {
        return sellEventsFilePath;
    }

    public static String getTrainTimeEventsFilePath() {
        return trainTimeEventsFilePath;
    }

    public static String getOutputPath() {
        return outputPath;
    }

    public static ExecutionMode getMode() {
        return mode;
    }

    public static String getClientsFilePath() {
        return clientsFilePath;
    }
    
    public static String getRootPath() throws Exception{
        
        String oS = System.getProperty("os.name"); 
        oS = oS.toLowerCase();
        oS = oS.replace(" ", "_");
       
       Properties p = new Properties();
       
       p.load(new FileInputStream("operating_system.properties"));
       String path = p.getProperty(oS+".config.path");
       
       if(!path.endsWith(File.separator)){
           path = path.concat(File.separator);
       }
       
       return path;
    }
    
}
