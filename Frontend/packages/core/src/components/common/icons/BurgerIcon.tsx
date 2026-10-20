const BurgerIcon = ({className}: {className?: string}) => {
    return (
        <svg 
            className={className} 
            viewBox="0 0 24 24" 
            width={40}
            height={40}
            fill="none" 
            xmlns="http://www.w3.org/2000/svg"
        >
            <path d="M5 12H20" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <path d="M5 17H20" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <path d="M5 7H20" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
        </svg>
    )
}

export default BurgerIcon;