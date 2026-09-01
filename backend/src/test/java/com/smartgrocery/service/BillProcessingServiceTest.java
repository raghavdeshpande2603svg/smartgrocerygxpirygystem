package com.smartgrocery.service;

import com.smartgrocery.ocr.ParsedBillItem;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class BillProcessingServiceTest {

    @Test
    void processUploadedBill_shouldUseActualUploadedTextContent() throws Exception {
        OCRTextParserService parserService = new OCRTextParserService();
        BillProcessingService service = new BillProcessingService(parserService);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "receipt.txt",
                "text/plain",
                "MANGO 2 KG 40.00\n".getBytes()
        );

        Map<String, Object> response = service.processUploadedBill(file, 1L);

        assertTrue((Boolean) response.get("success"));
        List<ParsedBillItem> items = (List<ParsedBillItem>) response.get("items");
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals("MANGO", items.get(0).getProductName());
    }
}
