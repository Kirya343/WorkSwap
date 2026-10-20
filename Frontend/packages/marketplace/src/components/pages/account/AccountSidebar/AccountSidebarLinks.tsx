"use client"

import NavItem from '@core/components/common/NavItem';
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import styles from "./AccountSidebar.module.scss";
import CardsIcon from "@core/components/common/icons/CardsIcon"
import GearIcon from '@core/components/common/icons/GearIcon';
import MessagesIcon from "@core/components/common/icons/MessagesIcon"
import HeartIcon from '@core/components/common/icons/HeartIcon';
import ShieldIcon from '@core/components/common/icons/ShieldIcon';
import CommentIcon from '@core/components/common/icons/CommentIcon';
import GridIcon from '@core/components/common/icons/GridIcon';
import { useEffect, useState } from 'react';

const AccountSidebarLinks = () => {

    const { dict } = useI18n();

    const [isMobile, setIsMobile] = useState(false);
    
    useEffect(() => {
        const check = () => setIsMobile(window.innerWidth <= 600);

        check();
    }, [])

    const links = [
        /* { key: "account", icon: "fa-user" }, */
        { key: "my-listings", icon: <CardsIcon className={styles.icon} /> },
        { key: "favorites", icon: <HeartIcon size={24} /> },
        { key: "messenger", icon: <CommentIcon size={22} />},
        { key: "reviews", icon: <CommentIcon size={22}/> },
        { key: "settings", icon: <GearIcon size={24}/> },
        { key: "security", icon: <ShieldIcon className={styles.icon} />},
    ];


    return (
        <nav className={styles.accountMenu}>
            {isMobile && (
                <>
                    <NavItem
                        href='/catalog'
                        className={styles.accountMenuItem}
                    >
                        <GridIcon size={24} />
                        {dict.navigation.catalog}
                    </NavItem>
                    <NavItem
                        href='/forum'
                        className={styles.accountMenuItem}
                    >
                        <MessagesIcon size={24} />
                        {dict.navigation.forum}
                    </NavItem>
                </>
            )}
            {links.map((link) => (
                <NavItem
                    key={link.key}
                    href={`/account/${link.key}`}
                    className={styles.accountMenuItem}
                    activeClassName={styles.active}
                >
                    {link.icon}
                    {dict.navigation.accountSidebar.links[link.key]}
                </NavItem>
            ))}
        </nav>
    );
};

export default AccountSidebarLinks;