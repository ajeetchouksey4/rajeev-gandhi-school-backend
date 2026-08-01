package in.rajeevgandhischool.backend.repository;

import in.rajeevgandhischool.backend.entity.GalleryCategory;
import in.rajeevgandhischool.backend.entity.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {

    List<GalleryImage> findAllByOrderByDisplayOrderAscCreatedAtDesc();

    List<GalleryImage> findByCategoryOrderByDisplayOrderAscCreatedAtDesc(GalleryCategory category);
}
