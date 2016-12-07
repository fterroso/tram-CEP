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
package tranvicep;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 * Simple class that generates the datasets for the plot of itinerary
 * memberships. Only for the conference paper.
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class ItineraryProfilesPlotGenerator {
    
    static Logger LOG = Logger.getLogger(ItineraryProfilesPlotGenerator.class);
    private static final int TYPE_POS = 0;
    private static final int LAT_POS = 1;
    private static final int LON_POS = 2;
    private static final int STOP_NAME_POS = 3;
           
    private static final int ORIGIN_STOP_NAME_POS = 3;
    private static final int DEST_STOP_NAME_POS = 4;
    private static final int DAY_POS = 6; 
    
    protected static final int NUM_OF_ITINERARIES= 94281;
    
    private static List<Point> pointsLine1 = new ArrayList<Point>();
    private static Map<String,Integer> stopsLine1Positions = new HashMap<String,Integer>();
        
    private static List<Point> pointsLine2 = new ArrayList<Point>();
    private static Map<String,Integer> stopsLine2Positions = new HashMap<String,Integer>();
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
               
        String targetDay = "1";
        int cluster = 4;
        int numClusters = 5;
        
        String linePointsPath = "/home/fernando/Trabajo_SVN/Tranvia_de_Murcia/graficas";

        String line1File = "tranvia_linea1.txt";
        String line2File = "tranvia_linea2.txt";
        
        String itinerariesFile = "/home/fernando/NetBeansProjects_SVN/TranviCEP/test/input/data_for_clustering.csv";
        String membershipFile = "/home/fernando/NetBeansProjects_SVN/TranviCEP/test/input/tranvia_membership_1.csv";
        
        try{
            
            Scanner scanner = new Scanner(new File(linePointsPath + File.separator + line1File));            
                       
            while(scanner.hasNext()){
                String line = scanner.nextLine();
                String parts[] = line.split("\t");
                String type = parts[TYPE_POS];
                double lat = Double.valueOf(parts[LAT_POS]);
                double lon = Double.valueOf(parts[LON_POS]);
                Point  p = new Point(lat, lon);
                pointsLine1.add(p);
                if(type.equals("E")){
                    String stopName = parts[STOP_NAME_POS];
                    stopsLine1Positions.put(stopName, pointsLine1.size()-1);
                }                
            }
            
            
            scanner = new Scanner(new File(linePointsPath + File.separator + line2File));            
                       
            while(scanner.hasNext()){
                String line = scanner.nextLine();
                
                String parts[] = line.split("\t");
                String type = parts[TYPE_POS];
                double lat = Double.valueOf(parts[LAT_POS]);
                double lon = Double.valueOf(parts[LON_POS]);
                Point  p = new Point(lat, lon);
                pointsLine2.add(p);
                if(type.equals("E")){
                    String stopName = parts[STOP_NAME_POS];
                    stopsLine2Positions.put(stopName, pointsLine2.size()-1);
                }                
            }
            
            scanner = new Scanner(new File(itinerariesFile));            
            Scanner memberScanner = new Scanner(new File(membershipFile)); 
                      
            int c = 1;
            while(scanner.hasNext()){
                String line = scanner.nextLine();
                String parts[] = line.split(",");
                String day = parts[DAY_POS];
                if(day.equals(targetDay)){
                    String originSt = parts[ORIGIN_STOP_NAME_POS];
                    String destSt = parts[DEST_STOP_NAME_POS];
                    String mLine = memberScanner.nextLine();
                    String mParts[] = mLine.split(",");
                    
                    double membership = Double.valueOf(mParts[cluster-1]);
                    boolean membershipValid = true;
                    for(int j = 0;j< numClusters; j++){
                        double m = Double.valueOf(mParts[j]);
                        if(m>membership){
                            membershipValid= false;
                            break;
                        }
                    }
                                      
                    if(membershipValid){
//                        LOG.debug(c + " "+membershipValid + " "+membership+" "+originSt+" "+destSt);
                        includeItinerary(originSt,destSt, membership);
                    }
                      c++;
                }
            }
            
            //Normalization of the memberships
            Double maxMem = Double.MIN_VALUE;
            for(int i= 0; i< pointsLine1.size(); i++){
                if(pointsLine1.get(i).getFinalCount()> maxMem){
                    maxMem = pointsLine1.get(i).getFinalCount();
                }
            }    
            
            for(int i= 0; i< pointsLine2.size(); i++){
                if(pointsLine2.get(i).getFinalCount()> maxMem){
                    maxMem = pointsLine2.get(i).getFinalCount();
                }
            } 
            
            LOG.debug("max "+String.format("%.4f", maxMem));
            for(int i= 0; i< pointsLine1.size(); i++){
//                LOG.debug("1 "+String.format("%.4f", pointsLine1.get(i).membership));
                pointsLine1.get(i).count = pointsLine1.get(i).getFinalCount()/maxMem;
//                LOG.debug("2 "+pointsLine1.get(i).membership);
            }
            
            PrintWriter writer = new PrintWriter("test/output/itinerary_memberships_1_"+targetDay+"_"+cluster+".txt");                     
            for(int i= 0; i< pointsLine1.size(); i++){
                writer.println(pointsLine1.get(i));
            }
            writer.flush();
            writer.close();
            
            //Normalization of the memberships
                       
            for(int i= 0; i< pointsLine2.size(); i++){
                pointsLine2.get(i).count = pointsLine2.get(i).getFinalCount()/maxMem;
            }
            
            writer = new PrintWriter("test/output/itinerary_memberships_2_"+targetDay+"_"+cluster+".txt");
            for(int i= 0; i< pointsLine2.size(); i++){
                writer.println(pointsLine2.get(i));
            }
            writer.flush();
            writer.close();
            
        }catch(Exception e){
            LOG.error("Error while generation plot ", e);
        }
        
    }
    
    private static void includeItinerary(
            String origin, 
            String destination,
            double membership){
        LOG.debug(origin+" "+destination+" "+membership);
        if(stopsLine1Positions.containsKey(origin) &&
                stopsLine1Positions.containsKey(destination)){
            
            int originIndex = stopsLine1Positions.get(origin);
            int destIndex = stopsLine1Positions.get(destination);
            
            if(origin.equals("Uni. Murcia") || 
                    origin.equals("Serv. Inv.") ||
                    origin.equals("Cen. Social") ||
                    origin.equals("Bib. general") ||
                    origin.equals("Residencia uni.")){
               
                StringBuilder sb = new StringBuilder();
                sb.append("1a: ");
                for(int i= originIndex; i< pointsLine1.size(); i++){
                   
                    Point p = pointsLine1.get(i);
                    p.setMembership(membership);
                    sb.append(p);
                    sb.append("-->");
                }
                LOG.debug(sb.toString());
                originIndex = stopsLine1Positions.get("Los Rect.");
                
            }            
            
            if(originIndex > destIndex){
                int aux = originIndex;
                originIndex = destIndex;
                destIndex = aux;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("1b: ");
            for(int i= originIndex; i< destIndex; i++){
                Point p = pointsLine1.get(i);
                p.setMembership(membership);
                sb.append(p);
                    sb.append("-->");
            }
            LOG.debug(sb.toString());
            
        }else if(stopsLine2Positions.containsKey(origin) &&
                stopsLine2Positions.containsKey(destination)){
            
            int originIndex = stopsLine2Positions.get(origin);
            int destIndex = stopsLine2Positions.get(destination);
            
            if(originIndex > destIndex){
                int aux = originIndex;
                originIndex = destIndex;
                destIndex = aux;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("2: ");
            for(int i= originIndex; i< destIndex; i++){
                Point p = pointsLine2.get(i);
                p.setMembership(membership);
                sb.append(p);
                    sb.append("-->");
            }
            LOG.debug(sb.toString());
        }else{
            if(stopsLine2Positions.containsKey(origin)){ //Origin in line 2, destination in line 1
                int originIndex = stopsLine2Positions.get(origin);
                int destIndex = stopsLine1Positions.get(destination);
                 
                StringBuilder sb = new StringBuilder();
                sb.append("3a: ");
                for(int i= 1; i<= originIndex; i++){
                    Point p = pointsLine2.get(i);
                    p.setMembership(membership);
                    sb.append(p);
                    sb.append("-->");
                }
                LOG.debug(sb.toString());
                originIndex = stopsLine1Positions.get("Los Rect.");
                if(originIndex > destIndex){
                    int aux = originIndex;
                    originIndex = destIndex;
                    destIndex = aux;
                }

                sb = new StringBuilder();
                sb.append("3b: ");
                for(int i= originIndex; i< destIndex; i++){
                    Point p = pointsLine1.get(i);
                    p.setMembership(membership);
                    sb.append(p);
                    sb.append("-->");
                }
                LOG.debug(sb.toString());
            }else{ // Origin in line 1, destination in line 2
                
                LOG.debug(origin + " "+stopsLine1Positions);
                int originIndex = stopsLine1Positions.get(origin);
                int destIndex = stopsLine2Positions.get(destination);
                
                if(origin.equals("Uni. Murcia") || 
                    origin.equals("Serv. Inv.") ||
                    origin.equals("Cen. Social") ||
                    origin.equals("Bib. general") ||
                    origin.equals("Residencia uni.")){
               
                    StringBuilder sb = new StringBuilder();
                    sb.append("4a: ");
                    for(int i= originIndex; i< pointsLine1.size(); i++){
                        Point p = pointsLine1.get(i);
                        p.setMembership(membership);
                        sb.append(p);
                    sb.append("-->");
                    }
                    LOG.debug(sb.toString());
                }else{
                    int transBordIndex = stopsLine1Positions.get("Los Rect.");
                    StringBuilder sb = new StringBuilder();
                    sb.append("4b: ");
                    for(int i= originIndex; i<= transBordIndex; i++){
                        Point p = pointsLine1.get(i);
                        p.setMembership(membership);
                        sb.append(p);
                    sb.append("-->");
                    }
                    LOG.debug(sb.toString());
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("4c: ");
                for(int i= 0; i<=destIndex; i++){
                    Point p = pointsLine2.get(i);
                    p.setMembership(membership);
                    sb.append(p);
                    sb.append("-->");
                }
                LOG.debug(sb.toString());
            }  
               
        }
    }
            
    
    private static class Point{
        
        double lat;
        double lon;
        double membership =0;
        double count = 0;

        public Point(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public void setMembership(double membership) {
            count++;
//            this.membership = this.membership + ((membership-this.membership)/count);            
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public double getMembership() {
            return membership;
        }
        
        public double getFinalCount(){
            return count/ItineraryProfilesPlotGenerator.NUM_OF_ITINERARIES;
        }
        
        @Override
        public String toString(){
            return lat + "\t"+ lon + "\t" + count;
        }
        
    }
}
