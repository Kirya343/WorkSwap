import { Dispatch, SetStateAction, useCallback, useEffect, useState } from "react";
import PermissionItem from "../PermissionItem/PermissionItem";
import { IPermission, IRole } from "@core/lib/user/types";
import { permissionsService } from "@core/lib/user/permissionsService";
import styles from "./PermissionsList.module.scss"
import PermissionCreateModal from "../PermissionCreateModal";
import PlusIcon from "@core/components/common/icons/PlusIcon";
import Loader from "@core/components/common/Loader/Loader";

interface PermissionsListProps {
    selectedRole: IRole | null;
    setSaving: Dispatch<SetStateAction<boolean>>
}

const PermissionsList = ({selectedRole, setSaving}: PermissionsListProps) => {

    const [checkedPermissions, setCheckedPermissions] = useState<IPermission[] | null>(null);
    const [permissions, setPermissions] = useState<IPermission[] | null>(null);
    const [isModalOpen, setModalOpen] = useState(false);

    useEffect(() => {
        async function loadPermsByRole() {
            if (!selectedRole?.id) return;
            const data = await permissionsService.getRolePermissions(selectedRole?.id);
            setCheckedPermissions(data);
        }

        if (selectedRole) {
            loadPermsByRole()
        }
    }, [selectedRole])

    useEffect(() => {
        async function loadPerms() {
            const data = await permissionsService.getAllPermissions();
            setPermissions(data);
        }

        loadPerms();
    }, [])

    const addPermission = useCallback((perm: IPermission) => {
        setPermissions(prev => {
            if (!prev) return prev;
            return ([perm, ...prev]);
        })
    }, [setPermissions])

    return (
        <div className={styles.list}>
            <PermissionItem onClick={() => setModalOpen(true)} createNew/>
            <PermissionCreateModal addPermission={addPermission} onClose={() => setModalOpen(false)} isOpen={isModalOpen}/>
            <Loader loadingActive={!permissions}>
                {permissions?.map((perm) => (
                    <PermissionItem 
                        key={perm.id}
                        permission={perm} 
                        setSaving={setSaving} 
                        selectedRole={selectedRole}
                        checkedPermissions={checkedPermissions}
                        setCheckedPermissions={setCheckedPermissions}
                    />
                ))}
            </Loader>
        </div>
    );
};

export default PermissionsList;