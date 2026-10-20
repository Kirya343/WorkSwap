"use client"

import Card from "@/components/ui/Card/Card";
import { IShortListing } from "@core/lib/listing/types";
import { IFullUser } from "@core/lib/user/types";
import { IReview } from "@core/lib/review/types"
import { IUserForumContent } from "@core/lib/forum/types"
import { useEffect, useState } from "react";
import { userService } from "@core/lib/user/services"
import { useParams } from "next/navigation";
import PublicListingCard from "@core/components/ui/listings/cards/PublicListingCard/PublicListingCard";
import styles from "./UserControlPage.module.scss"

const UserControlPage = () => {
    const { sub } = useParams();
    const [user, setUser] = useState<IFullUser | null>(null)
    const [listings, setListings] = useState<IShortListing[] | null>(null)
    const [reviews, setReviews] = useState<IReview[] | null>(null)
    const [forumContent, setForumContent] = useState<IUserForumContent | null>(null)

    useEffect(() => {
        async function loadUserInfo() {
            if (!sub) return;
            const data = await userService.getFullUserInfo(String(sub));
            setUser(data.user);
            setListings(data.listings);
            setReviews(data.reviews);
            setForumContent(data.forumContent);
        }
        loadUserInfo()
    }, [sub])

    return (
        <Card>
            {user && (
                <ul className={styles.userInfo}>
                    <li>ID: {user.id}</li>
                    <li>OpenID: {user.sub}</li>
                    <li>Тип пользователя: {user.type}</li>
                    <li>Имя: {user.name}</li>
                    <li>Телефон: {user.phone ?? '—'}</li>
                    <li>Email: {user.email}</li>
                    <li>О себе: {user.bio ?? '—'}</li>
                    <li>URL аватара: {user.avatarUrl ?? '—'}</li>
                    <li>Провайдер: {user.provider}</li>
                    <li>Языки: {user.languages.join(', ')}</li>
                    <li>Роли: {user.roles.join(', ')}</li>
                    <li>ID локации: {user.locationId ?? '—'}</li>
                    <li>Статус: {user.status ?? '—'}</li>
                    <li>Тип аватара: {user.avatarType}</li>
                    <li>Рейтинг: {user.rating ?? '—'}</li>
                    <li>Telegram подключён: {user.telegramConnected ? 'Да' : 'Нет'}</li>
                    <li>Дата создания: {user.createdAt}</li>
                    <li>Аватар Google: {user.googleAvatar ?? '—'}</li>
                    <li>Загруженный аватар: {user.uploadedAvatar ?? '—'}</li>
                    <li>Телефон виден: {user.phoneVisible ? 'Да' : 'Нет'}</li>
                    <li>Email виден: {user.emailVisible ? 'Да' : 'Нет'}</li>
                </ul>
            )}

            {listings?.map(listing => <PublicListingCard listing={listing}/>)}
        </Card>
    )
}

export default UserControlPage;