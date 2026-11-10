package com.lakshminarayana.customer.controller;

import com.lakshminarayana.customer.model.Car;
import com.lakshminarayana.customer.service.DataStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CustomerPortalController {

    @Autowired
    private DataStoreService dataStoreService;

    @GetMapping("/")
    public String home(Model model) {
        List<Car> allCars = dataStoreService.getAllCars();
        model.addAttribute("cars", allCars);
        model.addAttribute("totalCars", allCars.size());
        model.addAttribute("suvCount", allCars.stream().filter(c -> "SUV".equalsIgnoreCase(c.getCategory()) || "MUV".equalsIgnoreCase(c.getCategory()) || c.getCategory().contains("SUV")).count());
        model.addAttribute("cngCount", allCars.stream().filter(c -> "CNG".equalsIgnoreCase(c.getFuelType())).count());
        model.addAttribute("sevenSeaterCount", allCars.stream().filter(c -> c.getSeaters() >= 7).count());
        return "home";
    }

    @GetMapping("/cars")
    public String carsFleet(@RequestParam(required = false) Integer seaters,
                            @RequestParam(required = false) String fuel,
                            @RequestParam(required = false) String transmission,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) Double maxPrice,
                            Model model) {
        List<Car> cars = dataStoreService.getAllCars();

        if (seaters != null && seaters > 0) {
            cars = cars.stream().filter(c -> c.getSeaters() == seaters).collect(Collectors.toList());
        }
        if (fuel != null && !fuel.isBlank() && !"ALL".equalsIgnoreCase(fuel)) {
            cars = cars.stream().filter(c -> c.getFuelType() != null && c.getFuelType().equalsIgnoreCase(fuel)).collect(Collectors.toList());
        }
        if (transmission != null && !transmission.isBlank() && !"ALL".equalsIgnoreCase(transmission)) {
            cars = cars.stream().filter(c -> c.getTransmission() != null && c.getTransmission().equalsIgnoreCase(transmission)).collect(Collectors.toList());
        }
        if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
            cars = cars.stream().filter(c -> c.getCategory() != null && c.getCategory().toLowerCase().contains(category.toLowerCase())).collect(Collectors.toList());
        }
        if (maxPrice != null && maxPrice > 0) {
            cars = cars.stream().filter(c -> c.getPricePerDay() <= maxPrice).collect(Collectors.toList());
        }

        model.addAttribute("cars", cars);
        model.addAttribute("selectedSeaters", seaters);
        model.addAttribute("selectedFuel", fuel);
        model.addAttribute("selectedTransmission", transmission);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedMaxPrice", maxPrice != null ? maxPrice : 6000.0);
        return "cars";
    }

    @GetMapping("/car/{id}")
    public String carDetails(@PathVariable String id, Model model) {
        Car car = dataStoreService.getCarById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + id));
        model.addAttribute("car", car);
        return "car-details";
    }
}

