package org.opentripplanner.updater.vehicle_rental;

import org.opentripplanner.routing.vehicle_rental.VehicleRentalRegion;
import org.opentripplanner.routing.vehicle_rental.VehicleRentalStation;

import java.util.List;

/**
 * An interface for modeling a vehicle rental data source. All vehicle rental updaters are assumed to have stations and
 * regions that can be updated over time.
 */
public interface VehicleRentalDataSource {

    /** Returns true if there might have been changes to the regions */
    boolean regionsUpdated();

    /** Returns true if there might have been changes to the stations */
    boolean stationsUpdated();

    /**
     * @return a List of all currently known vehicle rental stations. The updater will use this to update the Graph.
     */
    List<VehicleRentalStation> getStations();

    /**
     * @return a List of all currently known vehicle rental regions. The updater will use this to update the Graph.
     */
    List<VehicleRentalRegion> getRegions();

    // updates to the latest data
    void update();
}
