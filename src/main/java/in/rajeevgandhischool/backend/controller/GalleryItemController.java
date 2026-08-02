package in.rajeevgandhischool.backend.controller;

import in.rajeevgandhischool.backend.entity.GalleryItem;
import in.rajeevgandhischool.backend.repository.GalleryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryItemController {

    private final GalleryItemRepository galleryItemRepository;

    @GetMapping
    public ResponseEntity<List<GalleryItem>> getAllGalleryItems() {
        List<GalleryItem> items = galleryItemRepository.findAllByOrderByDisplayOrderAscIdAsc();

        // Auto-migrate any null/blank section fields in database
        boolean updatedAny = false;
        for (GalleryItem item : items) {
            if (item.getSection() == null || item.getSection().isBlank()) {
                String cat = item.getCategory() != null ? item.getCategory().toLowerCase() : "";
                String title = item.getTitle() != null ? item.getTitle().toLowerCase() : "";

                if (cat.contains("facil") || title.contains("lab") || title.contains("class") || title.contains("librar") || title.contains("sport") || title.contains("transport")) {
                    item.setSection("FACILITIES");
                } else if (cat.contains("highl") || title.contains("championship") || title.contains("parade") || title.contains("exhibition")) {
                    item.setSection("HIGHLIGHTS");
                } else {
                    item.setSection("GALLERY");
                }
                galleryItemRepository.save(item);
                updatedAny = true;
            }
        }

        if (updatedAny) {
            items = galleryItemRepository.findAllByOrderByDisplayOrderAscIdAsc();
        }

        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<GalleryItem> createGalleryItem(@RequestBody GalleryItem item) {
        if (item.getSection() == null || item.getSection().isBlank()) {
            item.setSection("GALLERY");
        }
        if (item.getDisplayOrder() == null || item.getDisplayOrder() == 0) {
            long count = galleryItemRepository.count();
            item.setDisplayOrder((int) count + 1);
        }
        GalleryItem saved = galleryItemRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGalleryItem(@PathVariable Long id, @RequestBody GalleryItem updatedData) {
        return galleryItemRepository.findById(id)
                .map(existing -> {
                    if (updatedData.getTitle() != null) existing.setTitle(updatedData.getTitle());
                    if (updatedData.getCategory() != null) existing.setCategory(updatedData.getCategory());
                    if (updatedData.getSection() != null) existing.setSection(updatedData.getSection());
                    if (updatedData.getImageUrl() != null) existing.setImageUrl(updatedData.getImageUrl());
                    if (updatedData.getPublicId() != null) existing.setPublicId(updatedData.getPublicId());
                    if (updatedData.getWide() != null) existing.setWide(updatedData.getWide());
                    if (updatedData.getDisplayOrder() != null) existing.setDisplayOrder(updatedData.getDisplayOrder());
                    GalleryItem saved = galleryItemRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorderGalleryItems(@RequestBody List<GalleryItem> items) {
        for (int i = 0; i < items.size(); i++) {
            GalleryItem item = items.get(i);
            if (item.getId() != null) {
                final int orderIndex = item.getDisplayOrder() != null ? item.getDisplayOrder() : (i + 1);
                galleryItemRepository.findById(item.getId()).ifPresent(existing -> {
                    existing.setDisplayOrder(orderIndex);
                    if (item.getSection() != null) {
                        existing.setSection(item.getSection());
                    }
                    galleryItemRepository.save(existing);
                });
            }
        }
        List<GalleryItem> reordered = galleryItemRepository.findAllByOrderByDisplayOrderAscIdAsc();
        return ResponseEntity.ok(reordered);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGalleryItem(@PathVariable Long id) {
        if (!galleryItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        galleryItemRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Gallery item deleted successfully"));
    }
}
