"use client"

import { formatDistanceToNow } from 'date-fns'
import { ru } from 'date-fns/locale'
import React from 'react'

interface FormattedDateToNowProps extends React.HTMLAttributes<HTMLDivElement> {
    date: string
}

const FormattedDateToNow = ({date, ...props}: FormattedDateToNowProps) => {
    return (
        <div {...props}>
            {formatDistanceToNow(
                new Date(date.endsWith('Z') ? date : date + 'Z'), 
                { 
                    addSuffix: true, 
                    locale: ru 
                }
            )}
        </div>
    )
}

export default FormattedDateToNow