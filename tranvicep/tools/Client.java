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

import java.util.Date;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class Client {
    
    final String cardType;
    final String clientID;
    final int gendre;
    final int postalCode;
    final Date birthDate;
    final Date cardCreationDate;
    final Date lastUpdate;
    final String serialNumber;
    final String cardId;

    public Client(
            String cardType, 
            String clientID, 
            int gendre, 
            int postalCode, 
            Date birthDate, 
            Date cardCreationDate, 
            Date lastUpdate, 
            String serialNumber, 
            String cardId) {
        this.cardType = cardType;
        this.clientID = clientID;
        this.gendre = gendre;
        this.postalCode = postalCode;
        this.birthDate = birthDate;
        this.cardCreationDate = cardCreationDate;
        this.lastUpdate = lastUpdate;
        this.serialNumber = serialNumber;
        this.cardId = cardId;
    }

    public String getCardType() {
        return cardType;
    }

    public String getClientID() {
        return clientID;
    }

    public int getGendre() {
        return gendre;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Date getCardCreationDate() {
        return cardCreationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getCardId() {
        return cardId;
    }
 
              
}
