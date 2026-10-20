import { useI18n } from '@core/lib/common/contexts/I18nContext';
import { useNotification } from '@core/lib/notification/NotificationContext';
import { listingService } from '@core/lib/listing/services';
import { IFullListing } from '@core/lib/listing/types';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

const ListingEditActions = ({listing, saveListing}: {listing: IFullListing, saveListing: () => void}) => {

    const router = useRouter();
    const { dict } = useI18n();
    const { notificate } = useNotification();

    return (
        <>
            <button 
                onClick={saveListing}
                type="button" 
                className="btn btn-success"
            >
                {dict.buttons.listing.save}
            </button>

            <button 
                onClick={() => {
                    const confirmed = window.confirm(dict.messages.confirms.deleteListing);
                    if (confirmed) {
                        listingService.deleteListing(listing.id)
                            .then(() => notificate(dict.messages.notification.success.listingDelete, "success"))
                            .catch(() => notificate(dict.messages.notification.error.listingDelete, "error"));
                        router.push(`/account/my-listings`);
                    }
                }}
                type="button" 
                className="btn btn-outline-primary"
            >
                {dict.buttons.listing.delete}
            </button>
            
            <Link
                href="/account/my-listings" 
                type="button" 
                className="btn btn-outline-primary"
            >
                {dict.buttons.listing.goToMyListings}
            </Link>

            {listing.temporary && (
                <button 
                    onClick={() => listingService.publish(listing.id)
                        .then(() => {
                            notificate(dict.messages.notification.success.publish, "success");
                            router.push(`/account/my-listings`)
                        })
                    } 
                    type="button" 
                    className="btn btn-primary"
                >
                    {dict.buttons.listing.publish}
                </button>
            )}
        </>
    );
};

export default ListingEditActions;