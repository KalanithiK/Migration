package com.example.Migration.Delegate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component("processPaymentDelegate")
public class ProcessPaymentDelegate {

    private static final Logger logger = LoggerFactory.getLogger(ProcessPaymentDelegate.class);

    public ProcessPaymentDelegate() {
        logger.info("✅ ProcessPaymentDelegate bean created successfully");
    }

    @JobWorker(type = "processPaymentDelegate", autoComplete = true)
    public Map<String, Object> executeJobMigrated(ActivatedJob job) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            Object amountVar = job.getVariable("amountDue");
            BigDecimal amountDue = null;

            // Safely parse amount due (Zeebe variables can come as Double or String)
            if (amountVar instanceof BigDecimal) {
                amountDue = (BigDecimal) amountVar;
            } else if (amountVar instanceof Number) {
                amountDue = BigDecimal.valueOf(((Number) amountVar).doubleValue());
            } else if (amountVar != null) {
                try {
                    amountDue = new BigDecimal(amountVar.toString());
                } catch (NumberFormatException e) {
                    logger.warn("⚠️ Invalid amountDue format: {}", amountVar);
                    amountDue = BigDecimal.ZERO;
                }
            }

            logger.info("🔍 ProcessPaymentDelegate - Starting execution");
            logger.info("💳 Processing payment - Amount due: ₹{}", amountDue);

            // ✅ If no amount or zero, mark as success
            if (amountDue == null || amountDue.compareTo(BigDecimal.ZERO) == 0) {
                resultMap.put("paymentStatus", "SUCCESS");
                resultMap.put("paymentMessage", "No payment required");
                logger.info("✅ Payment successful - No amount due");
                return resultMap;
            }

            // ✅ Simulate random success/failure
            boolean success = Math.random() > 0.2;
            logger.debug("🎲 Payment simulation - 80% success rate. Result: {}", success);

            if (success) {
                resultMap.put("paymentStatus", "SUCCESS");
                resultMap.put("paymentMessage", "Payment processed successfully");
                logger.info("✅ Payment SUCCESSFUL - Amount: ₹{}", amountDue);
            } else {
                resultMap.put("paymentStatus", "FAILED");
                resultMap.put("paymentMessage", "Payment gateway declined the transaction");
                logger.error("❌ Payment FAILED - Amount: ₹{}", amountDue);
            }

        } catch (Exception e) {
            logger.error("❌ Unexpected error in ProcessPaymentDelegate: {}", e.getMessage(), e);
            resultMap.put("paymentStatus", "ERROR");
            resultMap.put("paymentMessage", "Unexpected error during payment: " + e.getMessage());
        }

        logger.info("✅ ProcessPaymentDelegate - Execution completed. Result: {}", resultMap);
        return resultMap;
    }
}
