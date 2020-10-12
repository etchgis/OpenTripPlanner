/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.routing.edgetype;

import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.vertextype.VehicleRentalStationVertex;
import org.opentripplanner.routing.vertextype.StreetVertex;

/**
 * This represents the connection between a street vertex and a vehicle rental station vertex.
 * 
 */
public class StreetVehicleRentalLink extends StreetRentalLink {

    private static final long serialVersionUID = 1L;

    public StreetVehicleRentalLink(StreetVertex fromv, VehicleRentalStationVertex tov) {
        super(fromv, tov);
    }

    public StreetVehicleRentalLink(VehicleRentalStationVertex fromv, StreetVertex tov) {
        super(fromv, tov);
    }

    public State traverse(State s0) {
        // Do not even consider vehicle rental vertices unless vehicle rental is enabled.
        if (!s0.getOptions().allowVehicleRental) {
            return null;
        }
        // Disallow traversing two StreetVehicleRentalLinks in a row.
        // This prevents the router from using vehicle rental stations as shortcuts to get around
        // turn restrictions.
        if (s0.getBackEdge() instanceof StreetVehicleRentalLink) {
            return null;
        }

        StateEditor s1 = s0.edit(this);
        s1.setBackMode(s0.getNonTransitMode());
        return s1.makeState();
    }

    @Override
    public double weightLowerBound(RoutingRequest options) {
        return options.modes.contains(TraverseMode.MICROMOBILITY) ? 0 : Double.POSITIVE_INFINITY;
    }

    public String toString() {
        return "StreetVehicleRentalLink(" + fromv + " -> " + tov + ")";
    }
}
