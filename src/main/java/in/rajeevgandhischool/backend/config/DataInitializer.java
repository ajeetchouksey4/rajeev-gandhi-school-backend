package in.rajeevgandhischool.backend.config;

import in.rajeevgandhischool.backend.entity.GalleryItem;
import in.rajeevgandhischool.backend.repository.GalleryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GalleryItemRepository galleryItemRepository;

    @Override
    public void run(String... args) throws Exception {
        if (galleryItemRepository.count() == 0) {
            List<GalleryItem> defaultItems = List.of(
                // Facilities Section Photos
                GalleryItem.builder()
                    .title("Science Labs")
                    .category("Facilities")
                    .section("FACILITIES")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130550/lab_sdvj0y.jpg")
                    .displayOrder(1)
                    .build(),
                GalleryItem.builder()
                    .title("Transport")
                    .category("Facilities")
                    .section("FACILITIES")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778570845/transport1_gql8sk.jpg")
                    .displayOrder(2)
                    .build(),

                // School Highlights Section Photos
                GalleryItem.builder()
                    .title("Sports Day Championship")
                    .category("Highlights")
                    .section("HIGHLIGHTS")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778142942/trophy1_bz0ht0.jpg")
                    .displayOrder(1)
                    .build(),
                GalleryItem.builder()
                    .title("Republic Day Parade")
                    .category("Highlights")
                    .section("HIGHLIGHTS")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130962/independence5_ku7v2n.jpg")
                    .displayOrder(2)
                    .build(),

                // Photo Gallery Section Photos
                GalleryItem.builder()
                    .title("Annual Day Celebration")
                    .category("Events")
                    .section("GALLERY")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778142942/trophy1_bz0ht0.jpg")
                    .wide(true)
                    .displayOrder(1)
                    .build(),
                GalleryItem.builder()
                    .title("Yoga Day")
                    .category("Activities")
                    .section("GALLERY")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130623/assembly2_lu2rg4.jpg")
                    .wide(false)
                    .displayOrder(2)
                    .build(),
                GalleryItem.builder()
                    .title("Sports Day")
                    .category("Sports")
                    .section("GALLERY")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778142942/trophy3_vrtlrd.jpg")
                    .wide(false)
                    .displayOrder(3)
                    .build(),
                GalleryItem.builder()
                    .title("Independence Day")
                    .category("Celebrations")
                    .section("GALLERY")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130962/independence6_kgjyx0.jpg")
                    .wide(false)
                    .displayOrder(4)
                    .build(),
                GalleryItem.builder()
                    .title("Science Exhibition")
                    .category("Academics")
                    .section("GALLERY")
                    .imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130550/lab_sdvj0y.jpg")
                    .wide(true)
                    .displayOrder(5)
                    .build()
            );
            galleryItemRepository.saveAll(defaultItems);
        }
    }
}
