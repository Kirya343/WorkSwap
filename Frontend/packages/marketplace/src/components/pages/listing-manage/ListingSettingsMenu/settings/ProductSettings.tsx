import { useCallback, } from "react";
import ListingSetting from "../ListingSetting/ListingSetting";
import { IFullListing } from "@core/lib/listing/types";
import CategorySelector from "@/components/ui/selectors/CategorySelector";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { UpdateListing } from "../ListingSettingsMenu";

const ProductSettings = ({
    updateListingSettings,
    listing
}: {
    updateListingSettings: UpdateListing
    listing: IFullListing
}) => {

    const { dict } = useI18n();

    // categoryChange
    const categoryChange = useCallback((lastId: number, path: number[]) => {
        updateListingSettings("categoryId", lastId);
    }, [updateListingSettings]);

    return (
        <>
            <h2 className="two-columns-grid">{dict.common.labels.settings.product}</h2>
            <ListingSetting title={dict.common.labels.category}>
                <CategorySelector listing={listing} onChange={categoryChange} />
            </ListingSetting>
        </>
    );
};

export default ProductSettings;