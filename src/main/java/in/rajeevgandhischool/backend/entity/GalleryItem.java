package in.rajeevgandhischool.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String category;

    @Builder.Default
    private String section = "GALLERY"; // "FACILITIES", "HIGHLIGHTS", "GALLERY"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    private String publicId;

    @Builder.Default
    private Boolean wide = false;

    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.section == null) {
            this.section = "GALLERY";
        }
    }
}
