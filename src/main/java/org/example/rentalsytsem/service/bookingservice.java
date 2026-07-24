package org.example.rentalsytsem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.rentalsytsem.entity.booking;
import org.example.rentalsytsem.entity.user;
import org.example.rentalsytsem.entity.vehicle;
import org.example.rentalsytsem.repository.bookingrepository;
import org.example.rentalsytsem.repository.userrepository;
import org.example.rentalsytsem.repository.vehiclerepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class bookingservice {

    private final bookingrepository bookingRepository;
    private final vehiclerepository vehicleRepository;
    private final userrepository userRepository;

    @Autowired
    public bookingservice(bookingrepository bookingRepository, vehiclerepository vehicleRepository, userrepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public booking createBooking(booking b) {
        b.setCreatedAt(LocalDateTime.now());
        b.setCreatedDate(LocalDateTime.now());
        b.setStatus(b.getStatus() == null ? "PENDING" : b.getStatus());

        // Set start/end dates from pickup/return dates if provided
        if (b.getPickupDate() != null && b.getStartDate() == null) {
            b.setStartDate(b.getPickupDate());
        }
        if (b.getReturnDate() != null && b.getEndDate() == null) {
            b.setEndDate(b.getReturnDate());
        }

        // Ensure user and vehicle are attached entities (load by id if necessary)
        if (b.getUser() != null && b.getUser().getId() != null) {
            user u = userRepository.findById(b.getUser().getId()).orElse(null);
            b.setUser(u);
        } else if (b.getUsername() != null) {
            // Try to find user by username
            user u = userRepository.findByUsername(b.getUsername()).orElse(null);
            b.setUser(u);
        }
        
        if (b.getVehicle() != null && b.getVehicle().getId() != null) {
            vehicle v = vehicleRepository.findById(b.getVehicle().getId()).orElse(null);
            b.setVehicle(v);
        } else if (b.getVehicleId() != null) {
            // Try to find vehicle by ID
            vehicle v = vehicleRepository.findById(b.getVehicleId()).orElse(null);
            b.setVehicle(v);
        }

        // Compute total price if possible
        LocalDate startDate = b.getStartDate() != null ? b.getStartDate() : b.getPickupDate();
        LocalDate endDate = b.getEndDate() != null ? b.getEndDate() : b.getReturnDate();
        
        if (b.getVehicle() != null && startDate != null && endDate != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            if (days <= 0) days = 1;
            double calculatedPrice = days * b.getVehicle().getRentalPricePerDay();
            b.setTotalPrice(calculatedPrice);
            
            // Use totalAmount if provided, otherwise use calculated price
            if (b.getTotalAmount() == null) {
                b.setTotalAmount(calculatedPrice);
            }
        }

        return bookingRepository.save(b);
    }

    public List<booking> getAllBookings() { return bookingRepository.findAll(); }

    public Optional<booking> getBookingById(Long id) { return bookingRepository.findById(id); }

    public List<booking> getBookingsForUser(Long userId) { return bookingRepository.findByUserId(userId); }

    public List<booking> getBookingsByUser(String username) { return bookingRepository.findByUsername(username); }

    public booking updateBooking(Long id, booking updated) {
        Optional<booking> existing = bookingRepository.findById(id);
        if (existing.isPresent()) {
            booking b = existing.get();
            
            // Update dates
            if (updated.getStartDate() != null) b.setStartDate(updated.getStartDate());
            if (updated.getEndDate() != null) b.setEndDate(updated.getEndDate());
            if (updated.getPickupDate() != null) b.setPickupDate(updated.getPickupDate());
            if (updated.getReturnDate() != null) b.setReturnDate(updated.getReturnDate());
            
            // Update locations
            if (updated.getPickupLocation() != null) b.setPickupLocation(updated.getPickupLocation());
            if (updated.getReturnLocation() != null) b.setReturnLocation(updated.getReturnLocation());
            
            // Update contact information
            if (updated.getContactNumber() != null) b.setContactNumber(updated.getContactNumber());
            if (updated.getEmail() != null) b.setEmail(updated.getEmail());
            
            // Update special requests (allow empty string to clear)
            if (updated.getSpecialRequests() != null) b.setSpecialRequests(updated.getSpecialRequests());
            
            // Sync pickup/return dates with start/end dates
            if (b.getPickupDate() != null && b.getStartDate() == null) {
                b.setStartDate(b.getPickupDate());
            }
            if (b.getReturnDate() != null && b.getEndDate() == null) {
                b.setEndDate(b.getReturnDate());
            }
            
            // Recompute price if dates changed
            LocalDate startDate = b.getStartDate() != null ? b.getStartDate() : b.getPickupDate();
            LocalDate endDate = b.getEndDate() != null ? b.getEndDate() : b.getReturnDate();
            
            if (b.getVehicle() != null && startDate != null && endDate != null) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
                if (days <= 0) days = 1;
                double calculatedPrice = days * b.getVehicle().getRentalPricePerDay();
                b.setTotalPrice(calculatedPrice);
                
                // Update totalAmount as well
                if (b.getTotalAmount() == null || b.getTotalAmount() == 0) {
                    b.setTotalAmount(calculatedPrice);
                }
            }
            
            return bookingRepository.save(b);
        }
        throw new RuntimeException("Booking not found");
    }

    public booking cancelBooking(Long id) {
        Optional<booking> existing = bookingRepository.findById(id);
        if (existing.isPresent()) {
            booking b = existing.get();
            b.setStatus("CANCELLED");
            return bookingRepository.save(b);
        }
        throw new RuntimeException("Booking not found");
    }

    public void deleteBooking(Long id) {
        Optional<booking> existing = bookingRepository.findById(id);
        if (existing.isPresent()) {
            bookingRepository.deleteById(id);
            return;
        }
        throw new RuntimeException("Booking not found");
    }

}

