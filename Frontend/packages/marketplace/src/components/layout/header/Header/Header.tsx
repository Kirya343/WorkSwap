"use client"

import MobileMenu from "../../MobileMenu/MobileMenu";
import Link from 'next/link';
import styles from "./Header.module.scss"
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import { useAuth } from "@core/lib/auth/AuthContext";
import { useEffect, useState } from "react";
import NavItem from "@core/components/common/NavItem";
import Avatar from "@core/components/common/Avatar/Avatar";
import NotificationHeaderButton from "../NotificationHeaderButton";
import SignOutIcon from "@core/components/common/icons/SignOutIcon";
import { AUTH_BASE } from "@core/config";
import clsx from "clsx";
import SignInIcon from "@core/components/common/icons/SignInIcon";
import LanguageSwitcher from "@core/components/layout/LanguageSwitcher/LanguageSwitcher";

const Header = () => {

    const { dict } = useI18n();
    const { user, isAuthenticated, isAdmin, logout } = useAuth();
    const [url, setUrl] = useState<string | null>(null);

    useEffect(() => {
        setUrl(window.location.href);
    }, []);
    
    return (
        <div className={styles.header}>
            <div className={styles.headerContainer}>
                <nav className={styles.navbar}>
                    <Link href="/catalog" className={styles.navbarBrand}>
                        <span>WorkSwap</span>
                        <div className={styles.allListings}>
                            <div>{dict.common['all-listings']}</div>
                        </div>

                    </Link>

                    <div className={styles.mobileNavButtons}>
                        <MobileMenu />
                    </div>

                    <div className={styles.navButtons}>

                        <NavItem href="/forum" className={styles.navLink} activeClassName={styles.active}>
                            {dict.navigation.forum}
                        </NavItem>

                        <NavItem href="/catalog" className={styles.navLink} activeClassName={styles.active}>
                            {dict.navigation.catalog}
                        </NavItem>

                        {isAdmin && (
                            <a href="https://dash.workswap.org" className={styles.navLink} target="_blank" rel="noreferrer">
                                {dict.navigation.admin}
                            </a>
                        )}
                    </div>

                    <div className={styles.right}>

                        {isAuthenticated ? (
                            <div className={styles.accountLinkContainer}>
                                <Link href="/account" className={styles.accountLink}>
                                    <Avatar
                                        user={user}
                                        size={32}
                                        className=''
                                        link={false}
                                    />
                                    <span className="ellipsis">{user?.name || "Пользователь"}</span>
                                </Link>
                                <NotificationHeaderButton className={styles.accountLink} />
                                <button
                                    className={styles.accountLink}
                                    onClick={logout}
                                >
                                    <SignOutIcon />
                                </button>
                            </div>
                        ) : (
                            <a
                                href={`${AUTH_BASE}/auth${url ? `?redirect=${encodeURIComponent(url || "")}` : ""}`}
                                className={clsx(styles.loginBtn, "btn-outline-primary")}
                            >
                                <SignInIcon /><span>{dict.buttons.login}</span>
                            </a>
                        )}

                        <LanguageSwitcher/>
                    </div>
                </nav>
            </div>
        </div>
    );
};

export default Header;