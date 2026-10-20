"use client"

import { useCallback, useEffect, useMemo, useState } from "react";
import { IShortUser, IUser } from "@core/lib/user/types";
import { userService } from ".";
import { redirect, useRouter } from "next/navigation";
import { AUTH_BASE } from "@core/config";

export function useCurrentUser() {
    const [user, setUser] = useState<IUser | null>(null);
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const isAuthenticated = useMemo<boolean>(() => {
        if (!user) return false;
        return user?.email?.length > 0;
    }, [user]);
    const isAdmin = useMemo<boolean>(() => user?.roles?.some(r => r.name === "ADMIN") ?? false, [user]);
    const shortUser = useMemo<IShortUser | null>(() => {
        if (!isAuthenticated || !user) return null;
        return { sub: user.sub, name: user.name, avatarUrl: user.avatarUrl ?? "" }
    }, [isAuthenticated, user])

    useEffect(() => {
        let cancelled = false;

        userService.getCurrent().then(data => {
            if (!cancelled) {
                setUser(data);
                setLoading(false);
            }
        });

        return () => {
            cancelled = true;
        };
    }, []);

    const logout = useCallback(async () => {
        try {
            await fetch(AUTH_BASE + "/api/auth/logout", { method: "POST", credentials: "include", });
            setUser(null)
        } catch (e) {
            console.error("Logout failed", e);
        } finally {
            router.push("/");
        }
    }, []);

    return { user, isAuthenticated, isAdmin, shortUser, loading, logout };
}