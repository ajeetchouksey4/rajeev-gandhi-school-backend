package in.rajeevgandhischool.backend.controller;

import in.rajeevgandhischool.backend.entity.GalleryCategory;
import in.rajeevgandhischool.backend.entity.GalleryImage;
import in.rajeevgandhischool.backend.repository.GalleryImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GalleryController {

    private final GalleryImageRepository galleryImageRepository;

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
}
