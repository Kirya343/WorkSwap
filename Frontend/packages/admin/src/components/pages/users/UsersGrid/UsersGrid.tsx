"use client"

import { useEffect, useState } from "react";
import UserCard from "../UserCard/UserCard";
import styles from "./UsersGrid.module.scss"
import { userService } from "@core/lib/user/services";
import { IUser } from "@core/lib/user/types";

const UsersGrid = () => {

    const [users, setUsers] = useState<IUser[] | null>(null);

    useEffect(() => {
        async function loadUsers(amount:number) {
            const data = await userService.getRecentUsers(amount);
            setUsers(data);
        }

        loadUsers(30);
    }, [])
    
    return users && (
        <div className={styles.grid}>
            {users.map((user: IUser) => (
                <UserCard user={user} key={user.sub}/>
            ))}
        </div>
    );
};

export default UsersGrid;