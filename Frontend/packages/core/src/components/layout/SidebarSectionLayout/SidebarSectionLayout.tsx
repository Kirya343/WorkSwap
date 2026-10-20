"use client"

import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { ReactNode, useState } from "react";
import styles from "./SidebarSectionLayout.module.scss";
import UnreadNotifications from "@core/components/ui/notifications/UnreadNotifications/UnreadNotifications";
import ArrowIcon from "@core/components/common/icons/ArrowIcon";
import clsx from "clsx"
 
export interface SidebarSection {
    first: boolean;
    name: string;
    icon: ReactNode;
}

interface SidebarSectionLayoutProps {
    pageName: string;
    sections: Record<string, SidebarSection>;
    notifications?: { menu: SidebarSection; count: number };
    children: (currentSection: SidebarSection | null) => ReactNode;
    rowMode?: boolean 
}

const SidebarSectionLayout = ({
    pageName,
    sections,
    notifications,
    children,
    rowMode,
}: SidebarSectionLayoutProps) => {

    const { dict } = useI18n();

    const params = new URLSearchParams(window.location.search);
    const isMobile = window.innerWidth <= 600;
    const section = params.get("section") || '';
    const initialMenu = findSection(section) || (isMobile ? null : findFirst());

    const [currentSection, setCurrentSection] = useState(initialMenu);
    // console.log("sections: ", sections)

    function findSection(name: string) {
        return Object.values(sections).find(s => s.name === name) || null;
    }

    function findFirst() {
        return Object.values(sections).find(s => s.first === true) || null;
    }

    return (
        <div className={`${styles.page} ${rowMode ? styles.rowMode : ""}`}>
            <div className={`${styles.sidebar} ${rowMode ? styles.rowMode : ""}`}>
                {Object.entries(sections).map(([key, section]) => (
                    <button
                        key={section.name}
                        className={clsx(
                            styles.section, 
                            "hover", 
                            section.name === currentSection?.name ? styles.active : null
                        )}
                        onClick={() => setCurrentSection(section)}
                        id={notifications?.menu.name === section.name ? "notificationAnchor" : "none"}
                    >
                        {/* {section.icon} */}
                        {dict.common[pageName].sections[key] ?? key}
                        {notifications?.menu.name === section.name && notifications.count > 0 &&
                            <UnreadNotifications count={notifications.count}/>
                        }
                    </button>
                ))}
            </div>
            <div className={`${styles.container} ${currentSection ? styles.active : ""}`} >
                <div className={`${styles.mobileActions} media-only-flex`}>
                    <button onClick={() => setCurrentSection(null)} className={styles.backBtn}>
                        <ArrowIcon left/>
                    </button>
                </div>
                
                {children && children(currentSection)}
            </div>
        </div>
    );
}

export default SidebarSectionLayout;