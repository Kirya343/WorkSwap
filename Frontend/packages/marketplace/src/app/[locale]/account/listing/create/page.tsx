"use client"

import { listingPublicTypes, ListingPublicTypeValue } from "@core/lib/listing/constants/listingTypes";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { listingService } from "@core/lib/listing/services";
import { useRouter } from "next/navigation";
import { useState } from "react";
import styles from "./ListingCreatePage.module.scss"
import AccountHeader from "@/components/pages/account/AccountHeader/AccountHeader";
import clsx from "clsx";

export default function ListingCreatePage() {

    const { notificate } = useNotification();
    const [listingType, setListingType] = useState<ListingPublicTypeValue | null>(null);
    const { dict } = useI18n();
    const router = useRouter();
    const productTypes = listingPublicTypes.filter(t => t.key.startsWith("PRODUCT"));
    const serviceTypes = listingPublicTypes.filter(t => t.key.startsWith("SERVICE"));
    const miscTypes = listingPublicTypes.filter(t => !t.key.startsWith("SERVICE") && !t.key.startsWith("PRODUCT"));

    const createListing = async () => {
        try {

            const data = await listingService.create(String(listingType))

            notificate(dict.messages.notification.success.createDraft, "success");
            router.push(`/account/listing/${data}/manage`)

        } catch {
            notificate(dict.messages.notification.misc.error.listingCreate, "error")
        }
    }

    return (
        <>
            <AccountHeader backLink={'/account/my-listings'} title={dict.common.titles.listingCreate}/>

            <div className={styles.container}>
                <h3>{dict.common.listingCreate.selectType}</h3>

                <div className={styles.typeSelector}>
                    <div className={styles.section}>
                        <h2>{dict.categories.listingType.SERVICE}</h2>
                        {serviceTypes.map((type) => (
                            <button 
                                key={type.key}
                                className={clsx("btn", `btn-${listingType == type.key ? "" : "outline-"}primary`)}
                                onClick={() => setListingType(type.key)}
                            >
                                {dict.categories.listingType.create[type.key]}
                            </button>
                        ))}
                    </div>
                    <div className={styles.section}>
                        <h2>{dict.categories.listingType.PRODUCT}</h2>
                        {productTypes.map((type) => (
                            <button 
                                key={type.key}
                                className={clsx("btn", `btn-${listingType == type.key ? "" : "outline-"}primary`)}
                                onClick={() => setListingType(type.key)}
                            >
                                {dict.categories.listingType.create[type.key]}
                            </button>
                        ))}
                    </div>
                    
                    <div className={styles.section}>
                        <h2>{dict.categories.listingType.misc}</h2>
                        {miscTypes.map((type) => (
                            <button 
                                key={type.key}
                                className={clsx("btn", `btn-${listingType == type.key ? "" : "outline-"}primary`)}
                                onClick={() => setListingType(type.key)}
                            >
                                {dict.categories.listingType.create[type.key]}
                            </button>
                        ))}
                    </div>
                </div>
                <button 
                    className="btn btn-success"
                    onClick={createListing}
                    disabled={!listingType}
                >
                    {dict.buttons.listing.createListing}
                </button>
            </div>
        </>
    );
}