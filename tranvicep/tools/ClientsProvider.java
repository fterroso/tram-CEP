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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import tranvicep.config.ConfigProvider;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class ClientsProvider {
    
    static Logger LOG = Logger.getLogger(ClientsProvider.class);
    
    private static final int CARD_TYPE_POS = 0;
    private static final int CLIENT_ID_POS = 1;
    private static final int GENDRE_POS = 2;
    private static final int POSTAL_CODE_POS = 3;
    private static final int BIRTH_DATE_POS = 4;
    private static final int CARD_CREATION_POS = 5;
    private static final int LAST_UPDATE_POS = 6;
    private static final int SERIAL_NUM_POS = 7;
    private static final int CARD_ID_POS = 8;
    
    static Map<Long,Client> clients;
    
    public static void init() throws Exception{
                       
        clients = new HashMap<Long,Client>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");         
        
        String line;
        BufferedReader br = new BufferedReader(new FileReader(new File(ConfigProvider.getRootPath()+ConfigProvider.getClientsFilePath())));
        while((line = br.readLine()) != null){
            
            String[] parts = line.split(";");
            
            if(parts.length >= 9){
                
                String postalCodeSt = parts[POSTAL_CODE_POS].replaceAll("[\\s+\0\\.]","");
                int postalCode = 0;
                if(postalCodeSt.length() > 0){
                    postalCode = Integer.valueOf(postalCodeSt);
                }
                
                Client c = new Client(
                        parts[CARD_TYPE_POS].trim(),
                        parts[CLIENT_ID_POS].trim(),
                        Integer.valueOf(parts[GENDRE_POS].replaceAll("[\\s+\0]","").trim()),
                        postalCode,
                        sdf.parse(parts[BIRTH_DATE_POS].replaceAll("[\\s+\0]","").trim()),
                        sdf.parse(parts[CARD_CREATION_POS].replaceAll("[\\s+\0]","").trim()),
                        sdf.parse(parts[LAST_UPDATE_POS].replaceAll("[\\s+\0]","").trim()),
                        parts[SERIAL_NUM_POS].replaceAll("[\\s+\0]","").trim(),
                        parts[CARD_ID_POS].replaceAll("[\\s+\0]","").trim());
                
                Long cardIdDecimal =  Long.parseLong(parts[CARD_ID_POS].replaceAll("[\\s+\0]","").trim(), 16);
                clients.put(cardIdDecimal, c);
            }
            
        }
        br.close();
        
        LOG.info("Clients loaded");    
    }
    
    public static Client getClientBySerialNum(long cardId){
        return clients.get(cardId);
    }
}
