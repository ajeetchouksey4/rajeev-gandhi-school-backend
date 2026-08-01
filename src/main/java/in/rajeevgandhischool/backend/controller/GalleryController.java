package in.rajeevgandhischool.backend.controller;

import in.rajeevgandhischool.backend.entity.GalleryCategory;
import in.rajeevgandhischool.backend.entity.GalleryImage;
import in.rajeevgandhischool.backend.repository.GalleryImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GalleryController {

    private final GalleryImageRepository galleryImageRepository;

    @Value("${cloudinary.cloud-name:dzckejmbq}")
    private String cloudName;

    @Value("${cloudinary.api-key:911945938763684}")
    private String apiKey;

    @Value("${cloudinary.api-secret:wSOYxFmdQWIo2SCkYvo4iXumZ5c}")
    private String apiSecret;

    @GetMapping
    public ResponseEntity<List<GalleryImage>> getGalleryImages(
            @RequestParam(required = false) String category) {
        
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
            try {
                GalleryCategory categoryEnum = GalleryCategory.valueOf(category.toUpperCase());
                List<GalleryImage> images = galleryImageRepository.findByCategoryOrderByDisplayOrderAscCreatedAtDesc(categoryEnum);
                return ResponseEntity.ok(images);
            } catch (IllegalArgumentException ignored) {
                // Invalid category passed, return all images
            }
        }
        
        List<GalleryImage> images = galleryImageRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc();
        return ResponseEntity.ok(images);
    }

    @PostMapping
    public ResponseEntity<?> createGalleryImage(@RequestBody GalleryImage galleryImage) {
        if (galleryImage.getImageUrl() == null || galleryImage.getImageUrl().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image URL is required"));
        }
        if (galleryImage.getCategory() == null) {
            galleryImage.setCategory(GalleryCategory.GENERAL);
        }
        if (galleryImage.getDisplayOrder() == null) {
            galleryImage.setDisplayOrder(0);
        }

        GalleryImage saved = galleryImageRepository.save(galleryImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGalleryImage(@PathVariable Long id) {
        if (!galleryImageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        galleryImageRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Gallery image deleted successfully"));
    }

    @PatchMapping("/{id}/order")
    public ResponseEntity<?> updateDisplayOrder(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Object orderObj = body.get("displayOrder");
        if (orderObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "displayOrder field is required"));
        }

        int newOrder;
        try {
            newOrder = Integer.parseInt(orderObj.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid displayOrder number: " + orderObj));
        }

        return galleryImageRepository.findById(id)
                .map(image -> {
                    image.setDisplayOrder(newOrder);
                    GalleryImage saved = galleryImageRepository.save(image);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGalleryImage(@PathVariable Long id, @RequestBody GalleryImage updatedData) {
        return galleryImageRepository.findById(id)
                .map(existing -> {
                    if (updatedData.getImageUrl() != null && !updatedData.getImageUrl().isBlank()) {
                        existing.setImageUrl(updatedData.getImageUrl());
                    }
                    if (updatedData.getCategory() != null) {
                        existing.setCategory(updatedData.getCategory());
                    }
                    existing.setTitle(updatedData.getTitle());
                    existing.setDescription(updatedData.getDescription());
                    existing.setEventDate(updatedData.getEventDate());
                    if (updatedData.getDisplayOrder() != null) {
                        existing.setDisplayOrder(updatedData.getDisplayOrder());
                    }
                    GalleryImage saved = galleryImageRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/cloudinary-signature")
    public ResponseEntity<Map<String, Object>> generateCloudinarySignature(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> paramsToSign = new HashMap<>();
        if (body != null && body.containsKey("paramsToSign") && body.get("paramsToSign") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) body.get("paramsToSign");
            paramsToSign = map;
        }

        String signature = generateSha1(paramsToSign, apiSecret);
        
        Map<String, Object> response = new HashMap<>();
        response.put("signature", signature);
        response.put("apiKey", apiKey);
        response.put("cloudName", cloudName);
        return ResponseEntity.ok(response);
    }

    private String generateSha1(Map<String, Object> params, String secret) {
        String paramString = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().toString().isBlank())
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String stringToSign = paramString + secret;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(stringToSign.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 error", e);
        }
    }
}
