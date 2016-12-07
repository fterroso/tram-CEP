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

/**
 * Type of type units used by the application.
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public enum TimeUnit {
    
    HOUR (3600000),
    MINUTE (60000),
    SECOND (1000),
    MILLISECOND (1);
    
   long toMilliseconds;

    private TimeUnit(long toMilliseconds) {
        this.toMilliseconds = toMilliseconds;
    }

    public long toMilliseconds() {
        return toMilliseconds;
    }
    
}
