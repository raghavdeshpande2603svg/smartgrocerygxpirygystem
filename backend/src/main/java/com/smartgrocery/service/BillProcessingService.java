package com.smartgrocery.service;

import com.smartgrocery.ocr.ParsedBillItem;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BillProcessingService {

    private static final Set<String> SUPPORTED_IMAGE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final OCRTextParserService ocrTextParserService;
    private final Tesseract tesseract;

    public BillProcessingService(OCRTextParserService ocrTextParserService) {
        this.ocrTextParserService = ocrTextParserService;
        this.tesseract = new Tesseract();
        this.tesseract.setLanguage("eng");
        String tessDataPath = System.getProperty("app.ocr.tesseract-data-path");
        if (tessDataPath != null && !tessDataPath.isBlank()) {
            this.tesseract.setDatapath(tessDataPath);
        }
    }

    public Map<String, Object> processUploadedBill(MultipartFile file, Long userId) {
        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("success", false);
            response.put("message", "No file uploaded");
            return response;
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String storedName = UUID.randomUUID() + "_" + originalFilename;
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);
            Path savedFile = uploadDir.resolve(storedName);
            file.transferTo(savedFile);

            String rawText = extractTextFromUploadedFile(savedFile, file);
            if (rawText == null || rawText.isBlank()) {
                response.put("success", false);
                response.put("message", "No readable text found in the uploaded image or bill. Please upload a clearer image or a text file.");
                return response;
            }

            List<ParsedBillItem> items = ocrTextParserService.parseRawText(rawText);
            if (items.isEmpty()) {
                response.put("success", false);
                response.put("message", "No grocery items could be detected from the uploaded file. Please try another image or enter manually.");
                return response;
            }

            for (ParsedBillItem item : items) {
                item.setCategory(classifyCategory(item.getNormalizedName()));
                item.setDefaultShelfLifeDays(resolveShelfLifeDays(item.getCategory()));
                item.setConfidence(0.9);
                item.setMatchSource("keyword");
            }

            response.put("success", true);
            response.put("fileName", originalFilename);
            response.put("storedPath", savedFile.toString());
            response.put("uploadedAt", LocalDate.now().toString());
            response.put("items", items);
            response.put("message", "Bill processed successfully");
            return response;
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to process bill: " + e.getMessage());
            return response;
        }
    }

    private String extractTextFromUploadedFile(Path savedFile, MultipartFile file) throws IOException {
        String fileName = savedFile.getFileName().toString().toLowerCase(Locale.ROOT);
        String contentType = file.getContentType();

        if (fileName.endsWith(".txt") || fileName.endsWith(".csv") || "text/plain".equalsIgnoreCase(contentType)) {
            return Files.readString(savedFile, StandardCharsets.UTF_8);
        }

        if (SUPPORTED_IMAGE_TYPES.contains(contentType) || fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".webp")) {
            try {
                return tesseract.doOCR(savedFile.toFile()).trim();
            } catch (TesseractException e) {
                throw new IllegalArgumentException("Image OCR failed. Please upload a clearer image or use manual entry. Details: " + e.getMessage());
            }
        }

        if (fileName.endsWith(".pdf") || "application/pdf".equalsIgnoreCase(contentType)) {
            throw new IllegalArgumentException("PDF uploads are not supported in this environment. Please upload a JPG or PNG image instead.");
        }

        throw new IllegalArgumentException("Unsupported file type. Please upload JPG, PNG, or TXT files.");
    }

    private String classifyCategory(String normalizedName) {
        if (normalizedName == null) {
            return "Other";
        }

        if (normalizedName.contains("milk") || normalizedName.contains("curd") || normalizedName.contains("paneer")) {
            return "Dairy";
        }
        if (normalizedName.contains("tomato") || normalizedName.contains("onion") || normalizedName.contains("spinach")) {
            return "Vegetables";
        }
        if (normalizedName.contains("rice") || normalizedName.contains("wheat") || normalizedName.contains("atta")) {
            return "Grains";
        }
        if (normalizedName.contains("apple") || normalizedName.contains("banana") || normalizedName.contains("orange")) {
            return "Fruits";
        }
        if (normalizedName.contains("juice") || normalizedName.contains("cola") || normalizedName.contains("water")) {
            return "Beverages";
        }
        if (normalizedName.contains("chips") || normalizedName.contains("biscuits") || normalizedName.contains("cookie")) {
            return "Snacks";
        }

        return "Other";
    }

    private Integer resolveShelfLifeDays(String category) {
        return switch (category) {
            case "Dairy" -> 5;
            case "Vegetables" -> 7;
            case "Grains" -> 30;
            case "Fruits" -> 6;
            case "Beverages" -> 12;
            case "Snacks" -> 45;
            case "Frozen" -> 60;
            default -> 14;
        };
    }
}
