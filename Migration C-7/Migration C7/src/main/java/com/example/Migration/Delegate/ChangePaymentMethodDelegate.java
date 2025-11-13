package com.example.Migration.Delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("changePaymentMethodDelegate")
public class ChangePaymentMethodDelegate implements JavaDelegate {
    
    private static final Logger logger = LoggerFactory.getLogger(ChangePaymentMethodDelegate.class);
    
    public ChangePaymentMethodDelegate() {
        logger.info("✅ ChangePaymentMethodDelegate bean created successfully");
    }
    
    @Override 
    public void execute(DelegateExecution ex) {
        try {
            logger.info("🔍 ChangePaymentMethodDelegate - Starting execution");
            logger.info("🔄 Changing payment method after failed payment");
            
            ex.setVariable("paymentMethodChanged", true);
            ex.setVariable("paymentRetryCount", getRetryCount(ex) + 1);
            
            logger.info("✅ Payment method changed - Retry count: {}", getRetryCount(ex));
            logger.info("✅ ChangePaymentMethodDelegate - Execution completed successfully");
            
        } catch (Exception e) {
            logger.error("❌ Error in ChangePaymentMethodDelegate: {}", e.getMessage(), e);
            ex.setVariable("paymentMethodChanged", false);
            ex.setVariable("errorMessage", "Error changing payment method: " + e.getMessage());
        }
    }
    
    private int getRetryCount(DelegateExecution ex) {
        Object retryCount = ex.getVariable("paymentRetryCount");
        return retryCount != null ? (Integer) retryCount : 0;
    }
}