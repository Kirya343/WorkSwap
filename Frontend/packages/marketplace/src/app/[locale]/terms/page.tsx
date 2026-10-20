"use client"

import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useEffect, useState } from "react";
import styles from "./TermsPage.module.scss"

export default function TermsPage() {

    const { locale } = useI18n();

    const [terms, setTerms] = useState("");

    useEffect(() => {
        async function loadTerms() {
            const data = await fetch(`https://cloud.workswap.org/info/terms_${locale}.txt`)
            setTerms(await data.text());
        }

        loadTerms();
    }, [locale])

    return (
        <div className={styles.termsPage}>
            {terms}
        </div>
    );
};