"use client"

import { useEffect, useState } from "react";
import { ILocation } from '@core/lib/location/types'
import { locationService } from '@core/lib/location/locationService'
import { useI18n } from "@core/lib/common/contexts/I18nContext";

const LocationSelector = ({ 
    locationId, 
    onChange 
}: {
    locationId: number | null, 
    onChange: (value: number, newPath: number[]) => void}
) => {
    
    const { dict } = useI18n();

    const [locations, setLocations] = useState<ILocation[] | null>(null);
    const [selectedPath, setSelectedPath] = useState<number[]>([]);

    useEffect(() => {
        async function loadLocations() {
            const data = await locationService.getLocations();

            const locs = data || [];
            setLocations(locs);

            // если уже есть categoryId → восстановим путь
            if (locationId) {
                const path = findPathToLocation(locs, locationId);
                setSelectedPath(path);
            }
        }

        function findPathToLocation(locations: ILocation[], locationId: number): number[] {
            const loc = locations.find(l => l.id === locationId);
            if (!loc) return [];

            // если есть parentId → рекурсивно ищем путь к родителю
            if (loc.countryId) {
                // сначала путь до страны, потом сам город
                return [...findPathToLocation(locations, loc.countryId), loc.id];
            } else {
                // это страна
                return [loc.id];
            }
        }

        loadLocations();
    }, [locationId]);

    // нормализуем сравнение
    const getChildren = (countryId: number | null) =>
        locations?.filter((c) => c.countryId === countryId) ?? [];

    const handleSelect = (level: number, value: number) => {
        const newPath = [...selectedPath.slice(0, level), value];
        setSelectedPath(newPath);

        if (onChange) {
            onChange(value, newPath); // пробрасываем выбранный id и путь
        }
    };

    const renderSelectors = () => {
        const selectors = [];
        let countryId = null;

        for (let level = 0; ; level++) {
            const children = getChildren(countryId);
            if (children.length === 0) break;

            const selected = selectedPath[level] || "";
            selectors.push(
                <select
                    key={level}
                    id={`locationSelector${level}`}
                    value={selected}
                    onChange={(e) => handleSelect(level, Number(e.target.value))}
                >
                    <option value="" disabled>
                        {dict.common.placeholders.location}
                    </option>
                    {children.map((l) => (
                        <option key={l.id} value={l.id}>
                            {l.name}
                        </option>
                    ))}
                </select>
            );

            if (!selected) break; // пока не выбрали — дальше не идём
            countryId = selected;
        }

        return selectors;
    };

    return <div className="form-group">{renderSelectors()}</div>;
};

export default LocationSelector;