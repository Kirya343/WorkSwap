"use client";

const TimeCounter = ({ duration }: {duration: number}) => {
    if (duration == null) {
        return null;
    }

    // допустим duration приходит в секундах
    const seconds = Math.floor(duration);

    if (seconds < 0) {
        return <span>!!!</span>;
    }

    const days = Math.floor(seconds / 86400); // 60*60*24
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor(seconds / 60);

    let text;
    if (days >= 1) {
        text = `${days} д.`;
    } else if (hours >= 1) {
        text = `${hours} ч.`;
    } else if (minutes >= 1) {
        text = `${minutes} мин.`;
    } else {
        text = `${seconds} сек.`;
    }

    return <span>{text}</span>;
};

export default TimeCounter;