"use client";

import { IShortUser, IShortUserProfile, IUser } from "@core/lib/user/types";
import { redirect } from "next/navigation";
import styles from "./Avatar.module.scss";

interface AvatarProps {
    user: IShortUser | IUser | IShortUserProfile | null;
    size: number | null;
    className?: string;
    link?: boolean;
}

const Avatar = ({ user, size = 40, className = "", link = true}: AvatarProps) => {

    const interactive = !!(link && user?.sub)

    const handleClick = () => {
        if (interactive) redirect(`/profile/${user?.sub}`);
    };

    return (
        <img
            className={`${className} ${styles.avatar} ${interactive ? styles.interactive : ""}`}
            src={user?.avatarUrl || "/images/placeholders/avatar-placeholder.png"}
            alt="Аватар"
            style={{
                width: `${size}px`,
                height: `${size}px`,
                borderRadius: "50%",
            }}
            onClick={handleClick}
            onError={(e) => { e.currentTarget.src = "/images/placeholders/avatar-placeholder.png"; }}
        />
    );
};

export default Avatar;