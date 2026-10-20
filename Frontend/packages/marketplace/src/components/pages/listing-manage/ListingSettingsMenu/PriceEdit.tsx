import { useEffect, useState } from "react";
import {settingService} from "@core/lib/common/services/settingsService"
import { IFullListing, IListingUpdate } from "@core/lib/listing/types";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { UpdateListing } from "./ListingSettingsMenu";

interface PriceEditProps {
    listingSettings: IListingUpdate
    updateListingSettings: UpdateListing
}
const PriceEdit = ({ listingSettings, updateListingSettings }: PriceEditProps) => {

    const { dict } = useI18n();

    const [priceTypes, setPriceTypes] = useState([])

    const priceType = listingSettings.priceType;

    useEffect(() => {
    
        async function loadPriceTypes() {
            const data = await settingService.getSupportedPriceTypes();
            setPriceTypes(data);
        }

        loadPriceTypes();
    }, [])

    return (
        <div className="form-group">
            <div className="duo">
                {(priceType != 'NEGOTIABLE' && priceType != 'SWAP') && (
                    <input
                        className="form-control first"
                        type="number"
                        id="price"
                        name="price"
                        value={listingSettings.price ?? ""}
                        onChange={(e) => updateListingSettings("price", Number(e.target.value))}
                        step="0.01"
                        required
                    />
                )}
                <select
                    id="priceType"
                    name="priceType"
                    className={`form-control ${(priceType != 'NEGOTIABLE' && priceType != 'SWAP') ? 'second' : ''}`}
                    required
                    value={priceType ?? ""}
                    onChange={(e) => updateListingSettings("priceType", e.target.value)}
                >
                    <option value="" disabled>{dict.common.placeholders.priceType}</option>
                    {priceTypes.map((type) => (
                        <option key={type} value={type}>
                            {dict.common.priceTypes[type]}
                        </option>
                    ))}
                </select>
            </div>
        </div>
    );
};

export default PriceEdit;