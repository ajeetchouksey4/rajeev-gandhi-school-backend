package in.rajeevgandhischool.backend.controller;

import in.rajeevgandhischool.backend.entity.Enquiry;
import in.rajeevgandhischool.backend.repository.EnquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/enquiries")
@RequiredArgsConstructor
public class EnquiryController {

    private final EnquiryRepository enquiryRepository;

    @PostMapping
    public ResponseEntity<Map<String, String>> submitEnquiry(@RequestBody Enquiry enquiry) {
        enquiryRepository.save(enquiry);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Enquiry submitted successfully!"));
    }

    @GetMapping
    public ResponseEntity<?> getAllEnquiries() {
        return ResponseEntity.ok(enquiryRepository.findAll());
    }
}
