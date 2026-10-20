export interface IPopupNotification {
    id: number;
    message: string;
    type: string;
}

export interface INotification {
    id: number;
    recipientId: number;
    isRead: boolean;
    title: string;
    content: string;
    link: string;
    type: string;
    importance: string;
    createdAt: string;
}