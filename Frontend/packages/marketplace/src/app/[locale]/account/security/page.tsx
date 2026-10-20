"use client"

import { useAuth } from '@core/lib/auth/AuthContext';
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import { useNotification } from '@core/lib/notification/NotificationContext';
import { userService } from '@core/lib/user/services';
import AccountHeader from '@/components/pages/account/AccountHeader/AccountHeader';

const SecurityPage = () => {

    const { notificate } = useNotification();
    const { user, logout } = useAuth();

    const { dict } = useI18n();

    async function deleteAccount() {
        const res = await userService.deleteCurrentUser();
        if (res.ok) {
            notificate(`Account ${user?.email} successfully deleted`, 'success')
            logout();
        }
    }

    return (
        <>
            <AccountHeader title={dict.common.titles.security} />

            <button 
                onClick={() => {
                    const confirmed = window.confirm(dict.messages.confirms.deleteAccount);
                    if (confirmed) {
                        deleteAccount();
                    }
                }} 
                className='btn btn-outline-danger'
            >
                {dict.buttons.security.deleteAccount}
            </button>
        </>
    );
};

export default SecurityPage;