import { useCatalogFilters } from "@core/lib/common/contexts/CatalogFiltersContext";
import { useState, useRef, useMemo, ReactNode, useEffect } from "react";
import { ICategory } from "@core/lib/category/types"
import { ListingType } from "@core/lib/listing/constants/listingTypes";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./CatalogCategories.module.scss"
import ListIcon from "@core/components/common/icons/ListIcon"
import { categoryService } from "@core/lib/category/services"
import { createPortal } from "react-dom";
import clsx from "clsx";

const CatalogCategories = () => {
    
    const { filters, updateFilter } = useCatalogFilters();
    
    const [categoriesMenu, setCategoriesMenu] = useState<boolean>(false);
    const [subcategories, setSubcategories] = useState<ICategory[] | null>(null);
    const { categories, listingType, setListingType, rootCategories } = categoryService.useCategories();
    const { dict } = useI18n();
    const [modalRoot, setModalRoot] = useState<HTMLElement | null>(null);
    
    useEffect(() => {
        setModalRoot(document.getElementById("modal-root"));
    }, []);

    const timeoutRef = useRef<number>(0);

    const children = (parentId: number | null) => {
        if (!categories || !parentId) return [];
        return categories?.filter(cat => cat.parentId === parentId) || []
    };

    const selectedCategory = useMemo<ICategory | null>(() => {
        if (!categories || !filters.categoryId) return null;
        return categories?.find(cat => cat.id === filters.categoryId) || null
    }, [categories, filters]);

    const handleMouseLeave = () => {
        timeoutRef.current = window.setTimeout(() => setCategoriesMenu(false), 500);
    };
    const handleMouseEnter = () => clearTimeout(timeoutRef.current);

    const selectCategory = (categoryId: number) => {
        if (filters.categoryId === categoryId) {
            updateFilter("categoryId", null);
            setSubcategories(null)
            return;
        } else {
            if (!categories) return;
            const cat = categories?.find(cat => cat.id === categoryId)

            if (!cat) return;
            const hasChildren = children(cat.id).length > 0
            setSubcategories(hasChildren ? children(cat.id) : children(cat?.parentId))
            updateFilter("categoryId", categoryId);
        }
    }

    function renderCategoriesWindow() {
        return (
            <div
                className={`${styles.menu} ${categoriesMenu ? styles.active : ""}`}
                onMouseEnter={handleMouseEnter}
                onMouseLeave={handleMouseLeave}
            >
                <div className={styles.types}>
                    {[ListingType.SERVICE, ListingType.PRODUCT].map(type =>
                        <button
                            key={type}
                            className={`${styles.listingTypeBtn} ${listingType == type ? styles.active : ""} hover`}
                            value={type}
                            onClick={() => setListingType(type)}
                        >
                            {dict.categories.listingType[type.toUpperCase()]}
                            <div className={`${styles.indicator} ${listingType == type ? styles.active : ""}`}>
                                <i className="fa-solid fa-angle-down"></i>
                            </div>
                        </button>
                    )}
                </div>
                <div className={styles.container}>
                    <div className={styles.sidebarContainer}>
                        <div className={styles.categoriesList}>
                            {rootCategories.map((cat) =>
                                <CategoryButton
                                    key={`category-${cat.id}`}
                                    active={filters.categoryId === cat.id || cat.id === selectedCategory?.parentId}
                                    onClick={() => selectCategory(cat.id)}
                                >
                                    {dict.categories.category[listingType][cat.name]}
                                </CategoryButton>
                            )}
                        </div>
                    </div>
                    <div className={styles.subcategories}>
                        {subcategories?.map(child =>
                            <SubCategoryButton
                                active={filters.categoryId === child.id}
                                key={`subCategory-${child.id}`}
                                onClick={() => selectCategory(child.id)}
                            >
                                {dict.categories.category[listingType][child.name]}
                            </SubCategoryButton>
                        )}
                    </div>
                </div>
                {selectedCategory && (
                    <span className={styles.selectedCategoryLabel}>
                        <span>{dict.tooltips.catalog.selectedCategory}: </span>
                        <span className={styles.selectedCategory}>{dict.categories.category[listingType][selectedCategory.name]}</span>
                    </span>
                )}
            </div>
        )
    }

    return (
        <>
            <button 
                className={styles.categoriesBtn} 
                onClick={() => setCategoriesMenu(prev => !prev)}
            >
                <ListIcon />
                {/*<span className="normal-only">{dict.categories.category['all-categories']}</span>*/}
            </button>

            {modalRoot && createPortal(renderCategoriesWindow(), modalRoot)}
        </>
    );
};

const CategoryButton = ({ active, onClick, children }: {active: boolean, onClick: () => void, children: ReactNode}) => (
    <button
        type="button"
        className={clsx(styles.category, "hover", active ? styles.active : "")}
        onClick={onClick}
    >
        {children}
        <div className={clsx(styles.indicator, active ? styles.active : "")}>
            <i className="fa-solid fa-angle-right"></i>
        </div>
    </button>
);

const SubCategoryButton = ({ active, onClick, children }: {active: boolean, onClick: () => void, children: ReactNode}) => (
    <button
        type="button"
        className={clsx(styles.subCategory, "hover", active ? styles.active : "")}
        onClick={onClick}
    >
        {children}
    </button>
);

export default CatalogCategories;
