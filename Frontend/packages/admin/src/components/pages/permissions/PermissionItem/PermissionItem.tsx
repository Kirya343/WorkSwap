import { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';
import ActionMenu, { IKebabAction } from '@core/components/ui/ActionMenu/ActionMenu';
import { IPermission, IPermissionUpdate, IRole } from '@core/lib/user/types';
import { useNotification } from '@core/lib/notification/NotificationContext';
import { permissionsService } from '@core/lib/user/permissionsService';
import SliderCheckbox from "@core/components/common/checkbox/SliderCheckbox/SliderCheckbox"
import styles from "./PermissionItem.module.scss"
import PlusIcon from '@core/components/common/icons/PlusIcon';

type PermissionItemProps =
    | {
        permission: IPermission | null;
        setSaving: Dispatch<SetStateAction<boolean>>;
        selectedRole: IRole | null;
        checkedPermissions: IPermission[] | null;
        setCheckedPermissions: Dispatch<SetStateAction<IPermission[] | null>>;
        createNew?: never;
        onClick?: never;
    }
    | {
        createNew: true;
        onClick: () => void;
        permission?: never;
        setSaving?: never;
        selectedRole?: never;
        checkedPermissions?: never;
        setCheckedPermissions?: never;
    }

const PermissionItem = ({
    permission,
    setSaving,
    selectedRole,
    checkedPermissions,
    setCheckedPermissions,
    onClick,
    createNew
}: PermissionItemProps) => {

    const { notificate } = useNotification()

    const [editMode, setEditMode] = useState(false);
    const [permissionName, setPermissionName] = useState(permission?.name);
    const [permissionComment, setPermissionComment] = useState(permission?.comment);

    const inputRef = useRef<HTMLInputElement | null>(null);

    useEffect(() => {
        if (editMode && inputRef.current) {
            inputRef.current.focus();
        }
    }, [editMode]);

    async function savePermissions(permId: number, enabled: boolean) {
        if (!selectedRole?.id) return
        const update: IPermissionUpdate = {
            permissionId: permId,
            enabled
        }
        const res = await permissionsService.updateRolePermissions(selectedRole?.id, update);
        if (res.ok) setSaving(false);
    }

    function changeRolePerms(perm: IPermission | null, enabled: boolean) {
        if (!perm || !setSaving) return
        setSaving(true);
        savePermissions(perm.id, enabled);
        setCheckedPermissions(prev => {
            // если уже есть — удалить
            if (!prev) return prev;

            if (prev.some(p => p.id === perm.id)) {
                return prev.filter(p => p.id !== perm.id);
            } else {
                return [...prev, perm];
            }
        });
    }

    async function savePermission(id: number | null) {
        if (!id) return
        const update: IPermission = {id: 0, name: "", comment: ""};
        if (permissionName) update.name = permissionName;
        update.comment = permissionComment || "";
        setEditMode(false);
        const res = await permissionsService.updatePermission(id, update);
        if (!res.ok) {
            notificate("Разрешение успешно создано", "error");
        }
    }

    const handleEnterDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') { 
            savePermission(permission?.id ?? null);
        }
    };

    const actions: IKebabAction[] = [];
    actions.push({
        title: "Изменить",
        func: () => {
            setEditMode(!editMode)
        },
        icon: "pen"
    })

    return !createNew ? (
        <div className={styles.permission} key={permission?.id}>
            {editMode ? (
                <>
                    <input
                        ref={inputRef}
                        id="permissionName"
                        className={styles.inputName}
                        type="text"
                        required
                        value={permissionName}
                        onChange={(e) => setPermissionName(e.target.value)}
                        onKeyDown={handleEnterDown}
                    />
                    <input
                        id="permissionComment"
                        type="text"
                        className={styles.inputComment}
                        required
                        value={permissionComment}
                        placeholder='Введите комментарий к разрешению'
                        onChange={(e) => setPermissionComment(e.target.value)}
                        onKeyDown={handleEnterDown}
                    />
                </>
            ) : (
                <>
                    <span>{permissionName}</span>
                    <div className={styles.comment}>{permissionComment}</div>
                </>
            )}
            {(selectedRole && !editMode) && (
                <SliderCheckbox
                    id={`permission-${permission?.id}`}
                    checked={checkedPermissions?.some(p => p.id === permission?.id) || false}
                    onChange={(e) => changeRolePerms(permission, e.target.checked)}
                    className={styles.toggler} 
                />
            )}
            <ActionMenu actions={actions} className={styles.kebab} />
        </div>
    ) : (
        <div className={styles.permission} onClick={onClick} style={{cursor: "pointer", justifyContent: "left", gap: "0.5rem"}}>
            <PlusIcon/><span>Создать новое разрешение</span>
        </div>
    );
};

export default PermissionItem;