"use client"

import { createContext, useContext } from "react";
import { IShortUser, IUser } from "../user/types";
import { userService } from "../user/services";

interface AuthContextType {
    isAuthenticated: boolean;
    user: IUser | null;
    shortUser: IShortUser | null;
    loading: boolean;
    isAdmin: boolean;
    logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) {
        throw new Error("useAuth must be used inside AuthProvider");
    }
    return ctx;
}

export const AuthProvider = ({ children }: { children?: React.ReactNode }) => {

    const { user, isAuthenticated, loading, shortUser, isAdmin, logout } = userService.useCurrentUser();

    return (
        <AuthContext.Provider value={{ isAuthenticated, user, shortUser, loading, isAdmin, logout }}>
            {children}
        </AuthContext.Provider>
    );
};