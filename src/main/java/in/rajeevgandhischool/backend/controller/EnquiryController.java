package in.rajeevgandhischool.backend.controller;

import in.rajeevgandhischool.backend.entity.Enquiry;
import in.rajeevgandhischool.backend.entity.EnquiryCategory;
import in.rajeevgandhischool.backend.entity.EnquiryStatus;
import in.rajeevgandhischool.backend.repository.EnquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enquiries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnquiryController {

    private final EnquiryRepository enquiryRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitEnquiry(@RequestBody Enquiry enquiry) {
        if (enquiry.getCategory() == null) {
            enquiry.setCategory(EnquiryCategory.ADMISSION);
        }
        if (enquiry.getStatus() == null) {
            enquiry.setStatus(EnquiryStatus.NEW);
        }
        if (enquiry.getIsRead() == null) {
            enquiry.setIsRead(false);
        }
        Enquiry saved = enquiryRepository.save(enquiry);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "Enquiry submitted successfully!",
                    "enquiry", saved
                ));
    }

    @GetMapping
    public ResponseEntity<List<Enquiry>> getAllEnquiries(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        
        EnquiryCategory categoryEnum = null;
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
            try {
                categoryEnum = EnquiryCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        EnquiryStatus statusEnum = null;
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            try {
                statusEnum = EnquiryStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        List<Enquiry> results = enquiryRepository.searchEnquiries(categoryEnum, statusEnum, search);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        long count = enquiryRepository.countByIsReadFalse();
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEnquiryById(@PathVariable Long id) {
        return enquiryRepository.findById(id)
                .map(enquiry -> {
                    if (Boolean.FALSE.equals(enquiry.getIsRead())) {
                        enquiry.setIsRead(true);
                        enquiryRepository.save(enquiry);
                    }
                    return ResponseEntity.ok(enquiry);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateEnquiryStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status field is required"));
        }

        EnquiryStatus newStatus;
        try {
            newStatus = EnquiryStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status value: " + statusStr));
        }

        return enquiryRepository.findById(id)
                .map(enquiry -> {
                    enquiry.setStatus(newStatus);
                    Enquiry saved = enquiryRepository.save(enquiry);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics(
            @RequestParam(defaultValue = "week") String range,
            @RequestParam(required = false) String category) {
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        
        if ("year".equalsIgnoreCase(range)) {
            startDate = now.minusMonths(11).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        } else if ("month".equalsIgnoreCase(range)) {
            startDate = now.minusDays(27).withHour(0).withMinute(0).withSecond(0);
        } else {
            startDate = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);
        }

        List<Enquiry> rawEnquiries = enquiryRepository.findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(startDate);

        EnquiryCategory filterCat = null;
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
            try {
                filterCat = EnquiryCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        final EnquiryCategory targetCat = filterCat;
        List<Enquiry> periodEnquiries = rawEnquiries.stream()
                .filter(e -> targetCat == null || e.getCategory() == targetCat)
                .collect(Collectors.toList());

        // Status breakdown
        Map<String, Long> statusBreakdown = new HashMap<>();
        statusBreakdown.put("NEW", 0L);
        statusBreakdown.put("CONTACTED", 0L);
        statusBreakdown.put("ENROLLED", 0L);
        statusBreakdown.put("CLOSED", 0L);

        for (Enquiry e : periodEnquiries) {
            if (e.getStatus() != null) {
                String sName = e.getStatus().name();
                statusBreakdown.put(sName, statusBreakdown.getOrDefault(sName, 0L) + 1);
            }
        }

        // Category breakdown
        Map<String, Long> categoryBreakdown = new HashMap<>();
        categoryBreakdown.put("ADMISSION", 0L);
        categoryBreakdown.put("GENERAL", 0L);
        categoryBreakdown.put("CAREER", 0L);
        categoryBreakdown.put("BUSINESS", 0L);

        for (Enquiry e : rawEnquiries) {
            if (e.getCategory() != null) {
                String cName = e.getCategory().name();
                categoryBreakdown.put(cName, categoryBreakdown.getOrDefault(cName, 0L) + 1);
            }
        }

        // Timeline aggregation
        List<Map<String, Object>> timeline = new ArrayList<>();

        if ("year".equalsIgnoreCase(range)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
            for (int i = 11; i >= 0; i--) {
                LocalDateTime mStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime mEnd = mStart.plusMonths(1);
                String label = mStart.format(formatter);

                long count = periodEnquiries.stream()
                        .filter(e -> e.getCreatedAt() != null && !e.getCreatedAt().isBefore(mStart) && e.getCreatedAt().isBefore(mEnd))
                        .count();

                timeline.add(Map.of("label", label, "count", count));
            }
        } else if ("month".equalsIgnoreCase(range)) {
            for (int i = 3; i >= 0; i--) {
                LocalDateTime wStart = now.minusDays((i + 1) * 7 - 1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime wEnd = wStart.plusDays(7);
                String label = "W" + (4 - i) + " (" + wStart.format(DateTimeFormatter.ofPattern("dd MMM")) + ")";

                long count = periodEnquiries.stream()
                        .filter(e -> e.getCreatedAt() != null && !e.getCreatedAt().isBefore(wStart) && e.getCreatedAt().isBefore(wEnd))
                        .count();

                timeline.add(Map.of("label", label, "count", count));
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM");
            for (int i = 6; i >= 0; i--) {
                LocalDate day = LocalDate.now().minusDays(i);
                String label = day.format(formatter);

                long count = periodEnquiries.stream()
                        .filter(e -> e.getCreatedAt() != null && e.getCreatedAt().toLocalDate().equals(day))
                        .count();

                timeline.add(Map.of("label", label, "count", count));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("range", range);
        result.put("total", periodEnquiries.size());
        result.put("statusBreakdown", statusBreakdown);
        result.put("categoryBreakdown", categoryBreakdown);
        result.put("timeline", timeline);

        return ResponseEntity.ok(result);
    }
}
