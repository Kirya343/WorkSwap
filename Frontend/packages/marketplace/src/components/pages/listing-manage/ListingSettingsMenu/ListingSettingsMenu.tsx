import { useState, useCallback, useEffect } from "react";
import ListingEditActions from "./ListingEditActions";
import ListingImagesUploader from "./ListingImagesUploader/ListingImagesUploader";
import ListingTranslations from "./translations/ListingTranslations/ListingTranslations";
import EventSettings from "./settings/EventSettings";
import ProductSettings from "./settings/ProductSettings";
import ServiceSettings from "./settings/ServiceSettings";
import PriceEdit from "./PriceEdit";
import ListingSetting from "./ListingSetting/ListingSetting";
import { IFullListing, IListingUpdate } from "@core/lib/listing/types";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { listingService } from "@core/lib/listing/services";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import LocationSelector from "@/components/ui/selectors/LocationSelector";
import SliderCheckbox from "@core/components/common/checkbox/SliderCheckbox/SliderCheckbox";
import { log } from "node:console";

const disabledTypesForPrice = ["PRODUCT_SWAP", "PRODUCT_GIVEAWAY", "PRODUCT_WANTED_FREE"];

export type UpdateListing = <K extends keyof IListingUpdate>(key: K, value: IListingUpdate[K]) => void

const ListingSettingsMenu = ({listing}: {listing: IFullListing}) => {
    
    const { dict } = useI18n();

    const { notificate } = useNotification();

    const [isActive, setActive] = useState<boolean>(listing?.active || false);

    const [listingSettings, setListingSettins] = useState<IListingUpdate>({
        price: listing.price,
        priceType: listing.priceType,
        locationId: listing.locationId,
        categoryId: listing.categoryId,
        mainImageId: listing.mainImageId,
        accessToken: listing.accessToken,
        active: listing.active,
        testMode: listing.testmode
    })
    
    const updateListingSettings: UpdateListing = useCallback(async (key, value) => {
        if (!listing.id) return;

        setListingSettins(prev => ({ ...prev, [key]: value}))
        console.log("обновляем", key, value)
    }, [listing, setListingSettins]);

    useEffect(() => {
        console.log("listingSettings", listingSettings)
    }, [listingSettings])

    const saveListing = useCallback(async () => {
        try {
            await listingService.update(listing.id, listingSettings)
        } catch {
            notificate(dict.messages.notification.error.listingUpdate, "error")
        }
    }, [listingSettings])

    // locationChange (у тебя уже был)
    const locationChange = useCallback((lastId: number, path: number[]) => {
        console.log("[L] Последний выбранный:", lastId);
        console.log("[L] Путь:", path);
        updateListingSettings("locationId", lastId );
    }, [updateListingSettings]);

    const renderExtraSettings = () => {

        const props = {
            listing: listing,
            updateListingSettings: updateListingSettings
        }
        switch (listing.type) {
            case ("EVENT"):
                return <EventSettings {...props} />

            case ("PRODUCT"):
                return <ProductSettings {...props} />

            case ("SERVICE"):
                return <ServiceSettings {...props} />
        }
    }

    return listing ? (
        <>
            <h2>{dict.common.labels.settings.main}</h2>
            <ListingSetting title={dict.common.labels.translations}>
                <div className="form-group">
                    <ListingTranslations id={listing.id} />
                </div>
            </ListingSetting>

            <ListingSetting title={dict.common.labels.status.listing}>
                <div className="form-group">
                    <SliderCheckbox
                        checked={isActive}
                        onChange={(e) => {
                            setActive(e.target.checked);
                            updateListingSettings("active", e.target.checked);
                        }}
                        options={{
                            checked: dict.common.statuses.active, 
                            unchecked: dict.common.statuses.inactive
                        }}
                        id="listingActive"
                    />
                    <div className="form-group">
                        {listing?.temporary ? (
                            <p>({dict.common.statuses.draft})</p>
                        ) : (
                            <p>({dict.common.statuses.published})</p>
                        )} 
                    </div>
                </div>
            </ListingSetting>

            {listing && !disabledTypesForPrice.includes(listing.publicType ?? "") &&
                <ListingSetting title={dict.common.labels.price}>
                    <PriceEdit 
                        listingSettings={listingSettings} 
                        updateListingSettings={updateListingSettings}
                    />
                </ListingSetting>
            }

            <ListingSetting title={dict.common.labels.location}>
                <LocationSelector 
                    locationId={listing.locationId} 
                    onChange={locationChange}
                />
            </ListingSetting>

            <ListingSetting title={dict.common.labels.images}>
                <ListingImagesUploader 
                    updateListingSettings={updateListingSettings} 
                    listingSettings={listingSettings} 
                    listing={listing}
                />
            </ListingSetting>

            {renderExtraSettings()}

            <div className="form-actions two-columns-grid">
                <ListingEditActions
                    listing={listing}
                    saveListing={saveListing}
                />
            </div>
        </>
    ) : (
        <></>
    );
};

export default ListingSettingsMenu;