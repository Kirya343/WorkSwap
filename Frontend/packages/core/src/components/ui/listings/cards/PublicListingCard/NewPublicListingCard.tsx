"use client"

import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./PublicListingCard.module.scss"
import clsx from "clsx"
import { useRouter } from "next/navigation";
import PlusIcon from "@core/components/common/icons/PlusIcon";

const NewPublicListingCard = () => {

    const { dict } = useI18n()
    const router = useRouter();

    return (
        <article key={0} onClick={() => router.push("/account/listing/create")} className={styles.card}>
            <div className={clsx(styles.imageWrapper, styles.new)}>
                <PlusIcon size={35} />
                <span className={styles.subtitle}>{dict.navigation.catalogSidebar.links.createListing}</span>
            </div>
        </article>
    );
};

export default NewPublicListingCard;