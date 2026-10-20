import { ChatTypeValue } from "./constants/chatTypes";
import { IShortListing } from "@core/lib/listing/types";
import { IShortUser } from "@core/lib/user/types";


export interface IChatMessage {
    id: number;
    text: string;
    sentAt: string | null;
    senderSub: string;
    chatId: number;
    read: boolean;
}

export interface GroupedMessages {
    id: number;
    senderSub: string;
    messages: IChatMessage[];
}

export interface IChat {
    id: number;
    unreadCount: number;
    lastMessageText: string;
    lastMessageTime: string;
    status: string;
    type: ChatTypeValue;
    targetId: number;

    interlocutors?: IShortUser[];
    listing?: IShortListing; 
}

export interface IChatDetails {
    chatId: number;
    interlocutors: IShortUser[];
    listing: IShortListing;
}