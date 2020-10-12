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

import com.google.common.collect.Sets;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.VehicleType;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.vehicle_rental.VehicleRentalStation;

/**
 * Dropping off a rented vehicle edge.
 * 
 * Cost is the time to dropoff a vehicle.
 * 
 * @author Evan Siroky
 * 
 */
public class RentAVehicleOffEdge extends RentAVehicleAbstractEdge {

    private static final long serialVersionUID = 1L;

    public RentAVehicleOffEdge(Vertex v, VehicleRentalStation station) {
        super(v, station);
    }

    @Override
    public State traverse(State s0) {
        RoutingRequest options = s0.getOptions();

        // To drop off a vehicle, we need to have vehicle rental allowed in request.
        if (!options.allowVehicleRental) {
            // request settings forbids vehicle renting. Don't drop off a vehicle.
            return null;
        }

        // Check if specific vehicle networks are requested or banned.
        if (options.whiteListedProviders != null &&
            !options.whiteListedProviders.isEmpty() &&
            Sets.intersection(station.networks, options.whiteListedProviders).isEmpty()) {
            return null;
        }
        if (options.bannedProviders != null &&
            !options.bannedProviders.isEmpty() &&
            Sets.difference(station.networks, options.bannedProviders).isEmpty()) {
            return null;
        }
        if (options.whiteListedVehicles != null &&
            !options.whiteListedVehicles.isEmpty() &&
            !options.whiteListedVehicles.contains(station.type)) {
            return null;
        }
        if (options.bannedVehicles != null &&
            !options.bannedVehicles.isEmpty() &&
            options.bannedVehicles.contains(station.type)) {
            return null;
        }

        // check if the current state would allow a vehicle dropoff. In certain cases, RentAVehicleOffEdges can be
        // created when creating border drop-off stations. In these cases we still want to specify that the vehicle is
        // being dropped off at a designated area in order for reverse optimization searches to recognize the station
        // when approaching from the out-of-boundary side.
        if (!s0.isVehicleRentalDropoffAllowed(true)) {
            // something about the current state makes dropping off a vehicle not possible, return null.
            return null;
        }

        // make sure there is at least one spot to park at the station
        if (options.useVehicleRentalAvailabilityInformation && station.spacesAvailable == 0) {
            // no spots available at the station to park the vehicle, return null.
            return null;
        }

        // dropoff at this station is ok, begin editing a new state
        StateEditor s1e = s0.edit(this);

        if (options.arriveBy) {
            // if in arrive-by mode, the search is progressing backwards and we are entering a rented vehicle state
            s1e.beginVehicleRenting(0, station.networks, station.type, !station.isBorderDropoff);
        } else {
            if (s0.getVehicleType() != station.type && s0.getVehicleType() != VehicleType.UNKNOWN)
                return null;

            // if in depart-at mode, this is the conclusion of a vehicle rental
            s1e.endVehicleRenting();
        }

        // increment weight and time associated with dropping off the vehicle
        s1e.incrementWeight(options.vehicleRentalDropoffCost);
        s1e.incrementTimeInSeconds(options.vehicleRentalDropoffTime);
        State s1 = s1e.makeState();
        return s1;
    }

    public boolean equals(Object o) {
        if (o instanceof RentAVehicleOffEdge) {
            RentAVehicleOffEdge other = (RentAVehicleOffEdge) o;
            return other.getFromVertex().equals(fromv) && other.getToVertex().equals(tov);
        }
        return false;
    }

    public String toString() {
        return "RentAVehicleOffEdge(" + fromv + " -> " + tov + ")";
    }
}
