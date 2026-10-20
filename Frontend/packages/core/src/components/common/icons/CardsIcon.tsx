const CardsIcon = ({className}: {className?: string}) => {
    return (
        <svg 
            width={24} height={24}
            className={className}
            viewBox="0 0 16 16" 
            xmlns="http://www.w3.org/2000/svg" 
            version="1.1" 
            fill="none" stroke="currentColor" 
            strokeLinecap="round" strokeLinejoin="round" 
            strokeWidth="1.5"
        >
            <rect height="11.5" width="8.25" y="2.75" x="1.75"/>
            <path d="m10 3.75 4.25 2-4.25 7.5"/>
        </svg>
    )
}

export default CardsIcon;