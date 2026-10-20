"use client"

import { useState, useEffect, ReactElement } from 'react';

interface DelayedListProps {
    className?: string;
    items: ReactElement[];
}


export function DelayedList({ className, items }: DelayedListProps) {
    const [count, setCount] = useState(0);

    useEffect(() => {
        setCount(0);

        if (!items.length) {
            return;
        }

        const interval = setInterval(() => {
            setCount(prev => {
                if (prev + 1 >= items.length) {
                    clearInterval(interval);
                }
                return prev + 1;
            });
        }, 100);

        return () => clearInterval(interval);
    }, [items]);

    return (
        <>
            {items.slice(0, count).map((item, index) => (
                <div key={item.key ?? index}>
                    {item}
                </div>
            ))}
        </>
    );
}