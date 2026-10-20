import { useCallback, useEffect, useState } from "react";
import ListingSetting from "../ListingSetting/ListingSetting";
import { IEventData, IFullListing } from "@core/lib/listing/types";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { eventService } from "@core/lib/listing/eventService"
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { UpdateListing } from "../ListingSettingsMenu";

const recurrencePatterns = [
    "NONE",
    "DAILY",
    "WEEKLY",
    "MONTHLY",
    "YEARLY"
];

const eventStatuses = [
    "RECRUITING",
    "CLOSED",
    "FINISHED",
    "ARCHIVED"
];

const EventSettings = ({
    updateListingSettings,
    listing
}: {
    updateListingSettings: UpdateListing
    listing: IFullListing
}) => {

    const { dict } = useI18n();
    const {notificate} = useNotification();

    const [event, setEvent] = useState<IEventData | null>(null)

    const [eventDate, setEventDate] = useState<string | null>(null)
    const [recurrence, setRecurrence] = useState('DAILY')
    const [isRecurring, setRecurring] = useState(false);
    const [maxParticipants, setMaxParticipants] = useState<number>(0);
    const [minParticipants, setMinParticipants] = useState<number>(0);
    const [eventStatus, setEventStatus] = useState('RECRUITING');
    const [registrationCloseTime, setRegistrationCloseTime] = useState<string | null>(null)
    const [isPublic, setPublic] = useState(true);

    const [accessToken, setAccessToken] = useState("");

    const updateEvent = useCallback(async (updates: Record<string, any>) => {
            if (!listing.id || updates === undefined) return;
            try {
                eventService.modifyEvent(listing.id, updates);
            } catch (err) {
                notificate(dict.messages.notification.error.listingUpdate, "error");
                throw err;
            }
        }, [listing.id, notificate]);

    useEffect(() => {
        eventService.getEventSettings(listing.id).then(setEvent);
        /* listingService.getListingAccessToken(listing.id).then(setAccessToken) */
    }, [listing.id]);

    useEffect(() => {

        if (!event) return;
        console.log(event);
    
        setEventDate(event.eventDate);
        setRegistrationCloseTime(event.registrationCloseTime)
        setRecurrence(event.recurrencePattern);
        setRecurring(event.recurring);
        setMaxParticipants(event.maxParticipants);
        setMinParticipants(event.minParticipants);
        setEventStatus(event.eventStatus);
        setPublic(event.isPublic);

    }, [event]);

    return (
        <>
            <h2 className="two-columns-grid">{dict.common.labels.settings.event}</h2>
            <ListingSetting title={dict.common.labels.event.date}>
                <div className="form-group">
                    <input 
                        type="datetime-local"
                        value={eventDate ?? ""}
                        id="event-date" 
                        name="eventDate"
                        onChange={(e) => {
                            setEventDate(e.target.value);
                            updateEvent({ eventDate: e.target.value });
                        }}
                    />
                </div>
            </ListingSetting>

            <ListingSetting title={dict.common.labels.event.registerClosingDate}>
                <div className="form-group">
                    <input 
                        type="datetime-local"
                        value={registrationCloseTime ?? ""}
                        id="event-date" 
                        name="eventDate"
                        onChange={(e) => {
                            setRegistrationCloseTime(e.target.value);
                            updateEvent({ registrationCloseTime: e.target.value });
                        }}
                    />
                </div>
            </ListingSetting>

            <ListingSetting title={dict.common.labels.status.event.title}>
                <div className="form-group">
                    <div className="status-toggle">
                        <select
                            value={eventStatus}
                            onChange={(e) => {
                                setEventStatus(e.target.value);
                                updateEvent({ eventStatus: e.target.value });
                            }}
                        >
                            <option 
                                selected
                                disabled
                            >
                                Выберите статус события
                            </option>
                            {eventStatuses.map((es) => (
                                <option 
                                    key={es} 
                                    value={es}
                                    selected={es == eventStatus}
                                >
                                    {dict.common.labels.status.event[es]}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
            </ListingSetting>
            
            <ListingSetting title={dict.common.labels.event.minMaxParticipants}>
                <div className="form-group">
                    <div className="duo">
                        <input
                            id="minParticipants"
                            className="form-control first"
                            type="number"
                            value={minParticipants ?? 0}
                            onChange={(e) => {
                                setMinParticipants(Number(e.target.value));
                                updateEvent({ minParticipants: e.target.value });
                            }}
                            step="1"
                        />
                        <input
                            id="maxParticipants"
                            className="form-control second"
                            type="number"
                            value={maxParticipants ?? 0}
                            onChange={(e) => {
                                setMaxParticipants(Number(e.target.value));
                                updateEvent({ maxParticipants: e.target.value });
                            }}
                            step="1"
                        />
                    </div>
                </div>
            </ListingSetting>

            <ListingSetting title={dict.common.labels.event.visibility.title}>
                <div className="form-group">
                    <div className="status-toggle">
                        <label className="switch">
                            <input 
                                type="checkbox" 
                                checked={isPublic ?? true}
                                onChange={(e) => {
                                    setPublic(e.target.checked);
                                    updateEvent({ isPublic: e.target.checked });
                                }}
                                value="true"
                            />
                            <span className="slider"></span>
                        </label>
                        {isPublic ? (
                            <p>{dict.common.labels.event.visibility.public}</p>
                        ) : (
                            <p>{dict.common.labels.event.visibility.private}</p>
                        )}
                    </div>

                    {!isPublic && (
                        <>
                            <h4>Пароль к объявлению</h4>
                            <input
                                className="form-control first"
                                type="text"
                                id="accessToken"
                                value={accessToken ?? ""}
                                onChange={(e) => {
                                    setAccessToken(e.target.value);
                                    updateListingSettings("accessToken", e.target.value);
                                }}
                                step="0.01"
                                required
                            />
                            <div 
                                className="btn hover"
                                onClick={() => {
                                    navigator.clipboard.writeText(window.location.origin + `/event/${listing.id}/?token=${accessToken}`)
                                        .then(() => notificate(dict.messages.notification.success.copyListingLink, "success"))
                                        .catch(() => notificate("Ошибка", "error"));
                                }}
                            >Скопировать ссылку</div>
                        </>
                    )}
                </div>
            </ListingSetting>

            <ListingSetting title={dict.common.labels.event.recurrence.title}>
                <div className="form-group">
                    <div className="status-toggle">
                        <label className="switch">
                            <input 
                                type="checkbox" 
                                checked={isRecurring ?? false}
                                onChange={(e) => {
                                    setRecurring(e.target.checked);
                                    updateEvent({ recurring: e.target.checked });
                                }}
                                value="true"
                            />
                            <span className="slider"></span>
                        </label>
                        {isRecurring ? (
                            <p>{dict.common.labels.event.recurrence.repeat}</p>
                        ) : (
                            <p>{dict.common.labels.event.recurrence.single}</p>
                        )}
                    </div>

                    {isRecurring && (
                        <>
                            <h4>{dict.common.labels.event.recurrenceParam.title}</h4>
                            <div className="status-toggle">
                                <select
                                    value={recurrence}
                                    onChange={(e) => {
                                        setRecurrence(e.target.value);
                                        updateEvent({ recurrence: e.target.value });
                                    }}
                                >
                                    <option 
                                        selected
                                        disabled
                                    >
                                        Выберите частоту повторения
                                    </option>
                                    {recurrencePatterns.map((r) => (
                                        <option 
                                            key={r} 
                                            value={r}
                                            selected={r == recurrence}
                                        >
                                            {dict.common.labels.event.recurrenceParam[r]}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </>
                    )}
                </div>
            </ListingSetting>
        </>
    );
};

export default EventSettings;