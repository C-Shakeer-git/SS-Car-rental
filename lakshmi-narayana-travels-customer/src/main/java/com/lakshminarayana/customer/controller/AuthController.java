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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private DataStoreService dataStoreService;

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("customer") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Optional<Customer> customerOpt = dataStoreService.getCustomerByEmail(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (customer.getPassword() != null && customer.getPassword().equals(password)) {
                session.setAttribute("customer", customer);
                session.setAttribute("customerId", customer.getId());
                session.setAttribute("customerName", customer.getFullName());
                redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + customer.getFullName() + "!");
                return "redirect:/";
            }
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Invalid email address or password. Please try again.");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("customer") != null) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String phone,
                                 @RequestParam(required = false) String address,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (dataStoreService.getCustomerByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "An account with this email already exists.");
            return "redirect:/register";
        }

        Customer newCustomer = new Customer();
        newCustomer.setFullName(fullName);
        newCustomer.setEmail(email);
        newCustomer.setPassword(password);
        newCustomer.setPhone(phone);
        newCustomer.setAddress(address != null ? address : "");
        newCustomer.setKycStatus("PENDING");
        newCustomer.setKycRemarks("Please upload Aadhaar and Driving License in your profile to complete KYC.");

        Customer saved = dataStoreService.saveCustomer(newCustomer);
        session.setAttribute("customer", saved);
        session.setAttribute("customerId", saved.getId());
        session.setAttribute("customerName", saved.getFullName());

        redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! Welcome to Lakshmi Narayana Travels.");
        return "redirect:/profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("infoMessage", "You have been logged out safely.");
        return "redirect:/login";
    }
}

