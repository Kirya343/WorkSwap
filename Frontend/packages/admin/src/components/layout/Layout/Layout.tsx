"use client";

import { useEffect, useState } from "react";
import Sidebar from "@/components/layout/Sidebar/Sidebar";
import Header from "@/components/layout/Header/Header";
import { useAuth } from "@core/lib/auth/AuthContext";

import styles from "./Layout.module.scss"
import { AUTH_BASE } from "@core/config";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import Loader from "@core/components/common/Loader/Loader";

export default function Layout({ children }: any) {
    const [sidebarVisible, setSidebarVisible] = useState(false);
    const {user, isAdmin, isAuthenticated } = useAuth();
    const [url, setUrl] = useState<string | null>(null);
    const { dict } = useI18n();

    useEffect(() => {
        setUrl(window.location.href);
    }, []);

    return (
        <>
            <div className={styles.layout}>
                <Sidebar
                    sidebarVisible={sidebarVisible}
                    setSidebarVisible={setSidebarVisible}
                />

                <main className={styles.main}>

                    <Loader loadingActive={!user}>
    
                        {user && isAuthenticated && isAdmin && (
                            <>
                                <Header toggleSidebar={() => setSidebarVisible(v => !v)} />

                                {children}
                            </>
                        )}

                        {user && !isAuthenticated && (
                            <div className={styles.message}>
                                <h1 className={styles.title}>Вы не авторизированы</h1>
                                <span className={styles.subtitle}>войдите в аккаунт</span>
                                <a
                                    href={`${AUTH_BASE}/auth${url ? `?redirect=${encodeURIComponent(url || "")}` : ""}`}
                                    className={`btn btn-outline-primary ${styles.loginBtn}`}
                                >
                                    <span>{dict.buttons.login}</span>
                                </a>
                            </div>
                        )}
                        
                        {isAuthenticated && !isAdmin && (
                            <div className={styles.message}>
                                <h1 className={styles.title}>У вас нет доступа</h1>
                                <span className={styles.subtitle}>вернитесь на основной сайт</span>
                                <div className={styles.actions}>
                                    <a
                                        href={`https://workswap.org`}
                                        className={`btn btn-primary ${styles.loginBtn}`}
                                    >
                                        <span>Вернуться</span>
                                    </a>
                                    <a
                                        href={`${AUTH_BASE}/auth${url ? `?redirect=${encodeURIComponent(url || "")}` : ""}`}
                                        className={`btn btn-outline-primary ${styles.loginBtn}`}
                                    >
                                        <span>Сменить аккаунт</span>
                                    </a>
                                </div>
                            </div>
                        )}

                    </Loader>
                </main>
            </div>

            <div id="modal-root" className={styles.modalRoot}></div>
            <div id="mobile-menu"></div>
        </>
    );
}