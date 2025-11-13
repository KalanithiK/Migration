package com.example.Migration.Delegate;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component("changePaymentMethodDelegate")
public class ChangePaymentMethodDelegate {
    
    private static final Logger logger = LoggerFactory.getLogger(ChangePaymentMethodDelegate.class);
    
    public ChangePaymentMethodDelegate() {
        logger.info("✅ ChangePaymentMethodDelegate bean created successfully");
    }
    
    private int getRetryCount(ActivatedJob job) {
        Object retryCount = job.getVariable("paymentRetryCount");
        if (retryCount instanceof Integer) {
            return (Integer) retryCount;
        } else if (retryCount instanceof Long) {
            return ((Long) retryCount).intValue();
        } else if (retryCount instanceof Number) {
            return ((Number) retryCount).intValue();
        }
        return 0;
    }

    @JobWorker(type = "changePaymentMethodDelegate", autoComplete = true)
    public Map<String, Object> executeJobMigrated(ActivatedJob job) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            logger.info(" ChangePaymentMethodDelegate - Starting execution");
            logger.info(" Changing payment method after failed payment");

            int newRetryCount = getRetryCount(job) + 1;
            resultMap.put("paymentMethodChanged", true);
            resultMap.put("paymentRetryCount", newRetryCount);
            
            logger.info("✅ Payment method changed - Retry count: {}", newRetryCount);
            logger.info("✅ ChangePaymentMethodDelegate - Execution completed successfully");
            
        } catch (Exception e) {
            logger.error("❌ Error in ChangePaymentMethodDelegate: {}", e.getMessage(), e);
            resultMap.put("paymentMethodChanged", false);
            resultMap.put("errorMessage", "Error changing payment method: " + e.getMessage());
        }
        return resultMap;
    }
}