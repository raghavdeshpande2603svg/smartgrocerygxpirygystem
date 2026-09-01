package com.smartgrocery.service;

import com.smartgrocery.ocr.ParsedBillItem;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OCRTextParserServiceTest {

    @Test
    void parseRawText_shouldExtractProductNameQuantityRateAndAmount() {
        String rawText = "" +
                "1 AMUL MILK 1L 52.00 52.00\n" +
                "2 ONION 1 KG 30.00 30.00\n" +
                "3 RICE 5KG 120.00 120.00\n";

        OCRTextParserService service = new OCRTextParserService();
        List<ParsedBillItem> items = service.parseRawText(rawText);

        assertEquals(3, items.size());
        assertEquals("AMUL MILK", items.get(0).getProductName());
        assertEquals(1.0, items.get(0).getQuantity());
        assertEquals(52.0, items.get(0).getPrice());
        assertEquals("milk", items.get(0).getNormalizedName());
    }

    @Test
    void parseRawText_shouldHandleHandwrittenBillFormat() {
        String rawText = "" +
                "CASH BILL\n" +
                "Date: 5-8-2020\n" +
                "Qty Particulars Rate Amount\n" +
                "3kg Tomato 55 165 00\n" +
                "2.5kg Onion 40 80 00\n" +
                "1 bunch Keerai big 85 100 00\n" +
                "1kg 8-chilly 20 12 00\n" +
                "Total 357 00\n";

        OCRTextParserService service = new OCRTextParserService();
        List<ParsedBillItem> items = service.parseRawText(rawText);

        assertEquals(4, items.size());
        assertEquals("Tomato", items.get(0).getProductName());
        assertEquals(3.0, items.get(0).getQuantity());
        assertEquals(165.0, items.get(0).getPrice());
        assertEquals("onion", items.get(1).getNormalizedName());
    }
}
