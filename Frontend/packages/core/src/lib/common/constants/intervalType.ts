export const Intervals = Object.freeze({
    ONE_DAY: {
        type: "DAY",
        multiplier: 1,
        title: "1 День"
    },

    THREE_DAYS: {
        type: "DAY",
        multiplier: 3,
        title: "3 Дня"
    },

    ONE_WEEK: {
        type: "DAY",
        multiplier: 7,
        title: "1 Неделя"
    },

    TWO_WEEKS: {
        type: "DAY",
        multiplier: 14,
        title: "2 недели"
    },

    ONE_MONTH: {
        type: "DAY",
        multiplier: 30,
        title: "1 Месяц"
    },
} satisfies Record<string, Interval>);

export interface Interval {
    type: IntervalType, 
    multiplier: number, 
    title: string
}

type IntervalType = "DAY";