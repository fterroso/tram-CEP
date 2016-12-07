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
package tranvicep.tools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class Stop implements Serializable{
    
    private final String stopId;
    private final String stopId2;
    
    private final String stopName;
    
    private final double xCoord;
    private final double yCoord;
    
    private final int numSeq;
    
    private final StopType type;
    private final StopArea area;
    
    private final Map<String,Integer> minNumSeqForLine;
    private final Map<String,Integer> maxNumSeqForLine;

    public Stop(
            String stopId, 
            String stopName, 
            int numSeq, 
            double xCoord, 
            double yCoord,
            StopType type,
            StopArea area) {
        this.stopId = stopId;
        this.stopId2 = "";
        this.stopName = stopName;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.numSeq = numSeq;
        this.type = type;
        this.area = area;
        
        minNumSeqForLine = new HashMap<String,Integer>();
        maxNumSeqForLine = new HashMap<String,Integer>();
    }
    
    public Stop(
            String stopId, 
            String stopId2,
            String stopName, 
            int numSeq, 
            double xCoord, 
            double yCoord,
            StopType type,
            StopArea area) {
        this.stopId = stopId;
        this.stopId2 = stopId2;
        this.stopName = stopName;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.numSeq = numSeq;
        this.type = type;
        this.area = area;
        
        minNumSeqForLine = new HashMap<String,Integer>();
        maxNumSeqForLine = new HashMap<String,Integer>();        
    }

    public String getName() {
        return stopName;
    }

    public double getxCoord() {
        return xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public int getNumSeq() {
        return numSeq;
    }

    public void setMinNumSeqForLine(String line, int nSeq){
        minNumSeqForLine.put(line, nSeq);
    }
    
    public void setMaxNumSeqForLine(String line, int nSeq){
        maxNumSeqForLine.put(line, nSeq);
    }
    
    public int getMinNumSeqForLine(String line){
        return minNumSeqForLine.get(line);
    }
    
    public int getMaxNumSeqForLine(String line){
        return maxNumSeqForLine.get(line);
    }

    public StopType getType() {
        return type;
    }

    public StopArea getArea() {
        return area;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[");
        sb.append(stopName);
        sb.append("]");
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj){
        
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        Stop st = (Stop) obj;
        
        if(!st.getName().equals(getName())){
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.stopId != null ? this.stopId.hashCode() : 0);
        hash = 89 * hash + (this.stopId2 != null ? this.stopId2.hashCode() : 0);
        hash = 89 * hash + (this.stopName != null ? this.stopName.hashCode() : 0);
        return hash;
    }   
    
    public enum StopType{
        
        NOT_DEFINED (0),
        LEISURE_ONLY (1),
        LEISURE_RESIDENCE (2),
        RESIDENCE_ONLY (3),
        RESIDENCE_OFFICE_ACADEMIC (4),
        OFFICE_ACADEMIC_ONLY (5);
                
        int value;

        private StopType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    public enum StopArea{
        NOT_DEFINED (0),
        SHOPPING_MALLS (1),
        OUTSKIRTS_1 (2),
        CITY_CENTER (3),
        OUTSKIRTS_2 (4),
        UNIVERSITY_1 (5),
        OUTSKIRTS_3 (6),
        UNIVERSITY_2(7);
        
        final int value;

        private StopArea(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        
    }
}
