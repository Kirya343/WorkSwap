"use client"

import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useEffect, useState } from "react";

const PrivacyPolicyPage = () => {

    const { locale } = useI18n();

    const [privacyPolicy, setPrivacyPolicy] = useState("");

    useEffect(() => {
        async function loadPrivacyPolicy() {
            const data = await fetch(`https://cloud.workswap.org/info/privacy-policy_${locale}.txt`)
            setPrivacyPolicy(await data.text());
        }

        loadPrivacyPolicy();
    }, [locale])

    return (
        <div className="terms-page">
            <div>{privacyPolicy}</div>
        </div>
    );
};

export default PrivacyPolicyPage;