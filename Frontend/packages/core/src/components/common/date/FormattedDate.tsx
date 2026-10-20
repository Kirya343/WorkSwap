"use client"

import { useI18n } from "@core/lib/common/contexts/I18nContext";

interface Props {
    isoDate: string;
    format?: DateFormat;
}

type DateFormat =
    | "DM"
    | "DMY"
    | "DMHM"
    | "DMYHM";

export const FormattedDate = ({ isoDate, format = "DMYHM"}: Props) => {
    const { locale } = useI18n();
    if (!isoDate) return null;

    const date = new Date(isoDate);

    let options = {};

    if (format === "DM") {
        options = {
            day: "2-digit",
            month: "long"
        };
    } else if (format === "DMY") {
        options = {
            day: "2-digit",
            month: "2-digit",
            year: "2-digit"
        };
    } else if (format === "DMHM") {
        options = {
            day: "2-digit",
            month: "long",
            hour: "2-digit",
            minute: "2-digit",
        };
    } else if (format === "DMYHM") {
        options = {
            day: "2-digit",
            month: "long",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        };
    }

    const formatted = new Intl.DateTimeFormat(locale, options).format(date);

    return <span>{formatted}</span>;
};

export default FormattedDate