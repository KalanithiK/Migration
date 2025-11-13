package com.example.Migration.Delegate;

import java.math.BigDecimal;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("processPaymentDelegate")
public class ProcessPaymentDelegate implements JavaDelegate {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessPaymentDelegate.class);
    
    public ProcessPaymentDelegate() {
        logger.info("✅ ProcessPaymentDelegate bean created successfully");
    }
    
    @Override 
    public void execute(DelegateExecution ex) {
        try {
            BigDecimal amountDue = (BigDecimal) ex.getVariable("amountDue");
            
            logger.info("🔍 ProcessPaymentDelegate - Starting execution");
            logger.info("💳 Processing payment - Amount due: ₹{}", amountDue);
            
            // If no amount due or zero amount, payment is successful
            if (amountDue == null || BigDecimal.ZERO.compareTo(amountDue) == 0) {
                ex.setVariable("paymentStatus", "SUCCESS");
                ex.setVariable("paymentMessage", "No payment required");
                logger.info("✅ Payment successful - No amount due");
                return;
            }

            // Simulate payment processing with 80% success rate
            boolean success = Math.random() > 0.2;
            logger.debug("🎲 Payment simulation - Success rate: 80%, Result: {}", success);

            if (success) {
                ex.setVariable("paymentStatus", "SUCCESS");
                ex.setVariable("paymentMessage", "Payment processed successfully");
                logger.info("✅ Payment SUCCESSFUL - Amount: ₹{}", amountDue);
            } else {
                ex.setVariable("paymentStatus", "FAILED");
                ex.setVariable("paymentMessage", "Payment gateway declined the transaction");
                logger.error("❌ Payment FAILED - Amount: ₹{}", amountDue);
                throw new BpmnError("PAYMENT_FAILED", "Payment processing failed");
            }
            
            logger.info("✅ ProcessPaymentDelegate - Execution completed successfully");
            
        } catch (BpmnError e) {
            logger.error("❌ BPMN Error in ProcessPaymentDelegate: {}", e.getMessage());
            throw e; // Re-throw BPMN errors
        } catch (Exception e) {
            logger.error("❌ Unexpected error in ProcessPaymentDelegate: {}", e.getMessage(), e);
            throw new BpmnError("PAYMENT_FAILED", "Unexpected error during payment: " + e.getMessage());
        }
    }
}