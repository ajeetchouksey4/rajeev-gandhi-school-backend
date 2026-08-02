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
                GalleryItem.builder().title("Smart Classrooms").category("Facilities").section("FACILITIES").imageUrl("https://images.unsplash.com/photo-1562774053-701939374585?w=500&q=80").displayOrder(1).build(),
                GalleryItem.builder().title("Science Labs").category("Facilities").section("FACILITIES").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130550/lab_sdvj0y.jpg").displayOrder(2).build(),
                GalleryItem.builder().title("Library").category("Facilities").section("FACILITIES").imageUrl("https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500&q=80").displayOrder(3).build(),
                GalleryItem.builder().title("Sports Complex").category("Facilities").section("FACILITIES").imageUrl("https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=500&q=80").displayOrder(4).build(),
                GalleryItem.builder().title("Computer Lab").category("Facilities").section("FACILITIES").imageUrl("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500&q=80").displayOrder(5).build(),
                GalleryItem.builder().title("Transport").category("Facilities").section("FACILITIES").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778570845/transport1_gql8sk.jpg").displayOrder(6).build(),

                // School Highlights Section Photos
                GalleryItem.builder().title("Annual Day Celebration").category("Events").section("HIGHLIGHTS").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778142942/trophy1_bz0ht0.jpg").displayOrder(1).build(),
                GalleryItem.builder().title("Sports Day Championship").category("Sports").section("HIGHLIGHTS").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778142942/trophy3_vrtlrd.jpg").displayOrder(2).build(),
                GalleryItem.builder().title("Science Exhibition").category("Academics").section("HIGHLIGHTS").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130550/lab_sdvj0y.jpg").displayOrder(3).build(),
                GalleryItem.builder().title("Republic Day Parade").category("Celebrations").section("HIGHLIGHTS").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130962/independence5_ku7v2n.jpg").displayOrder(4).build(),

                // Photo Gallery Section Photos
                GalleryItem.builder().title("Annual Day Celebration").category("Events").section("GALLERY").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778142942/trophy1_bz0ht0.jpg").wide(true).displayOrder(1).build(),
                GalleryItem.builder().title("Yoga Day").category("Activities").section("GALLERY").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130623/assembly2_lu2rg4.jpg").wide(false).displayOrder(2).build(),
                GalleryItem.builder().title("Sports Day").category("Sports").section("GALLERY").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778142942/trophy3_vrtlrd.jpg").wide(false).displayOrder(3).build(),
                GalleryItem.builder().title("Independence Day").category("Celebrations").section("GALLERY").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130962/independence6_kgjyx0.jpg").wide(false).displayOrder(4).build(),
                GalleryItem.builder().title("Science Exhibition").category("Academics").section("GALLERY").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130550/lab_sdvj0y.jpg").wide(true).displayOrder(5).build(),
                GalleryItem.builder().title("Republic Day").category("Celebrations").section("GALLERY").imageUrl("https://res.cloudinary.com/dzckejmbq/image/upload/v1778130962/independence5_ku7v2n.jpg").wide(false).displayOrder(6).build()
            );
            galleryItemRepository.saveAll(defaultItems);
        }
    }
}
