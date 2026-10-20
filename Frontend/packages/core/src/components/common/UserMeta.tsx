"use client";

import { IShortUser, IUser } from "@core/lib/user/types";
import Avatar from "./Avatar/Avatar";

interface Props {
    user: IUser | IShortUser;
    height: number;
}

const userNameStyle = {
    fontWeight: 600,
    fontSize: 'large'
}

const userMetaStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem'
}

const UserMeta = ({user, height}: Props) => {
    return (
        <div style={userMetaStyle}>
            <Avatar user={user} size={height} />
            <span style={userNameStyle}>{user.name}</span>
        </div>
    )
}

export default UserMeta;