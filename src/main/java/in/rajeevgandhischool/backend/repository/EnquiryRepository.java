package in.rajeevgandhischool.backend.repository;

import in.rajeevgandhischool.backend.entity.Enquiry;
import in.rajeevgandhischool.backend.entity.EnquiryCategory;
import in.rajeevgandhischool.backend.entity.EnquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

    long countByIsReadFalse();

    @Query("SELECT e FROM Enquiry e WHERE " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(COALESCE(e.studentName, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(COALESCE(e.parentName, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(COALESCE(e.parentPhone, '')) LIKE LOWER(CONCAT('%', :search, '%')))" +
           " ORDER BY e.createdAt DESC")
    List<Enquiry> searchEnquiries(@Param("category") EnquiryCategory category,
                                  @Param("status") EnquiryStatus status,
                                  @Param("search") String search);

    List<Enquiry> findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(LocalDateTime startDate);
}
