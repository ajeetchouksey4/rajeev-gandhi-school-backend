package in.rajeevgandhischool.backend.controller;

import in.rajeevgandhischool.backend.entity.Announcement;
import in.rajeevgandhischool.backend.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;

    @GetMapping
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        List<Announcement> list = announcementRepository.findAllByOrderByIsPinnedDescIdDesc();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        Announcement saved = announcementRepository.save(announcement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement updatedData) {
        return announcementRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedData.getTitle());
                    existing.setCategory(updatedData.getCategory());
                    existing.setBadge(updatedData.getBadge());
                    existing.setDate(updatedData.getDate());
                    existing.setDescription(updatedData.getDescription());
                    existing.setIsPinned(updatedData.getIsPinned());
                    Announcement saved = announcementRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAnnouncement(@PathVariable Long id) {
        if (!announcementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        announcementRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Announcement deleted successfully"));
    }
}
