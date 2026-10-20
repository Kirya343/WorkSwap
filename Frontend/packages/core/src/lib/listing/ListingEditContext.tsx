import { createContext } from "react"
import { IFullListing } from "./types"

interface IListingEditContext {
    listing: IFullListing
}

export const ListingEditContext = createContext<IListingEditContext | null>(null);

