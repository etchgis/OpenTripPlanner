package org.opentripplanner.updater.vehicle_rental;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opentripplanner.routing.vehicle_rental.VehicleRentalStation;

import java.util.List;

public class TestGenericGbfsService extends TestCase {
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectNode config;

    private String network = "LIME";

    @BeforeClass
    public void setUp() {
        config = mapper.createObjectNode();
        config.put("network", network);
        config.put("sourceType", "gbfs");
        config.put("type", "micromobility-rental-updater");
        config.put("url", "file:src/test/resources/vehicle_rental");
    }

    @Test
    public void testParseVehiclesJson() {
        GenericGbfsService gbfsVehicleRentalDataSource = new GenericGbfsService();
        gbfsVehicleRentalDataSource.configure(null, config);

        // update data source and consume vehicles json
        gbfsVehicleRentalDataSource.update();
        assertTrue(gbfsVehicleRentalDataSource.stationsUpdated());
        List<VehicleRentalStation> rentalStations = gbfsVehicleRentalDataSource.getStations();
        assertEquals(4, rentalStations.size());
        for (VehicleRentalStation rentalStation : rentalStations) {
            System.out.println(rentalStation);
        }

        // make sure the first vehicle is the only station
        VehicleRentalStation firstVehicle = rentalStations.get(0);
        assertEquals(true, firstVehicle.allowPickup);
        assertEquals(true, firstVehicle.allowDropoff);
        assertEquals(5, firstVehicle.vehiclesAvailable);
        assertEquals("portland", firstVehicle.id);
        assertEquals(false, firstVehicle.isFloatingVehicle);
        assertEquals("Portland", firstVehicle.name.toString());
        assertEquals(true, firstVehicle.networks.contains(network));
        assertEquals(6, firstVehicle.spacesAvailable);
        assertEquals(-122.627205, firstVehicle.x);
        assertEquals(45.543855, firstVehicle.y);

        // make sure the second vehicle is a free bike
        VehicleRentalStation secondVehicle = rentalStations.get(1);
        assertEquals(true, secondVehicle.allowPickup);
        assertEquals(false, secondVehicle.allowDropoff);
        assertEquals(1, secondVehicle.vehiclesAvailable);
        assertEquals("TEST1", secondVehicle.id);
        assertEquals(true, secondVehicle.isFloatingVehicle);
        assertEquals("TEST1", secondVehicle.name.toString());
        assertEquals(true, secondVehicle.networks.contains(network));
        assertEquals(0, secondVehicle.spacesAvailable);
        assertEquals(-122.65073, secondVehicle.x);
        assertEquals(45.519958, secondVehicle.y);
    }
}
