
import { IForumComment, IForumPost, IForumTopic } from '@core/lib/forum/types';
import { apiFetch, apiFetchJson, apiFetchText } from '../common/utils/apiClient';

export const forumService = {
    getTopic: (topicOpenId: string) => apiFetchJson(`/forum/topic`, {method: "GET"}, {topicOpenId}),
    getRecentTopics: (count: number, translationsFilter: boolean) => apiFetchJson(`/forum/recent-topics`, {method: "GET"}, {count, translationsFilter}),
    getTags: () => apiFetchJson(`/forum/tags`),
    getActivity: () => apiFetchJson("/forum/activity"),

    createTopic: (topic: IForumTopic) =>
        apiFetchText(`/forum/topic`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(topic),
        }),
    createPost: (post: IForumPost) =>
        apiFetchText(`/forum/post`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(post),
        }),
    createComment: (comment: IForumComment) =>
        apiFetchText(`/forum/topic`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(comment),
        }),
    
    deleteTopic: (topicOpenId: string) => apiFetch(`/forum/topic`, { method: 'DELETE' }, {topicOpenId}),
    deletePost: (postOpenId: string) => apiFetch(`/forum/post`, { method: 'DELETE' }, {postOpenId}),
    deleteComment: (commentId: number) => apiFetch(`/forum/comment`, { method: 'DELETE' }, {commentId})  
} 