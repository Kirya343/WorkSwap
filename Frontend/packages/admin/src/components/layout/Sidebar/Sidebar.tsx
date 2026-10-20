
import ThemeChanger from "@core/components/layout/ThemeChanger";
import { Dispatch, ReactNode, SetStateAction } from "react";
import NavItem from "@core/components/common/NavItem"
import styles from "./Sidebar.module.scss"
import UserIcon from "@core/components/common/icons/UserIcon"
import LocationIcon from "@core/components/common/icons/LocationIcon"
import SlidersIcon from "@core/components/common/icons/SlidersIcon"
import ShieldIcon from "@core/components/common/icons/ShieldIcon"
import SitemapIcon from "@core/components/common/icons/SitemapIcon"
import TasksIcon from "@core/components/common/icons/TasksIcon"
import CardsIcon from "@core/components/common/icons/CardsIcon"
import NewspaperIcon from "@core/components/common/icons/NewspaperIcon"
import MessagesIcon from "@core/components/common/icons/MessagesIcon"
import GaudeIcon from "@core/components/common/icons/GaudeIcon"

interface SidebarProps {
    sidebarVisible: boolean, 
    setSidebarVisible: Dispatch<SetStateAction<boolean>>
}

interface ILink {
    href: string;
    title: string;
    icon: ReactNode
    disabled?: true;
}

interface ISection {
    title: string;
    links: ILink[]
}

export default function Sidebar({ sidebarVisible, setSidebarVisible}: SidebarProps) {
    
    const nav: ISection[] = [
        { 
            title: "Основное",
            links: [
                {
                    href: "/dashboard",
                    title: "Панель управления",
                    icon: <GaudeIcon className={styles.icon} />
                },
                {
                    href: "/settings",
                    title: "Системные настройки",
                    icon: <SlidersIcon className={styles.icon} />
                }
            ]
        },
        { 
            title: "Контент",
            links: [
                {
                    href: "/listings",
                    title: "Объявления",
                    icon: <CardsIcon className={styles.icon} />
                },
                {
                    href: "/news",
                    title: "Новости",
                    icon: <NewspaperIcon className={styles.icon} />
                },
                {
                    href: "/reviews",
                    title: "Отзывы",
                    icon: <MessagesIcon className={styles.icon}/>
                }
            ]
        },
        { 
            title: "Пользователи",
            links: [
                {
                    href: "/users",
                    title: "Пользователи",
                    icon: <UserIcon className={styles.icon} />
                },
                {
                    href: "/permissions",
                    title: "Роли и разрешения",
                    icon: <ShieldIcon className={styles.icon} />
                }
            ]
        },
        { 
            title: "Настройки данных",
            links: [
                {
                    href: "/locations",
                    title: "Локации",
                    icon: <LocationIcon className={styles.icon} />
                },
                {
                    href: "/categories",
                    title: "Категории",
                    icon: <SitemapIcon className={styles.icon} />
                }
            ]
        },
        { 
            title: "Организация",
            links: [
                {
                    href: "/tasks",
                    title: "Задачи",
                    icon: <TasksIcon className={styles.icon} />
                },
            ]
        }
    ]
    return (
        <aside className={`${styles.sidebar} ${sidebarVisible ? styles.show : ''}`}>
            <div className="text-center">
                <a href="https://workswap.org">
                    <h2 className={styles.logo}>WorkSwap</h2>
                </a>
            </div>

            <div className={styles.themeContainer}>
                <ThemeChanger id={"headerTC"} />
            </div>

            <nav className={styles.nav}>
                {nav.map(section => (
                    <div key={section.title}>
                        <div className={styles.title}>{section.title}</div>
                        {section.links.map(link => (
                            <NavItem 
                                key={`link-${link.href}`}
                                href={link.href} 
                                className={styles.navItem} 
                                onClick={() => setSidebarVisible(false)}
                            >
                                {link.icon} {link.title}
                            </NavItem>
                        ))}
                    </div>
                ))}
            </nav>
        </aside>
    );
}