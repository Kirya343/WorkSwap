import { IRole } from "@core/lib/user/types";
import styles from "./RolesList.module.scss"
import RoleCreateModal from "../RoleCreateModal";
import { useCallback, useEffect, useState } from "react";
import { permissionsService } from "@core/lib/user/permissionsService";
import PlusIcon from "@core/components/common/icons/PlusIcon";
import UserIcon from "@core/components/common/icons/UserIcon";
import Loader from "@core/components/common/Loader/Loader";

interface RolesListProps {
    selectRole: (role: IRole) => void;
    saving: boolean;
    roleListVisible: boolean;
    selectedRole: IRole | null;
}

const RolesList = ({
    selectRole, 
    selectedRole, 
    saving,
    roleListVisible
}: RolesListProps) => {

    const [roles, setRoles] = useState<IRole[] | null>(null);

    const [modalOpen, setModalOpen] = useState(false);

    const addRole = useCallback((role: IRole) => {
        setRoles((prev) => {
            if (!prev) return prev;
            return ([...prev, role]);
        })
    }, [setRoles]);

    useEffect(() => {
        async function loadRoles() {
            const data = await permissionsService.getAllRoles();
            setRoles(data);
        }

        loadRoles();
    }, [])

    return (
        <div className={`${styles.rolesList} ${roleListVisible ? styles.show : ""}`}>
            <Loader loadingActive={!roles}>
                {roles?.map((role) => (
                    <button 
                        className={`${styles.role} ${role.id == selectedRole?.id ? styles.active : ""}`} 
                        key={role.id}
                        onClick={() => selectRole(role)}
                    >
                        <UserIcon className={styles.icon}/><span>{role.name}</span>
                        {(selectedRole?.id == role.id && saving) &&
                            <div>
                                <i className="fa-solid fa-loader fa-spin"></i>
                            </div>
                        }
                    </button>
                ))}

                <button onClick={() => setModalOpen(true)} className={styles.role}>
                    <PlusIcon className={styles.icon}/><span>Создать новую</span>
                </button>
            </Loader>
            <RoleCreateModal addRole={addRole} onClose={() => setModalOpen(false)} isOpen={modalOpen}/>
        </div>
    );
};

export default RolesList;