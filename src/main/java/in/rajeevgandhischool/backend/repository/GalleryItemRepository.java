package in.rajeevgandhischool.backend.repository;

import in.rajeevgandhischool.backend.entity.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {
    List<GalleryItem> findAllByOrderByDisplayOrderAscIdAsc();
    long countBySection(String section);
}
