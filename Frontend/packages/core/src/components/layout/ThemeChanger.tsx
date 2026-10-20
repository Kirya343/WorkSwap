"use client";

import { useEffect, useState } from "react";
import SunIcon from "../common/icons/SunIcon";
import MoonIcon from "../common/icons/MoonIcon";
import SliderCheckbox from "../common/checkbox/SliderCheckbox/SliderCheckbox";
import { useI18n } from "@core/lib/common/contexts/I18nContext";

type Theme = "light" | "dark";

const ThemeChanger = ({ id }: { id: string }) => {
    const [theme, setTheme] = useState<Theme>("light");
    const { dict } = useI18n();

    useEffect(() => {
        const savedTheme = localStorage.getItem("theme");

        if (savedTheme === "light" || savedTheme === "dark") {
            setTheme(savedTheme);
            document.documentElement.setAttribute("data-theme", savedTheme);
            return;
        }

        const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        const initialTheme = prefersDark ? "dark" : "light";

        setTheme(initialTheme);
        document.documentElement.setAttribute("data-theme", initialTheme);
    }, []);

    const applyTheme = (newTheme: Theme) => {
        setTheme(newTheme);
        localStorage.setItem("theme", newTheme);

        const expires = new Date(
            Date.now() + 365 * 24 * 60 * 60 * 1000
        ).toUTCString();

        document.cookie = `theme=${newTheme}; path=/; expires=${expires}`;
        document.documentElement.setAttribute("data-theme", newTheme);
    };

    return (
        <SliderCheckbox
            id={id}
            checked={theme === "dark"}
            onChange={(e) => {
                applyTheme(e.target.checked ? "dark" : "light");
            }}
            icons={[<MoonIcon key="moon" />, <SunIcon key="sun" />]}
            options={{checked: dict.common.theme.dark, unchecked: dict.common.theme.light}}
        />
    );
};

export default ThemeChanger;