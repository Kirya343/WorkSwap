import { useEffect, useState } from "react";
import ListingGallery from "../ListingGallery/ListingGallery";
import { IListingPageRequest } from "@core/lib/listing/types";
import { IShortUserProfile } from "@core/lib/user/types";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { ICategory } from "@core/lib/category/types";
import { listingService } from "@core/lib/listing/services";
import { categoryService } from "@core/lib/category/services";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import HeartIcon from "@core/components/common/icons/HeartIcon"
import styles from "./ListingPageLayout.module.scss"
import SidebarCard from "../SidebarCard/SidebarCard";
import ListingPageElement from "../ListingPageElement/ListingPageElement";
import ListingAction from "../ListingAction/ListingAction";
import PenIcon from "@core/components/common/icons/PenIcon"
import ShareIcon from "@core/components/common/icons/ShareIcon"
import ItemViewLayout from "@/components/layout/ItemViewLayout/ItemViewLayout";
import { ListingTypeValue } from "@core/lib/listing/constants/listingTypes";

interface ListingPageLayoutProps {
    details: React.ReactNode,
    extraSidebarNode?: React.ReactNode,
    listingActions?: React.ReactNode,
    extraContent?: React.ReactNode,
    listingPage: IListingPageRequest
    author: IShortUserProfile | null
}

const ListingPageLayout = ({
    listingPage, 
    author, 
    details, 
    extraSidebarNode,
    listingActions,
    extraContent
}: ListingPageLayoutProps) => {

    const { listing } = listingPage;

    const { user } = useAuth();
    const isOwner = !!(user?.sub == author?.sub);

    const { isFavorite, toggleFavorite } = listingService.useFavorite(listing);
    
    const { notificate } = useNotification();

    const [categories, setCategories] = useState<ICategory[] | null>(null);
    const { dict } = useI18n();

    useEffect(() => {
        async function loadCategoryPath(categoryId: number, type: ListingTypeValue) {
            const data = await categoryService.getPathToCategory(categoryId, type)
            setCategories(data)
        }
        if (listing?.categoryId && listing?.type) loadCategoryPath(listing.categoryId, listing.type);
    }, [listing])

    return (
        <ItemViewLayout
            breadcrumbs={[
                { href: "/catalog", title: dict.navigation.catalog },

                ...(listing?.publicType ? [{ 
                    href: `/catalog?type=${listing?.publicType}`, 
                    title: dict.categories.listingType[listing?.publicType] 
                }] : []),

                ...(categories ? categories.map(cat => ({
                    href: `/catalog?type=${listing?.publicType}&category=${cat.id}`,
                    title: listing?.type
                        ? dict.categories.category[listing.type][cat.name]
                        : "",
                })): []),

                { href: "#", title: listing?.localizedTitle },
            ]}

            header={(
                <>
                    <h1>{listing?.localizedTitle}</h1>
                    <div className={styles.meta}>
                        <span>{listing?.publishedAt ? new Date(listing?.publishedAt).toLocaleDateString("ru-RU") : ""}</span>
                        <span className={styles.views}>
                            {dict.common.labels.views}: <span>{listing?.views}</span>
                        </span>
                    </div>
                </>
            )}

            content={(
                <>
                    <ListingGallery images={listingPage?.images ?? []}/>
                    
                    {listing?.localizedDescription && (
                        <ListingPageElement title={dict.common.labels.description}>
                            <p className={styles.description}>
                                {listing.localizedDescription || "Нет описания"}
                            </p>
                        </ListingPageElement>
                    )}

                    {extraContent}
                </>
            )}

            primarySidebarNode={
                <SidebarCard>
                    {details}

                    <div className={styles.actions}>
                        {listingActions}
                        {listing && (
                            <>
                                {!isOwner ? (
                                    <ListingAction onClick={toggleFavorite}>
                                        <HeartIcon
                                            filled={isFavorite}
                                            className={"like"}
                                            size={24}
                                        />
                                    </ListingAction>
                                ) : (
                                    <ListingAction
                                        href={`/account/listing/edit/${listing.id}`}
                                    >
                                        <PenIcon />
                                    </ListingAction>
                                )}
                            </>
                        )}
                        <ListingAction 
                            onClick={() => {
                                navigator.clipboard.writeText(window.location.href)
                                    .then(() => notificate(dict.messages.notification.success.copyListingLink, "success"))
                                    .catch(() => notificate("Ошибка", "error"));
                            }}
                        >
                            <ShareIcon />
                        </ListingAction>
                    </div>
                </SidebarCard>
            }

            sidebarNode={extraSidebarNode}

            listingId={listing.id}

            author={author ?? undefined}
        />
    );
}

export default ListingPageLayout;