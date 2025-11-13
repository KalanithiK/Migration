package com.example.Migration.controller;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.Migration.dao.OrderDAO;
import com.example.Migration.model.OrderItem;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private OrderDAO orderDAO;

    @GetMapping("/orders/{orderId}/items")
    public List<Map<String, Object>> getItems(@PathVariable("orderId") Long orderId) {
        try {
            List<OrderItem> items = orderDAO.findItems(orderId);

            return items.stream().map(i -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("itemId", i.getItemId());
                map.put("productId", i.getProductId());
                map.put("productName", i.getProductName());
                map.put("price", i.getPrice());
                return map;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("❌ Error fetching items: " + e.getMessage(), e);
        }
    }

    
    @PostMapping("/process/{processInstanceKey}/choose-products")
    public ResponseEntity<?> chooseProducts(
            @PathVariable("processInstanceKey") long processInstanceKey,
            @RequestBody Map<String, Object> request) {
        try {
            // Simulate the user selecting a product
            Long selectedProductId = Long.parseLong(request.get("selectedProductId").toString());
            String selectedProductName = request.get("selectedProductName").toString();

            // Get the active job for this process instance and task type
            // In Camunda 8 user tasks are of type "chooseProducts"
            // You must query the jobKey using Operate API or store it when fetched earlier
            // For simplicity, we'll assume you provide the jobKey directly
            long jobKey = Long.parseLong(request.get("jobKey").toString());

            Map<String, Object> vars = new HashMap<>();
            vars.put("selectedProductId", selectedProductId);
            vars.put("selectedProductName", selectedProductName);

            zeebeClient
                    .newCompleteCommand(jobKey)
                    .variables(vars)
                    .send()
                    .join();

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Product chosen successfully",
                    "processInstanceKey", processInstanceKey,
                    "selectedProductId", selectedProductId,
                    "selectedProductName", selectedProductName
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/start")
    public Map<String, Object> startProcess(@PathVariable("orderId") Long orderId) {
        try {
            Map<String, Object> vars = new HashMap<>();
            vars.put("orderId", orderId);

            ProcessInstanceEvent event = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId("order-process") // process ID from BPMN model
                    .latestVersion()
                    .variables(vars)
                    .send()
                    .join();

            return Map.of(
                    "message", "Process started successfully",
                    "processInstanceKey", event.getProcessInstanceKey(),
                    "orderId", orderId
            );

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to start process: " + e.getMessage(), e);
        }
    }

    @PostMapping("/task/complete")
    public ResponseEntity<?> completeTask(
            @RequestParam("jobKey") long jobKey,
            @RequestBody Map<String, Object> vars) {

        try {
            zeebeClient.newCompleteCommand(jobKey)
                    .variables(vars)
                    .send()
                    .join();

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "jobKey", jobKey,
                    "message", "Task completed successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    
    // ✅ Publish message to continue process
    @PostMapping("/process/{processInstanceKey}/propose-alternative")
    public ResponseEntity<?> proposeAlternative(
            @PathVariable("processInstanceKey") long processInstanceKey,
            @RequestBody Map<String, Object> request) {
        try {
            Long chosenAlternativeId = Long.parseLong(request.get("chosenAlternativeId").toString());
            String chosenAlternativeName = request.get("chosenAlternativeName").toString();
            long jobKey = Long.parseLong(request.get("jobKey").toString());

            Map<String, Object> vars = new HashMap<>();
            vars.put("chosenAlternativeId", chosenAlternativeId);
            vars.put("chosenAlternativeName", chosenAlternativeName);
            vars.put("proposalMade", true); // Important variable from your BPMN condition

            zeebeClient
                    .newCompleteCommand(jobKey)
                    .variables(vars)
                    .send()
                    .join();

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Alternative proposed successfully",
                    "processInstanceKey", processInstanceKey,
                    "chosenAlternativeId", chosenAlternativeId,
                    "chosenAlternativeName", chosenAlternativeName
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    
   
}
