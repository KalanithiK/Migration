package com.example.Migration.Delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.Migration.dao.InventoryDAO;

@Component("checkAvailabilityDelegate")
public class CheckAvailabilityDelegate implements JavaDelegate {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckAvailabilityDelegate.class);
    private final InventoryDAO inventoryDAO;

    @Autowired
    public CheckAvailabilityDelegate(InventoryDAO inventoryDAO) {
        this.inventoryDAO = inventoryDAO;
        logger.info("✅ CheckAvailabilityDelegate bean created successfully");
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // ✅ FIXED: Handle Integer/Long conversion
            Object productIdObj = execution.getVariable("selectedProductId");
            Long productId = null;
            
            if (productIdObj instanceof Integer) {
                productId = ((Integer) productIdObj).longValue();
                logger.debug("🔧 Converted Integer {} to Long {}", productIdObj, productId);
            } else if (productIdObj instanceof Long) {
                productId = (Long) productIdObj;
            } else if (productIdObj != null) {
                productId = Long.parseLong(productIdObj.toString());
            }
            
            String productName = (String) execution.getVariable("selectedProductName");
            
            logger.info("🔍 CheckAvailabilityDelegate - Starting execution");
            logger.info("📦 Checking availability for Product ID: {}, Name: {}", productId, productName);
            
            if (productId == null) {
                logger.warn("⚠️ Product ID is null - marking as not available");
                execution.setVariable("availability", "PRODUCT_NOT_FOUND");
                // ✅ FIX: Don't set proposalMade here - let ProposeAlternativesDelegate handle it
                execution.setVariable("errorMessage", "Product ID is required");
                return;
            }

            logger.debug("🔍 Querying inventory for product ID: {}", productId);
            boolean available = inventoryDAO.isAvailable(productId);
            logger.info("📊 Availability result - Product ID: {}, Available: {}", productId, available);

            if (available) {
                execution.setVariable("availability", "AVAILABLE");
                execution.setVariable("proposalMade", false); // No proposal needed if available
                execution.setVariable("errorMessage", null);
                logger.info("✅ Product {} is available - proceeding with order", productId);
            } else {
                execution.setVariable("availability", "NOT_AVAILABLE");
                // ✅ FIX: Don't set proposalMade to false - let ProposeAlternativesDelegate set it to true if alternatives found
                execution.setVariable("errorMessage", "Product is out of stock");
                logger.info("❌ Product {} is not available - will trigger alternatives", productId);
            }
            
            logger.info("✅ CheckAvailabilityDelegate - Execution completed successfully");
            
        } catch (Exception e) {
            logger.error("❌ Error in CheckAvailabilityDelegate: {}", e.getMessage(), e);
            execution.setVariable("availability", "ERROR");
            execution.setVariable("proposalMade", false);
            execution.setVariable("errorMessage", "Error checking availability: " + e.getMessage());
        }
    }
}