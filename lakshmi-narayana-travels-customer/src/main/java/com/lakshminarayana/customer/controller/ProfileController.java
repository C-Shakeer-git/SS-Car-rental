package com.lakshminarayana.customer.controller;

import com.lakshminarayana.customer.model.Customer;
import com.lakshminarayana.customer.service.DataStoreService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private DataStoreService dataStoreService;

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            // If not logged in, provide sample/fallback or redirect to login
            customer = dataStoreService.getAllCustomers().stream().findFirst().orElse(null);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please log in to view your profile.");
                return "redirect:/login";
            }
            session.setAttribute("customer", customer);
        } else {
            // refresh from store
            customer = dataStoreService.getCustomerById(customer.getId()).orElse(customer);
            session.setAttribute("customer", customer);
        }

        model.addAttribute("customer", customer);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam String address,
                                @RequestParam(required = false) String aadhaarNumber,
                                @RequestParam(required = false) String drivingLicenseNumber,
                                @RequestParam(required = false) MultipartFile aadhaarFile,
                                @RequestParam(required = false) MultipartFile dlFile,
                                @RequestParam(required = false) String aadhaarDocUrl,
                                @RequestParam(required = false) String drivingLicenseDocUrl,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in.");
            return "redirect:/login";
        }

        customer = dataStoreService.getCustomerById(customer.getId()).orElse(customer);
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setAddress(address);

        if (aadhaarNumber != null && !aadhaarNumber.isBlank()) {
            customer.setAadhaarNumber(aadhaarNumber);
        }
        if (drivingLicenseNumber != null && !drivingLicenseNumber.isBlank()) {
            customer.setDrivingLicenseNumber(drivingLicenseNumber);
        }

        try {
            if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
                String path = dataStoreService.saveProofFile(aadhaarFile.getOriginalFilename(), aadhaarFile.getBytes());
                customer.setAadhaarDocUrl(path);
                customer.setKycStatus("PENDING");
                customer.setKycRemarks("New Aadhaar uploaded. Staff verification pending.");
            } else if (aadhaarDocUrl != null && !aadhaarDocUrl.isBlank()) {
                customer.setAadhaarDocUrl(aadhaarDocUrl);
            }

            if (dlFile != null && !dlFile.isEmpty()) {
                String path = dataStoreService.saveProofFile(dlFile.getOriginalFilename(), dlFile.getBytes());
                customer.setDrivingLicenseDocUrl(path);
                customer.setKycStatus("PENDING");
                customer.setKycRemarks("New Driving License uploaded. Staff verification pending.");
            } else if (drivingLicenseDocUrl != null && !drivingLicenseDocUrl.isBlank()) {
                customer.setDrivingLicenseDocUrl(drivingLicenseDocUrl);
            }

            dataStoreService.saveCustomer(customer);
            session.setAttribute("customer", customer);
            redirectAttributes.addFlashAttribute("successMessage", "Profile & Identity Proofs updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload document proofs: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}

