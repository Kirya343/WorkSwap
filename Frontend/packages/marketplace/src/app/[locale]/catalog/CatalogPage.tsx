"use client";

import { ReactNode, useEffect, useRef, useState } from "react";
import { useI18n } from "@core/lib/common/contexts/I18nContext";

import { listingPublicTypes } from "@core/lib/listing/constants/listingTypes"
import Pagination from "@core/components/ui/Pagination/Pagination";
import styles from "./CatalogPage.module.scss"
import CatalogHeader from "@/components/pages/catalog/CatalogHeader/CatalogHeader";
import CatalogSidebar from "@/components/pages/catalog/CatalogSidebar/CatalogSidebar";
import clsx from "clsx";
import Checkbox from "@core/components/common/checkbox/Checkbox/Checkbox"
import { useCatalogFilters } from "@core/lib/common/contexts/CatalogFiltersContext";

export default function CatalogPage({children}: {children: ReactNode}) {

    const { dict } = useI18n();

    const [sidebarOpened, setSidebarOpened] = useState<boolean>(false)

    function toggleSidebar() {
        setSidebarOpened(!sidebarOpened)
    }

    const contentRef = useRef<HTMLDivElement>(null);

    const { filters, updateFilter, totalPages } = useCatalogFilters();
    
    useEffect(() => {
        contentRef.current?.scrollTo({ top: 0, behavior: 'smooth' });
    }, [filters.page]);

    return(
        <>
            <CatalogHeader />
            <div className={styles.wrapper}>
                <div className={styles.layout}>
                    <CatalogSidebar
                        sidebarOpened={sidebarOpened}
                        toggleSidebar={toggleSidebar}
                    />
                    <main className={styles.main} ref={contentRef}>
                        <div className={styles.typesList} data-mb-swipe-ignore>
                            {listingPublicTypes.map((type) => (
                                <button
                                    key={type.key}
                                    type="button"
                                    className={clsx(styles.type, filters.type === type.key ? styles.active : "")}
                                    onClick={() => updateFilter("type", type.key != filters.type ? type.key : null)}
                                >
                                    {dict.categories.listingType[type.key]}
                                </button>
                            ))}
                        </div>
                        
                        <Checkbox
                            id="translationsCheckbox"
                            onChange={(e) => updateFilter("translationsFilter", e.target.checked)}
                            checked={!!filters.translationsFilter}
                            className="media-only-block"
                        >{dict.common.catalog.sidebar.translationsFilter}</Checkbox>

                        {children}

                        <Pagination 
                            page={filters.page || 0} 
                            totalPages={totalPages} 
                            onChange={(page) => updateFilter("page", page)}
                        />
                    </main>
                </div>
            </div>
        </>
    );
};