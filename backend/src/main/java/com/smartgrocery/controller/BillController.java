package com.smartgrocery.controller;

import com.smartgrocery.service.BillProcessingService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class BillController {

    private final BillProcessingService billProcessingService;
    private final JdbcTemplate jdbcTemplate;

    public BillController(BillProcessingService billProcessingService, JdbcTemplate jdbcTemplate) {
        this.billProcessingService = billProcessingService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/bills/upload")
    public ResponseEntity<Map<String, Object>> uploadBill(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) Long userId
    ) {
        Map<String, Object> result = billProcessingService.processUploadedBill(file, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> getInventory(@RequestParam(value = "status", required = false) String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT i.id, p.name, i.category, i.quantity, i.unit, i.expiry_date, i.status, i.cost " +
                "FROM inventory_items i " +
                "LEFT JOIN products p ON p.id = i.product_id " +
                "WHERE i.user_id = ?"
        );

        List<Object> params = new ArrayList<>();
        params.add(1L);

        if (status != null && !status.isBlank()) {
            sql.append(" AND i.status = ?::inventory_status");
            params.add(status.trim());
        }

        sql.append(" ORDER BY i.created_at DESC");

        List<Map<String, Object>> items = jdbcTemplate.query(
                sql.toString(),
                ps -> {
                    for (int i = 0; i < params.size(); i++) {
                        ps.setObject(i + 1, params.get(i));
                    }
                },
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getLong("id"));
                    item.put("name", rs.getString("name"));
                    item.put("category", rs.getString("category"));
                    item.put("qty", rs.getBigDecimal("quantity"));
                    item.put("unit", rs.getString("unit"));
                    item.put("expiry", rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate().toString() : null);
                    item.put("status", rs.getString("status"));
                    item.put("cost", rs.getBigDecimal("cost") != null ? rs.getBigDecimal("cost") : BigDecimal.ZERO);
                    return item;
                }
        );

        return ResponseEntity.ok(Map.of("items", items));
    }

    @PostMapping("/inventory")
    public ResponseEntity<Map<String, Object>> saveInventoryItem(@RequestBody Map<String, Object> payload) {
        Long userId = payload.get("userId") != null ? Long.valueOf(payload.get("userId").toString()) : 1L;
        String name = String.valueOf(payload.getOrDefault("name", "Unknown item")).trim();
        String category = String.valueOf(payload.getOrDefault("category", "Other")).trim();
        BigDecimal qty = new BigDecimal(String.valueOf(payload.getOrDefault("qty", 1)));
        String unit = String.valueOf(payload.getOrDefault("unit", "pcs")).trim();
        BigDecimal cost = new BigDecimal(String.valueOf(payload.getOrDefault("cost", 0)));
        String expiry = String.valueOf(payload.getOrDefault("expiry", LocalDate.now().plusDays(7).toString()));
        String status = String.valueOf(payload.getOrDefault("status", "fresh")).trim();

        jdbcTemplate.update("INSERT INTO categories (name) VALUES (?) ON CONFLICT (name) DO NOTHING", category);

        Long productId = jdbcTemplate.query(
                "SELECT id FROM products WHERE lower(name) = lower(?) AND lower(category) = lower(?) LIMIT 1",
                ps -> {
                    ps.setString(1, name);
                    ps.setString(2, category);
                },
                rs -> rs.next() ? rs.getLong("id") : null
        );

        if (productId == null) {
            productId = jdbcTemplate.queryForObject(
                    "INSERT INTO products (name, category, default_shelf_life_days) VALUES (?, ?, ?) RETURNING id",
                    Long.class,
                    name,
                    category,
                    7
            );
        }

        Long itemId = jdbcTemplate.queryForObject(
                "INSERT INTO inventory_items (user_id, product_id, quantity, unit, purchase_date, expiry_date, category, status, cost) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?::inventory_status, ?) RETURNING id",
                Long.class,
                userId,
                productId,
                qty,
                unit,
                LocalDate.now(),
                LocalDate.parse(expiry),
                category,
                status,
                cost
        );

        Map<String, Object> item = new HashMap<>();
        item.put("id", itemId);
        item.put("name", name);
        item.put("category", category);
        item.put("qty", qty);
        item.put("unit", unit);
        item.put("expiry", LocalDate.parse(expiry).toString());
        item.put("status", status);
        item.put("cost", cost);

        return ResponseEntity.ok(Map.of("success", true, "message", "Item saved successfully", "item", item));
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        return ResponseEntity.ok(Map.of(
                "totalItems", 128,
                "expiringSoon", 14,
                "expired", 6,
                "fresh", 108
        ));
    }
}
