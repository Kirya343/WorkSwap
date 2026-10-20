import CatalogCategories from "../CatalogCategories/CatalogCategories";
import { useCatalogFilters } from "@core/lib/common/contexts/CatalogFiltersContext";
import { useI18n } from "@core/lib/common/contexts/I18nContext"
import SearchIcon from "@core/components/common/icons/SearchIcon"
import styles from "./CatalogHeader.module.scss"
import clsx from "clsx";

const CatalogHeader = () => {

    const { filters, updateFilter } = useCatalogFilters();
    const { dict } = useI18n();

    return (
        <div className={styles.header}>
            <div className={styles.content}>

                <div className={styles.search}>
                    <input 
                        type="text" 
                        className={styles.searchInput} 
                        value={filters.searchQuery ?? ""}
                        onChange={(e) => updateFilter("searchQuery", e.target.value)}
                        name="searchQuery"
                        placeholder={dict.common.placeholders.search}
                    />
                    <CatalogCategories />
                    <button className={clsx("btn", styles.btnSearch)} type="button">
                        <SearchIcon />
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CatalogHeader;