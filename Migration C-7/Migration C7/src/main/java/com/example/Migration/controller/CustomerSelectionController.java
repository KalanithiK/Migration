package com.example.Migration.controller;

import com.example.Migration.dto.CustomerSelectionDTO;
import com.example.Migration.service.CustomerSelectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.camunda.bpm.engine.RuntimeService;

@RestController
@RequestMapping("/api/customer")
public class CustomerSelectionController {

    @Autowired
    private CustomerSelectionService service;

    @Autowired
    private RuntimeService runtimeService;

    @PostMapping("/choose-products")
    public String chooseProducts(@RequestBody CustomerSelectionDTO dto) {
        // 1️⃣ Save the customer’s selected products
        service.saveCustomerSelection(dto);

        // 2️⃣ Start Camunda BPMN process (OrderProcess)
        runtimeService.startProcessInstanceByKey("OrderProcess");

        return "Customer products saved and OrderProcess started successfully!";
    }
}
