"use client"

import { useRouter } from "next/navigation";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import ArrowIcon from "@core/components/common/icons/ArrowIcon";
import HomeIcon from "@core/components/common/icons/HomeIcon";
import styles from "./NotFoundPage.module.scss";
import Link from "next/link";

const NotFoundPage = () => {

    const router = useRouter();
    const { dict } = useI18n();

    return (
        <div className={styles.page}>
            <div className={styles.card}>
                <h1>{dict.common.fallbacks.pageNotFound}</h1>
                <div className={styles.actions}>
                    <Link href="/" className={`${styles.button} btn btn-primary`}>
                        <HomeIcon className={styles.homeIcon} />
                        Главная
                    </Link>
                    <button onClick={() => router.back()} className={`${styles.button} btn btn-outline-primary`}>
                        <ArrowIcon left className={styles.arrowIcon} />
                        {dict.navigation.back}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default NotFoundPage;