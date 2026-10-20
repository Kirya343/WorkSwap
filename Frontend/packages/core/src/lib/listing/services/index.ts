import * as api from './listing.service';
import * as hooks from './listing.hooks';

export const listingService = {
    ...api,
    ...hooks
};