package in.rajeevgandhischool.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enquiries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentName;

    private String parentName;

    @Column(name = "parent_phone")
    private String parentPhone;

    @Column(name = "class_applying_for")
    private String classApplyingFor;

    private String parentEmail;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private EnquiryStatus status = EnquiryStatus.NEW;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper getters/setters to support both property naming styles (phone/parentPhone, classAppliedFor/classApplyingFor)
    public String getPhone() {
        return parentPhone != null ? parentPhone : "";
    }

    public void setPhone(String phone) {
        this.parentPhone = phone;
    }

    public String getClassAppliedFor() {
        return classApplyingFor != null ? classApplyingFor : "";
    }

    public void setClassAppliedFor(String classAppliedFor) {
        this.classApplyingFor = classAppliedFor;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = EnquiryStatus.NEW;
        }
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.parentName == null || this.parentName.trim().isEmpty()) {
            this.parentName = "Parent / Guardian";
        }
    }
}
