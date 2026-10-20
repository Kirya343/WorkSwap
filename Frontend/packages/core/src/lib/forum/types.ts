import { IShortUser } from "@core/lib/user/types";


export interface IForumTopic {
    openId: string;
    title: string;
    content: string;
    tagName: string;
    language: string;
    createdAt: string;
    author: IShortUser;
    posts: IForumPost[] | null;
    postsCount: number;
}

export interface IForumPost {
    topicOpenId: string;
    openId: string;
    content: string;
    createdAt: string;
    author: IShortUser;
    comments: IForumComment[];
}

export interface IForumTopic {
    openId: string;
    title: string;
    content: string;
    tagName: string;
    postsCount: number;
}


export interface IForumComment {
    id: number;
    content: string;
    targetOpenId: string;
    createdAt: string;
    author: IShortUser;
}

export interface ForumTag {
    id: number;
    name: string;
    parentId: number;
    leaf: boolean;
}

export interface IUserForumContent {
    topics: IForumTopic[] | null;
    posts: IForumPost[] | null;
    comments: IForumComment[] | null;
}

export interface IForumActivityItem {
    title: string;
    author: IShortUser;
    link: string;
    lang: string;
    createdAt: string;
    type: string;
}