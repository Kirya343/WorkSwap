import * as api from './category.service';
import * as hooks from './category.hooks';

export const categoryService = {
    ...api,
    ...hooks
};