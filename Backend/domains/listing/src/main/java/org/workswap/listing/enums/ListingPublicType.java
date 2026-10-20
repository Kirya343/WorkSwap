package org.workswap.listing.enums;

public enum ListingPublicType {
    SERVICE_OFFER(ListingType.SERVICE, ServiceType.OFFER, null),
    SERVICE_REQUEST(ListingType.SERVICE, ServiceType.REQUEST, null),

    PRODUCT_SALE(ListingType.PRODUCT, null, ProductType.SALE),
    PRODUCT_PURCHASE(ListingType.PRODUCT, null, ProductType.PURCHASE),
    PRODUCT_SWAP(ListingType.PRODUCT, null, ProductType.SWAP),
    PRODUCT_GIVEAWAY(ListingType.PRODUCT, null, ProductType.GIVEAWAY),
    PRODUCT_WANTED_FREE(ListingType.PRODUCT, null, ProductType.WANTED_FREE),

    EVENT(ListingType.EVENT, null, null);

    private final ListingType listingType;
    private final ServiceType serviceType;
    private final ProductType productType;

    ListingPublicType(ListingType listingType, ServiceType serviceType, ProductType productType) {
        this.listingType = listingType;
        this.serviceType = serviceType;
        this.productType = productType;
    }

    public ListingType getListingType() {
        return listingType;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public ProductType getProductType() {
        return productType;
    }

    // Для поиска по внутреннему типу (на случай, если понадобится)
    public static ListingPublicType from(ServiceType serviceType) {
        for (var v : values()) {
            if (v.serviceType == serviceType)
                return v;
        }
        return null;
    }

    public static ListingPublicType from(ProductType productType) {
        for (var v : values()) {
            if (v.productType == productType)
                return v;
        }
        return null;
    }
}