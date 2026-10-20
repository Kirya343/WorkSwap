package org.workswap.listing.datasource.model;

import org.workswap.storage.util.ImageFormatRegistry;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Image {

    public Image(
            String objectKey,
            String contentType,
            Long size,
            int width,
            int height,
            String hash,
            Listing listing
        ) {
        this.objectKey = objectKey;
        this.listing = listing;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objectKey;

    private String contentType;
    private Long size;
    private int width;
    private int height;
    private String hash;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(name = "listing_id", insertable = false, updatable = false)
    private Long listingId;

    public String getLink() {
        return "https://cloud.workswap.org/listing-images/%s.%s".formatted(
            objectKey, 
            ImageFormatRegistry.extensionFromMime(contentType));
    }
}