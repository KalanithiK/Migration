package com.example.Migration.Delegate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.Migration.dao.InventoryDAO;
import com.example.Migration.model.Inventory;

@Component("proposeAlternativesDelegate")
public class ProposeAlternativesDelegate implements JavaDelegate {
    
    private static final Logger logger = LoggerFactory.getLogger(ProposeAlternativesDelegate.class);
    private final InventoryDAO inventoryDAO;

    @Autowired
    public ProposeAlternativesDelegate(InventoryDAO inventoryDAO) {
        this.inventoryDAO = inventoryDAO;
        logger.info("✅ ProposeAlternativesDelegate bean created successfully");
    }

    @Override
    public void execute(DelegateExecution ex) {
        try {
            String productName = (String) ex.getVariable("selectedProductName");
            
            // ✅ FIXED: Handle Integer/Long conversion for productId
            Object productIdObj = ex.getVariable("selectedProductId");
            Long currentProductId = null;
            
            if (productIdObj instanceof Integer) {
                currentProductId = ((Integer) productIdObj).longValue();
                logger.debug("🔧 Converted Integer {} to Long {}", productIdObj, currentProductId);
            } else if (productIdObj instanceof Long) {
                currentProductId = (Long) productIdObj;
            } else if (productIdObj != null) {
                currentProductId = Long.parseLong(productIdObj.toString());
            }
            
            logger.info("🔍 ProposeAlternativesDelegate - Starting execution");
            logger.info("🔄 Finding alternatives for Product ID: {}, Name: {}", currentProductId, productName);
            
            if (currentProductId == null) {
                logger.error("❌ Current product ID is null - cannot find alternatives");
                ex.setVariable("proposalMade", false);
                ex.setVariable("errorMessage", "Current product ID is required");
                return;
            }
            
            String modelPrefix = "Item"; // Default prefix
            if (productName != null && !productName.trim().isEmpty()) {
                String[] nameParts = productName.split(" ");
                modelPrefix = nameParts.length > 0 ? nameParts[0] : "Item";
                logger.debug("🔍 Extracted model prefix: {} from product name: {}", modelPrefix, productName);
            } else {
                logger.warn("⚠️ Product name is null or empty, using default prefix: Item");
            }

            logger.info("🔍 Searching alternatives with model prefix: {}", modelPrefix);
            List<Inventory> alternatives = inventoryDAO.findAlternativesByModelPrefix(modelPrefix);
            
            // ✅ FIX: Create a final copy of currentProductId for use in lambda
            final Long finalCurrentProductId = currentProductId;
            
            // Filter out the current product from alternatives
            List<Inventory> filteredAlternatives = alternatives.stream()
                .filter(alt -> !alt.getProductId().equals(finalCurrentProductId))
                .collect(Collectors.toList());

            logger.info("📊 Found {} total alternatives, {} after filtering out current product", 
                       alternatives.size(), filteredAlternatives.size());

            // ✅ CHANGED: Always set proposalMade to true if alternatives found
            // This will create a human task for user to choose alternatives
            if (filteredAlternatives.isEmpty()) {
                ex.setVariable("proposalMade", false);
                ex.setVariable("alternativeOptions", null);
                ex.setVariable("alternativesCount", 0);
                ex.setVariable("showAlternativesTask", false);
                logger.warn("⚠️ No alternatives found for product {} with prefix {}", currentProductId, modelPrefix);
            } else {
                // ✅ CHANGED: Set variables to trigger human task
                ex.setVariable("proposalMade", true);
                ex.setVariable("showAlternativesTask", true);
                ex.setVariable("alternativesAvailable", true);
                
                // Create list of alternative maps for API response
                List<Map<String, Object>> alternativeList = filteredAlternatives.stream()
                    .map(alt -> {
                        Map<String, Object> altMap = new HashMap<>();
                        altMap.put("productId", alt.getProductId());
                        altMap.put("model", alt.getModel());
                        altMap.put("price", alt.getPrice());
                        altMap.put("quantity", alt.getQuantity());
                        altMap.put("available", alt.getQuantity() > 0);
                        return altMap;
                    })
                    .collect(Collectors.toList());
                
                ex.setVariable("alternativeOptions", alternativeList);
                ex.setVariable("alternativesCount", filteredAlternatives.size());
                
                logger.info("✅ Proposed {} alternatives for product {}", filteredAlternatives.size(), currentProductId);
                logger.info("👤 Human task will be created for user to choose alternative");
                logger.debug("📋 Alternative products: {}", alternativeList);
            }
            
            logger.info("✅ ProposeAlternativesDelegate - Execution completed successfully");
            
        } catch (Exception e) {
            logger.error("❌ Error in ProposeAlternativesDelegate: {}", e.getMessage(), e);
            ex.setVariable("proposalMade", false);
            ex.setVariable("showAlternativesTask", false);
            ex.setVariable("errorMessage", "Error proposing alternatives: " + e.getMessage());
        }
    }
}