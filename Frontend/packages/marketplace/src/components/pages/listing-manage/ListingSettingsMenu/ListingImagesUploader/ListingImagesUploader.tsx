import { useNotification } from "@core/lib/notification/NotificationContext";
import { listingService } from "@core/lib/listing/services";
import { IFullListing, IListingImage, IListingUpdate } from "@core/lib/listing/types";
import { useEffect, useState } from "react";
import styles from "./ListingImagesUploader.module.scss"
import PlusIcon from "@core/components/common/icons/PlusIcon";
import ActionMenu from "@core/components/ui/ActionMenu/ActionMenu";
import { IKebabAction } from '@core/components/ui/ActionMenu/ActionMenu';
import { UpdateListing } from "../ListingSettingsMenu";

const ListingImagesUploader = ({
    updateListingSettings,
    listingSettings,
    listing
}: {
    updateListingSettings: UpdateListing
    listingSettings: IListingUpdate
    listing: IFullListing
}) => {

    const {notificate} = useNotification();

    const [images, setImages] = useState<IListingImage[] | null>(null);

    useEffect(() => {
        setImages(images);
    }, [images, listing])

    useEffect(() => {
        if (!listing.id) return;
        async function loadListingImages() {
            const data = await listingService.getImages(listing.id);
            setImages(data);
        }

        loadListingImages()
    }, [listing.id]);

    // Добавляем новое изображение
    const addListingImageUrl = (newImage: IListingImage) => {
        setImages(prev => {
            if (!prev) return prev;
            return [...prev, newImage]
        });
    };
    
    // Удаляем изображение
    const deleteImg = async (img: IListingImage) => {
        try {
            const res = await listingService.deleteListingImage(listing.id, img.id)
            if (res) notificate(res)
        } catch {
            notificate("Ошибка удаления изображения", "error")
            throw new Error("Ошибка удаления изображения")
        }
                    
        setImages(prev => prev?.filter(item => item.path !== img.path) ?? null);
        if (listingSettings.mainImageId === img.id) updateListingSettings("mainImageId", null); // если основное изображение удалено
    };

    // Загрузка нового изображения
    const uploadImage = async (file: File) => {

        const formData = new FormData();
        formData.append("image", file);

        const data = await listingService.uploadListingImage(listing.id, formData)

        if (data.path) {
            notificate("Изображение загружено", "success")
            const newImage: IListingImage = { id: data.id, listingId: listing.id, path: data.path }
            addListingImageUrl(newImage);
            if (!listingSettings.mainImageId) updateListingSettings("mainImageId", newImage.id);
        } else {
            notificate("Ошибка загрузки изображения", "error")
        }
    };

    const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const files = e.target.files;
        if (!files) return;
        for (let file of files) {
            await uploadImage(file);
        }
    };

    return (
        <div className={styles.imageGallery}>
            {images?.map((img) => (
                <Image
                    key={img.id} image={img}
                    setListingMainImage={(imageId) => updateListingSettings("mainImageId", imageId)}
                    deleteImg={deleteImg}
                    isMain={img.id == listingSettings.mainImageId}
                />
            ))}
            <div className={styles.updloadImage}>
                <input
                    type="file"
                    id="uploadImage"
                    accept="image/*"
                    multiple
                    onChange={handleImageUpload}
                />
                <label htmlFor="uploadImage">
                    <PlusIcon />
                </label>
            </div>
        </div>
    );
};

function Image({
    image, setListingMainImage, deleteImg, isMain
}: {
    image: IListingImage;
    setListingMainImage: (mainImageId: number) => void;
    deleteImg: (img: IListingImage) => void;
    isMain: boolean;
}) {

    const actions: IKebabAction[] = [
        {
            title: "Сделать избранным",
            func: () => setListingMainImage(image.id),
            access: !isMain
        },
        {
            title: "Удалить",
            func: () => deleteImg(image)
        }
    ]

    return (
        <div className={styles.image}>
            <img
                src={image.path ?? "/images/placeholders/default-listing.svg"}
                onError={(e) => {
                    const img = e.target as HTMLImageElement;
                    img.src = '/images/placeholders/default-listing.svg';
                }}
            />
            <ActionMenu className={styles.kebab} actions={actions}/>
        </div>
    )
}

export default ListingImagesUploader;