"use client"

import { useCallback, useEffect, useState } from "react";
import { IUser } from "@core/lib/user/types";
import { userService } from "@core/lib/user/services";
import Card from "@/components/ui/Card/Card";
import { Page } from "@core/lib/common/types/page";
import Loader from "@core/components/common/Loader/Loader";
import Pagination from "@core/components/ui/Pagination/Pagination";
import UsersTable from "@/components/pages/users/UsersTable/UsersTable";
import Breadcrumbs from "@core/components/ui/Breadcrumbs/Breadcrumbs";

export default function UsersPage() {

    const [sortParam, setSortParam] = useState<string>("id");
    const [users, setUsers] = useState<Page<IUser> | null>(null);

    const loadUsers = useCallback(async (page: number) => {
        const data: Page<IUser> = await userService.getUsersPage(page, 10, sortParam);
        setUsers(data);
    }, [])
    
    useEffect(() => {
        loadUsers(0);
    }, [sortParam])

    return (
        <>
            <Breadcrumbs
                crumbs={[
                    { href: "/dashboard", title: "Панель управления" },
                    { href: "#", title: "Управление пользователями" },
                ]}
            />

            <Card>
                <Loader loadingActive={!users?.content}>
                    {users?.content && <UsersTable users={users?.content}/>}
                    <Pagination
                        page={users?.page.number || 0} 
                        totalPages={users?.page.totalPages || 1} 
                        onChange={(page) => loadUsers(page)}
                    />
                </Loader>
            </Card>
        </>
    )
}