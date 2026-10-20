import Avatar from "@core/components/common/Avatar/Avatar";
import SignOutIcon from "@core/components/common/icons/SignOutIcon"
import { useAuth } from "@core/lib/auth/AuthContext";
import styles from "./Header.module.scss";
import BurgerIcon from "@core/components/common/icons/BurgerIcon"
import { pageTitles, useActivePage } from "@core/lib/common/contexts/ActivePageContext";

const Header = ({ toggleSidebar }: {toggleSidebar: () => void}) => {

    const { user, logout } = useAuth();
    const activePage = useActivePage();
    
    return (
        <header className={styles.header}>
            <h1>{activePage && pageTitles[activePage]}</h1>

            <div className={styles.user}>
                <span>{user?.name || "Администратор"}</span>
                <Avatar 
                    user={user}        // передаём объект пользователя
                    size={40}          // размер аватара, например 40px
                />
                <button
                    className={styles.logoutBtn}
                    onClick={logout}
                >
                    <SignOutIcon />
                </button>
            </div>

            <div className={styles.mobileMenuBtn} onClick={() => toggleSidebar()}>
                <BurgerIcon />
            </div>
        </header>
    );
};

export default Header;