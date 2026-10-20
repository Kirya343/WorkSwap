
import { ReactNode } from "react";
import AccountSidebar from "@/components/pages/account/AccountSidebar/AccountSidebar";
import { PrivateRoute } from "@core/routes";
import styles from "./AccountLayout.module.scss"

const AccountLayout = ({children}: {children: ReactNode}) => {

    return (
        <PrivateRoute>
            <div className={styles.accountLayout}>

                <AccountSidebar/>

                <main className={styles.accountMain}>
                    <div className={`${styles.card}`}>
                        {children}
                    </div>
                </main>
            </div>
        </PrivateRoute>
    );
};

export default AccountLayout;