package com.smartgrocery.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class ExpiryStatusService {

    public String calculateStatus(LocalDate expiryDate, LocalDate today) {
        if (expiryDate == null) {
            return "fresh";
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);
        if (daysUntilExpiry < 0) {
            return "expired";
        }
        if (daysUntilExpiry <= 3) {
            return "expiring_soon";
        }
        return "fresh";
    }
}
