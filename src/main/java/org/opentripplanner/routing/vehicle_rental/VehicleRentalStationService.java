package org.opentripplanner.routing.vehicle_rental;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A service to help link between the vehicle rental updaters and API requests and graph searches. This class holds
 * information about vehicle rental stations and vehicle rental regions from all vehicle updaters.
 */
public class VehicleRentalStationService implements Serializable {
    private static final long serialVersionUID = -1288992939159246764L;

    /* vehicleRentalRegions is a map of vehicle network name to its service area. */
    private Map<String, VehicleRentalRegion> vehicleRentalRegions = new HashMap<>();

    private Set<VehicleRentalStation> vehicleRentalStations = new HashSet<VehicleRentalStation>();

    public Collection<VehicleRentalStation> getVehicleRentalStations() {
        return vehicleRentalStations;
    }

    public void addVehicleRentalStation(VehicleRentalStation vehicleRentalStation) {
        // Remove old reference first, as adding will be a no-op if already present
        vehicleRentalStations.remove(vehicleRentalStation);
        vehicleRentalStations.add(vehicleRentalStation);
    }

    public void removeVehicleRentalStation(VehicleRentalStation vehicleRentalStation) {
        vehicleRentalStations.remove(vehicleRentalStation);
    }

    public Map<String, VehicleRentalRegion> getVehicleRentalRegions() {
        return vehicleRentalRegions;
    }
    
    public void addVehicleRentalRegion(VehicleRentalRegion vehicleRentalRegion) {
        vehicleRentalRegions.put(vehicleRentalRegion.network, vehicleRentalRegion);
    }
}
