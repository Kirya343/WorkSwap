"use client"

import { ReactNode } from "react";
import { useAuth } from "@core/lib/auth/AuthContext";
import { AUTH_BASE } from "@core/config";
import { redirect } from "next/navigation";

const PrivateRoute = ({children}: {children: ReactNode}) => {
    const { loading, isAuthenticated } = useAuth();

    if (!isAuthenticated && !loading) {
        return redirect(`${AUTH_BASE}/auth?redirect=${encodeURIComponent(window.location.origin + window.location.href)}`);
    }

    if (loading) {
        return <></>;
    }

    return children;
};

export default PrivateRoute;