import Modal from "@core/components/ui/Modal/Modal"
import { locationService } from "@core/lib/location/locationService";
import { ILocation } from "@core/lib/location/types";
import { useState } from "react"

interface LocationCreateModalProps {
    country: ILocation | null;
    addLocation: (location: ILocation) => void
    isOpen: boolean
    onClose: () => void
}

const LocationCreateModal = ({country, addLocation, isOpen, onClose}: LocationCreateModalProps) => {

    const [locationName, setLocationName] = useState("");

    const createLocation = async () => {
        const newLocation: ILocation = {
            id: 0,
            city: !!country,
            countryId: country?.id || null,
            fullName: "",
            name: locationName
        }

        const newLocationId = await locationService.createLocation(newLocation);

        if (!newLocationId) throw new Error("Ошибка создания категории");

        addLocation({...newLocation, id: Number(newLocationId)})
        setLocationName("");
        
        onClose()
    } 

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={"Создать локацию"}>
            <div className="form-group">
                {country ? (
                    <span>Страна: <strong>{country?.name}</strong>, вы добавляете город</span>
                ) : (
                    <span>Вы добавляете страну</span>
                )}
                <label htmlFor="locationName">Имя:</label>
                <input
                    type="text"
                    id="locationName"
                    name="locationName"
                    required
                    value={locationName}
                    onChange={(e) => setLocationName(e.target.value)}
                />
            </div>

            <div className="form-actions" onClick={() => createLocation()}>
                <button className="btn btn-outline-primary">Сохранить</button>
            </div>
        </Modal>
    )
}

export default LocationCreateModal;