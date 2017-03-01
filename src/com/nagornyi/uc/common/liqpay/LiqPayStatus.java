package com.nagornyi.uc.common.liqpay;

import java.util.Arrays;
import java.util.List;

public enum LiqPayStatus {
    /**
     * failed
     */
    FAILURE("failure"),
    REVERSED("reversed"),

    /**
     * in progress
     */
    CASH_WAIT("cash_wait"),
    PROCESSING("processing"),
    WAIT_SECURE("wait_secure"),
    /**
     * succeeded
     */
    SUCCESS("success");

    private static List<LiqPayStatus> failedStatuses = Arrays.asList(FAILURE, REVERSED);
    private static List<LiqPayStatus> processingStatuses = Arrays.asList(CASH_WAIT, PROCESSING, WAIT_SECURE);

    private String key;

    LiqPayStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static boolean isOneOfFailed(String liqPayStatusKey) {
        for (LiqPayStatus failureStatus: failedStatuses) {
            if (failureStatus.getKey().equals(liqPayStatusKey)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isOneOfProgress(String liqPayStatusKey) {
        for (LiqPayStatus failureStatus: processingStatuses) {
            if (failureStatus.getKey().equals(liqPayStatusKey)) {
                return true;
            }
        }

        return false;
    }
}
