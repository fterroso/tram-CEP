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

package tranvicep.tools;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.log4j.Logger;
import tranvicep.config.ConfigProvider;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class StatsProvider {
    
    static Logger LOG = Logger.getLogger(StatsProvider.class);
    
    private static Map<String,List<Double>> totalValidations;
    private static Map<String,List<Double>> totalSells;
    
    private static Map<String,String>lastStopForTrain;
    private static Map<String,List<String[]>>fileContentForTrain;
    
    private static int nTotalValues = 0;
    
    
    static{
        totalValidations = new HashMap<String,List<Double>> ();
        totalSells = new HashMap<String,List<Double>> ();
        lastStopForTrain = new HashMap<String,String>();
        fileContentForTrain = new HashMap<String,List<String[]>>();
    }
    
     public  synchronized static void registerInfo(
            String trainId,
            String dayInterval,
            String weekDay,
            String stopNumSeq,
            String stopId,
            double value,
            boolean isValidation){
         
         String h = trainId+"\t"+dayInterval + "\t" + weekDay + "\t" + stopNumSeq + "\t" + stopId;
         addNewValue(trainId,h,stopId,value,isValidation);
         
     }
    
    protected static void addNewValue(
            String key, 
            String header,
            String stopId, 
            double value, 
            boolean isValidation){
        
        List<Double> validations = (totalValidations.containsKey(key)) ? totalValidations.get(key) : new LinkedList<Double>();
        List<Double> sells = (totalSells.containsKey(key)) ? totalSells.get(key) : new LinkedList<Double>();
        String lastStopId = (lastStopForTrain.containsKey(key)) ? lastStopForTrain.get(key) : "";   
        List<String[]> fileContent = (fileContentForTrain.containsKey(key)) ? fileContentForTrain.get(key) : new LinkedList<String[]>(); 
        
        if(lastStopId.equals(stopId)){
            if(isValidation){
                validations.remove(validations.size()-1);
                validations.add(value);
            }else{
                sells.remove(sells.size()-1);
                sells.add(value);
            }
            fileContent.remove(fileContent.size()-1);

        }else{
            if(isValidation){
                validations.add(value);
                sells.add(0.0);
            }else{
                sells.add(value);
                validations.add(0.0);
            }
            nTotalValues++;
        }
        String[] content = new String[]{header,String.valueOf(sells.get(sells.size()-1)),String.valueOf(validations.get(validations.size()-1))};
        fileContent.add(content);
        
//        LOG.debug(trainId+" ["+lastStopId+","+stopId + "] v:"+validations.size() + " s:"+sells.size());
        lastStopForTrain.put(key, stopId);
        totalSells.put(key, sells);
        totalValidations.put(key, validations);
        fileContentForTrain.put(key, fileContent);        
    }
    
    public static void generate() throws Exception{
        double[] tSellsArray = new double[nTotalValues];
        double[] tValidationsArray = new double[nTotalValues];

        PrintWriter writer = new PrintWriter(ConfigProvider.getOutputPath()+File.separator+"sell_val.txt");
        
        int i = 0;
        for(String trainId : totalValidations.keySet()){
            
            List<Double> validations = totalValidations.get(trainId);
            Double[] val = new Double[validations.size()];
            val = validations.toArray(val);
            
            double[] valPrim = ArrayUtils.toPrimitive(val);
            System.arraycopy(valPrim, 0, tValidationsArray, i, val.length);

            List<Double> sells = totalSells.get(trainId);
            Double[] sell = new Double[sells.size()];
            sell = sells.toArray(sell);
            double[] sellPrim = ArrayUtils.toPrimitive(sell);            
            System.arraycopy(sellPrim, 0, tSellsArray, i, sell.length);   
            
            i += sell.length;
            
            StringBuilder sb = new StringBuilder();
            sb.append("trainId=");
            sb.append(trainId);
            sb.append("\n");
            for(int j = 0; j< sell.length; j++){
                sb.append(val[j]);
                sb.append("\t");
                sb.append(sell[j]);
                sb.append("\n");
            }            
//            LOG.debug(sb.toString());
            
            List<String[]> fileContent = fileContentForTrain.get(trainId);
            
            for(String[] content : fileContent){
                String c = content[0]+"\t"+content[1]+"\t"+content[2];
                writer.println(c);                
            }
            
            writer.flush();                        
        }
        
        writer.close();
        
        PearsonsCorrelation corr = new PearsonsCorrelation();
        
        double corrValue = corr.correlation(tSellsArray, tValidationsArray);
        LOG.debug("Correlation: "+corrValue);
        
    }
   
}
