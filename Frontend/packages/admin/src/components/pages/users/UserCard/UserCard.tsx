import React from "react";
import styles from "./UserCard.module.scss"
import Link from "next/link";
import Avatar from "@core/components/common/Avatar/Avatar";
import { IUser } from "@core/lib/user/types";

type UserCardProps = {
    user: IUser;
};

const UserCard: React.FC<UserCardProps> = ({user}) => {

    return (
        <Link href={`/users/${user.sub}`} className={styles.card}>
            <Avatar user={user} size={80} />
            <span>{user.name}</span>
        </Link>
    );
};

export default UserCard;