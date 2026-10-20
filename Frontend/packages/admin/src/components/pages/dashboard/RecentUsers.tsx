"use client"

import { useEffect, useState } from "react";
import { userService } from "@core/lib/user/services";
import { IUser } from "@core/lib/user/types";
import UsersTable from "../users/UsersTable/UsersTable";

const RecentUsers = () => {

    const [users, setUsers] = useState<IUser[] | null>(null);
    
    useEffect(() => {
        const loadUsers = async () => {
            try {
                const data = await userService.getRecentUsers(3);
                setUsers(data);
            } catch (err) {
                console.error(err);
            }
        };

        loadUsers();
    }, []);

    return users && (
        <>
            <h2>Последние пользователи</h2>
            {users && <UsersTable users={users}/>}
        </>
    )
};

export default RecentUsers;