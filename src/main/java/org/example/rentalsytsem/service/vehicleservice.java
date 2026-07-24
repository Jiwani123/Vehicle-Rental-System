package org.example.rentalsytsem.service;

import org.example.rentalsytsem.entity.booking;
import org.example.rentalsytsem.entity.vehicle;
import org.example.rentalsytsem.repository.bookingrepository;
import org.example.rentalsytsem.repository.vehiclerepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class vehicleservice {

    private final vehiclerepository vehiclerepository;
    private final bookingrepository bookingrepository;

    @Autowired
    public vehicleservice(vehiclerepository vehiclerepository, bookingrepository bookingrepository) {
        this.vehiclerepository = vehiclerepository;
        this.bookingrepository = bookingrepository;
    }

    public  vehicle createVehicle(vehicle v) {
        return vehiclerepository.save(v);
    }

    public List<vehicle> getAllVehicles() {
        return vehiclerepository.findAll();
    }

    public Optional<vehicle> getVehicleById(Long id) {
        return vehiclerepository.findById(id);
    }
    public vehicle updateVehicle(Long id, vehicle updateVehicle) {
        Optional<vehicle> existing = vehiclerepository.findById(id);
        if (existing.isPresent()) {
            vehicle ex = existing.get();
            ex.setBrandName(updateVehicle.getBrandName());
            ex.setModelName(updateVehicle.getModelName());
            ex.setVehicleType(updateVehicle.getVehicleType());
            ex.setAvailable(updateVehicle.isAvailable());
            ex.setRentalPricePerDay(updateVehicle.getRentalPricePerDay());
            ex.setImageUrl(updateVehicle.getImageUrl());
            ex.setStatus(updateVehicle.getStatus()); // Add this line to update status
            return vehiclerepository.save(ex);
        } else {
            // if not found, set id and save as new
            updateVehicle.setId(id);
            return vehiclerepository.save(updateVehicle);
        }
    }
    @Transactional
    public void deleteVehicle(Long id) {
        System.out.println("Attempting to delete vehicle with ID: " + id);
        
        // Check if vehicle exists
        Optional<vehicle> vehicleToDelete = vehiclerepository.findById(id);
        if (vehicleToDelete.isEmpty()) {
            throw new RuntimeException("Vehicle with ID " + id + " not found");
        }
        
        // First, delete all bookings associated with this vehicle
        List<booking> relatedBookings = bookingrepository.findByVehicleId(id);
        System.out.println("Found " + relatedBookings.size() + " related bookings for vehicle ID: " + id);
        
        if (!relatedBookings.isEmpty()) {
            System.out.println("Deleting " + relatedBookings.size() + " related bookings...");
            bookingrepository.deleteAll(relatedBookings);
            System.out.println("Related bookings deleted successfully");
        }
        
        // Then delete the vehicle
        System.out.println("Deleting vehicle with ID: " + id);
        vehiclerepository.deleteById(id);
        System.out.println("Vehicle deleted successfully");
    }

        // Set status (ADMIN action)
        public vehicle setStatus(Long id, String status) {
            Optional<vehicle> existing = vehiclerepository.findById(id);
            if (existing.isPresent()) {
                vehicle v = existing.get();
                v.setStatus(status);
                return vehiclerepository.save(v);
            }
            throw new RuntimeException("Vehicle not found");
        }

        // Find vehicles with PENDING status
        public List<vehicle> findPending() {
            return vehiclerepository.findAll().stream().filter(v -> "PENDING".equalsIgnoreCase(v.getStatus())).toList();
        }
}

