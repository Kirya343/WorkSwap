"use client"

import { categoryService } from "@core/lib/category/services"
import Card from "@/components/ui/Card/Card";
import CategoryTable from "@/components/pages/categories/CategoryTable/CategoryTable";
import styles from "./CategoriesPage.module.scss"
import Loader from "@core/components/common/Loader/Loader"
import Breadcrumbs from "@core/components/ui/Breadcrumbs/Breadcrumbs";
import { ListingType } from "@core/lib/listing/constants/listingTypes";
import clsx from "clsx"
import CategoryCreateModal from "@/components/pages/categories/CategoryCreateModal/CategoryCreateModal";
import { useState } from "react";

const CategoriesPage = () => {

    const { 
        categories, 
        loading, 
        listingType, 
        setListingType, 
        categoriesCount, 
        addCategory, 
        removeCategory 
    } = categoryService.useCategories();

    const [createModal, setCreateModal] = useState<{
        open: boolean;
        parentId: number | null;
    }>({
        open: false,
        parentId: null
    });

    const onEditCategory = (id: number) => {
        console.log("TODO: edit category", id);
    };

    

    return (
        <>
            <Breadcrumbs
                crumbs={[
                    { href: "/dashboard", title: "Панель управления" },
                    { href: "#", title: "Управление категориями" },
                ]}
            />

            <div className={styles.listingTypes}>
                {Object.values(ListingType).map(type => (
                    <button 
                        key={type}
                        className={clsx(styles.listingType, type == listingType ? styles.active : "")} 
                        onClick={() => setListingType(type)}
                    >
                        {type} ({categoriesCount.get(type)})
                    </button>
                ))}
            </div>

            <Card header={listingType}>
                <Loader loadingActive={loading}>
                    <div className={styles.page}>
                        <div className={styles.tables}>
                            <CategoryTable
                                type={listingType}
                                categories={categories}
                                onDeleteCategory={removeCategory}
                                onEditCategory={onEditCategory}
                                onCreateCategory={(parentId: number) => setCreateModal({ open: true, parentId })}
                            />
                        </div>
                    </div>
                </Loader>
            </Card>

            <CategoryCreateModal 
                isOpen={createModal.open}
                listingType={listingType}
                parentCategory={categories.find(cat => cat.id === createModal.parentId) || null}
                addCategory={addCategory}
                onClose={() => setCreateModal({
                    open: false,
                    parentId: null
                })}
            />
        </>
    );
};

export default CategoriesPage;