export function randomString(length: number): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

export function formatSignedValue(value: number) {
    if (value > 0) {
        return `+${value}`;
    }

    if (value < 0) {
        return `-${value}`;
    }

    return "+0";
}