import { IListingImage } from "@core/lib/listing/types";
import { useCallback, useEffect, useState } from "react";
import styles from "./ListingGallery.module.scss"

const ListingGallery = ({ images }: {images: IListingImage[]}) => {

    const [mainImageIndex, setMainImageIndex] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const openModal = (index: number) => {
        setMainImageIndex(index);
        setIsModalOpen(true);
    };

    const handlePrevImage = () => {
        setMainImageIndex((prev) => (prev - 1 + images.length) % images.length);
    };

    const handleNextImage = () => {
        setMainImageIndex((prev) => (prev + 1) % images.length);
    };

    const closeModal = () => setIsModalOpen(false);

    const handleKeyDown = useCallback((e: KeyboardEvent) => {
        if (e.key === 'Escape') {
            if (isModalOpen) setIsModalOpen(false);
        }
    }, [isModalOpen])

    useEffect(() => {
        document.addEventListener('keydown', handleKeyDown);
        return () => {
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, [handleKeyDown, isModalOpen]);

    return (
        <>
            <div className={`${styles.gallery} fade-down`}>
                <div className={styles.mainImage}>
                    <div className={styles.imageContainer}>
                        <button className={`${styles.navArrow} ${styles.prev}`} onClick={handlePrevImage}>
                            <i className="fa-solid fa-circle-left fa-xl"></i>
                        </button>
                        <img
                            src={images[mainImageIndex]?.path ?? `/images/placeholders/default-listing.svg`}
                            onError={(e) => e.currentTarget.src = `/images/placeholders/default-listing.svg`}
                            alt="Основное изображение"
                            className={`${styles.clickableImage} ${styles.mainImageView}`}
                            id="mainImageView"
                            onClick={() => openModal(mainImageIndex)}
                        />
                        <button className={`${styles.navArrow} ${styles.next}`} onClick={handleNextImage}>
                            <i className="fa-solid fa-circle-right fa-xl"></i>
                        </button>
                    </div>
                </div>

                {images?.length > 1 && (
                    <div className={styles.thumbnails}>
                        {images.map((image, index) => (
                            <div className={styles.thumbnail} key={index} data-index={index}>
                                <img
                                    key={image.id}
                                    src={image.path}
                                    onError={(e) => e.currentTarget.src = `/images/placeholders/default-listing.svg`}
                                    alt="Дополнительное изображение"
                                    className={styles.clickableImage}
                                    onClick={() => setMainImageIndex(index)}
                                />
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Модальное окно (fullscreen) */}
            {isModalOpen && (
                <div className={styles.fullscreenModal}>
                    <span className={styles.close} id="close-modal" onClick={closeModal}>
                        &times;
                    </span>
                    <button className={`${styles.modalArrow} ${styles.prev}`} onClick={handlePrevImage}>
                        &#10094;
                    </button>
                    <img
                        src={images[mainImageIndex].path}
                        alt="Fullscreen"
                    />
                    <button className={`${styles.modalArrow} ${styles.next}`} onClick={handleNextImage}>
                        &#10095;
                    </button>
                </div>
            )}
        </>
    );
};

export default ListingGallery;