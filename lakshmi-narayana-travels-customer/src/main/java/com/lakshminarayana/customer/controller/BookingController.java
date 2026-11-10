package com.lakshminarayana.customer.controller;

import com.lakshminarayana.customer.model.Booking;
import com.lakshminarayana.customer.model.Car;
import com.lakshminarayana.customer.model.Customer;
import com.lakshminarayana.customer.model.Payment;
import com.lakshminarayana.customer.service.DataStoreService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class BookingController {

    @Autowired
    private DataStoreService dataStoreService;

    @GetMapping("/book/{carId}")
    public String bookingForm(@PathVariable String carId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Car car = dataStoreService.getCarById(carId).orElse(null);
        if (car == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected car was not found.");
            return "redirect:/cars";
        }

        Customer loggedCustomer = (Customer) session.getAttribute("customer");
        model.addAttribute("car", car);
        model.addAttribute("customer", loggedCustomer);
        model.addAttribute("todayDate", LocalDate.now().toString());
        model.addAttribute("tomorrowDate", LocalDate.now().plusDays(2).toString());
        return "book";
    }

    @PostMapping("/book/create")
    public String createBooking(@RequestParam String carId,
                                @RequestParam String customerName,
                                @RequestParam String customerEmail,
                                @RequestParam String customerPhone,
                                @RequestParam String pickupLocation,
                                @RequestParam String dropLocation,
                                @RequestParam String pickupDate,
                                @RequestParam String dropDate,
                                @RequestParam(defaultValue = "09:00") String pickupTime,
                                @RequestParam(defaultValue = "18:00") String dropTime,
                                @RequestParam(required = false) List<String> addOns,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Car car = dataStoreService.getCarById(carId).orElse(null);
        if (car == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Car not found.");
            return "redirect:/cars";
        }

        Customer loggedCustomer = (Customer) session.getAttribute("customer");
        String customerId = loggedCustomer != null ? loggedCustomer.getId() : "GUEST-" + System.currentTimeMillis();

        long days = 1;
        try {
            LocalDate pDate = LocalDate.parse(pickupDate);
            LocalDate dDate = LocalDate.parse(dropDate);
            days = ChronoUnit.DAYS.between(pDate, dDate);
            if (days <= 0) days = 1;
        } catch (Exception ignored) {
            days = 1;
        }

        double baseAmount = car.getPricePerDay() * days;
        double addOnAmount = 0.0;
        List<String> selectedAddOns = new ArrayList<>();

        if (addOns != null) {
            for (String addOn : addOns) {
                if (addOn.contains("GPS")) {
                    addOnAmount += (200.0 * days);
                    selectedAddOns.add("GPS Navigation Device (₹200/day)");
                } else if (addOn.contains("Child")) {
                    addOnAmount += (150.0 * days);
                    selectedAddOns.add("Child Safety Seat (₹150/day)");
                } else if (addOn.contains("Driver")) {
                    addOnAmount += 300.0;
                    selectedAddOns.add("Additional Driver Coverage (₹300 flat)");
                } else if (addOn.contains("Insurance")) {
                    addOnAmount += (499.0 * days);
                    selectedAddOns.add("Zero-Depreciation Comprehensive Insurance (₹499/day)");
                } else if (addOn.contains("Fastag")) {
                    addOnAmount += 500.0;
                    selectedAddOns.add("Fastag Highway Pass Auto-Refill (₹500 deposit)");
                }
            }
        }

        double subTotal = baseAmount + addOnAmount;
        double gstAmount = Math.round((subTotal * 0.18) * 100.0) / 100.0; // 18% GST
        double totalAmount = Math.round((subTotal + gstAmount) * 100.0) / 100.0;

        Booking booking = new Booking();
        booking.setCustomerId(customerId);
        booking.setCustomerName(customerName);
        booking.setCustomerEmail(customerEmail);
        booking.setCustomerPhone(customerPhone);
        booking.setCarId(car.getId());
        booking.setCarName(car.getName());
        booking.setCarImageUrl(car.getImageUrl());
        booking.setPickupLocation(pickupLocation);
        booking.setDropLocation(dropLocation);
        booking.setPickupDate(pickupDate);
        booking.setDropDate(dropDate);
        booking.setPickupTime(pickupTime);
        booking.setDropTime(dropTime);
        booking.setTotalDays((int) days);
        booking.setDailyRate(car.getPricePerDay());
        booking.setSelectedFeatures(selectedAddOns);
        booking.setBaseAmount(baseAmount);
        booking.setAddOnAmount(addOnAmount);
        booking.setGstAmount(gstAmount);
        booking.setTotalAmount(totalAmount);
        booking.setStatus("PENDING");
        booking.setPaymentStatus("PENDING");

        Booking saved = dataStoreService.saveBooking(booking);

        return "redirect:/payment/" + saved.getId();
    }

    @GetMapping("/payment/{bookingId}")
    public String paymentPage(@PathVariable String bookingId, Model model, RedirectAttributes redirectAttributes) {
        Booking booking = dataStoreService.getBookingById(bookingId).orElse(null);
        if (booking == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
            return "redirect:/cars";
        }
        model.addAttribute("booking", booking);
        return "payment";
    }

    @PostMapping("/payment/process")
    public String processPayment(@RequestParam String bookingId,
                                 @RequestParam String paymentMethod,
                                 @RequestParam(required = false) String transactionRef,
                                 RedirectAttributes redirectAttributes) {
        Booking booking = dataStoreService.getBookingById(bookingId).orElse(null);
        if (booking == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
            return "redirect:/";
        }

        String txnId = transactionRef != null && !transactionRef.isBlank() 
                ? transactionRef 
                : "TXN-" + System.currentTimeMillis();

        booking.setPaymentStatus("PAID");
        booking.setStatus("APPROVED");
        booking.setPaymentMethod(paymentMethod);
        booking.setTransactionId(txnId);
        dataStoreService.saveBooking(booking);

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setCustomerName(booking.getCustomerName());
        payment.setCustomerEmail(booking.getCustomerEmail());
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionRef(txnId);
        payment.setStatus("SUCCESS");
        dataStoreService.recordPayment(payment);

        redirectAttributes.addFlashAttribute("successMessage", "Payment of ₹" + booking.getTotalAmount() + " processed successfully! Your car is reserved.");
        return "redirect:/invoice/" + booking.getId();
    }

    @GetMapping("/invoice/{bookingId}")
    public String viewInvoice(@PathVariable String bookingId, Model model, RedirectAttributes redirectAttributes) {
        Booking booking = dataStoreService.getBookingById(bookingId).orElse(null);
        if (booking == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invoice not found.");
            return "redirect:/my-bookings";
        }
        Car car = dataStoreService.getCarById(booking.getCarId()).orElse(null);
        model.addAttribute("booking", booking);
        model.addAttribute("car", car);
        return "invoice";
    }

    @GetMapping("/my-bookings")
    public String myBookings(@RequestParam(required = false) String searchEmail,
                             HttpSession session,
                             Model model) {
        Customer loggedCustomer = (Customer) session.getAttribute("customer");
        List<Booking> bookings;

        if (loggedCustomer != null) {
            bookings = dataStoreService.getBookingsByCustomerId(loggedCustomer.getId());
            if (bookings.isEmpty() && loggedCustomer.getEmail() != null) {
                bookings = dataStoreService.getAllBookings().stream()
                        .filter(b -> loggedCustomer.getEmail().equalsIgnoreCase(b.getCustomerEmail()))
                        .toList();
            }
        } else if (searchEmail != null && !searchEmail.isBlank()) {
            bookings = dataStoreService.getAllBookings().stream()
                    .filter(b -> searchEmail.trim().equalsIgnoreCase(b.getCustomerEmail()))
                    .toList();
            model.addAttribute("searchEmail", searchEmail);
        } else {
            // default sample bookings
            bookings = dataStoreService.getAllBookings();
        }

        model.addAttribute("bookings", bookings);
        return "my-bookings";
    }

    @PostMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable String id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Booking booking = dataStoreService.getBookingById(id).orElse(null);
        if (booking != null) {
            booking.setStatus("CANCELLED");
            booking.setPaymentStatus("REFUND_INITIATED");
            dataStoreService.saveBooking(booking);
            redirectAttributes.addFlashAttribute("infoMessage", "Booking " + id + " has been cancelled. Any eligible refund will be credited within 3-5 business days.");
        }
        return "redirect:/my-bookings";
    }
}

