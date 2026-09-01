package com.smartgrocery.service;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CategoryMatchingAndExpiryServiceTest {

    @Test
    void classify_shouldMatchMilkToDairyUsingKeywords() {
        CategoryMatchingService service = new CategoryMatchingService();

        CategoryMatchingService.CategoryMatchResult result = service.matchCategory("Amul Milk 1L");

        assertEquals("Dairy", result.category());
        assertEquals("keyword", result.source());
        assertTrue(result.confidence() >= 0.8);
    }

    @Test
    void calculateStatus_shouldMarkItemsExpiringSoonWithinThreeDays() {
        ExpiryStatusService service = new ExpiryStatusService();
        LocalDate today = LocalDate.now();

        assertEquals("fresh", service.calculateStatus(today.plusDays(10), today));
        assertEquals("expiring_soon", service.calculateStatus(today.plusDays(2), today));
        assertEquals("expired", service.calculateStatus(today.minusDays(1), today));
    }
}
