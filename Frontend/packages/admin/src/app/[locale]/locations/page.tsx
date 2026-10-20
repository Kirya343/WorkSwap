"use client"

import Card from "@/components/ui/Card/Card";
import styles from "./LocationsPage.module.scss"
import Loader from "@core/components/common/Loader/Loader"
import Breadcrumbs from "@core/components/ui/Breadcrumbs/Breadcrumbs";
import { useEffect, useState } from "react";
import { ILocation } from "@core/lib/location/types"
import { locationService } from "@core/lib/location/locationService"
import LocationsTable from "@/components/pages/locations/LocationsTable/LocationsTable";
import LocationCreateModal from "@/components/pages/locations/LocationCreateModal/LocationCreateModal";

const LocationsPage = () => {

    const [locations, setLocations] = useState<ILocation[] | null>(null)

    const [createModal, setCreateModal] = useState<{
        open: boolean;
        country: ILocation | null;
    }>({
        open: false,
        country: null
    });
    
    useEffect(() => {
        async function loadLocations() {
            const data = await locationService.getLocations();
            setLocations(data)
        }

        loadLocations()
    }, [])

    const onEditLocation = (id: number) => {
        console.log("TODO: edit location", id);
    };

    const addLocation = (
        location: ILocation
    ) => {
        setLocations(prev => {
            if (!prev) return prev;

            return [
                ...prev,
                location
            ];
        });
    };

    const removeLocation = async (
        locationId: number
    ) => {

        const res = await locationService.deleteLocation(locationId);

        if (!res.ok) throw new Error("Ошибка удаления категории");

        setLocations(prev => {
            if (!prev) return prev;

            return prev.filter(
                location => location.id !== locationId
            );
        });

    };

    return (
        <>
            <Breadcrumbs
                crumbs={[
                    { href: "/dashboard", title: "Панель управления" },
                    { href: "#", title: "Локации" },
                ]}
            />

            <Card>
                <Loader loadingActive={!locations}>
                    <div className={styles.page}>
                        <LocationsTable
                            locations={locations || []}
                            onDeleteLocation={removeLocation}
                            onEditLocation={onEditLocation}
                            onCreateLocation={(id: number | null) => 
                                setCreateModal({ 
                                    open: true, 
                                    country: id ? locations?.find(loc => loc.id === id && loc.city == false) || null : null
                                })}
                        />
                    </div>
                </Loader>
            </Card>

            <LocationCreateModal
                country={createModal.country}
                addLocation={addLocation}
                isOpen={createModal.open}
                onClose={() => setCreateModal({ open: false, country: null })}
            />
        </>
    );
};

export default LocationsPage;