export const ChatType = Object.freeze({
    LISTING_DISCUSSION: "LISTING_DISCUSSION",
    PRIVATE_CHAT: "PRIVATE_CHAT",
    EVENT_TOPIC: "EVENT_TOPIC"
} as const);

export type ChatTypeKey = keyof typeof ChatType;
export type ChatTypeValue = typeof ChatType[ChatTypeKey];

export const privateChatTypes: ChatTypeValue[] = [
    ChatType.LISTING_DISCUSSION,
    ChatType.PRIVATE_CHAT
];

export const chatTypes: { key: ChatTypeValue }[] = 
    Object.values(ChatType).map(value => ({ key: value }))