import * as api from './user.service';
import * as hooks from './user.hooks';

export const userService = {
    ...api,
    ...hooks
};