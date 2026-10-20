export interface Order {
    id: string;
    listingId: number;
    buyerId: number;
    sellerId: number;
    chatId: number;
    status: string;
    confirmedByBuyer: boolean;
    confirmedBySeller: boolean;
    createdAt: string;
}