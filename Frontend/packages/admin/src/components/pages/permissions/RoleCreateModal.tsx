import Modal from "@core/components/ui/Modal/Modal";
import PlusIcon from "@core/components/common/icons/PlusIcon";
import { useState } from "react";
import { IRole } from "@core/lib/user/types";
import { permissionsService } from "@core/lib/user/permissionsService";

const RoleCreateModal = ({addRole, onClose, isOpen}: {addRole: (role: IRole) => void, onClose: () => void, isOpen: boolean}) => {

    const [name, setName] = useState("");

    async function createRole() {
        if (!name.trim()) return;
        const newRoleId = await permissionsService.createRole(name);
        console.log(newRoleId)
        if (newRoleId) {
            addRole({id: newRoleId, name: name, level: 0})
            setName("");
            onClose();
        }
    }

    return (
        <>
            <Modal isOpen={isOpen} onClose={onClose} title="Создать роль">
                <div className="form-group">
                    <label htmlFor="roleName">Имя:</label>
                    <span>(на англиском, заглавными буквами)</span>
                    <input
                        type="text"
                        id="roleName"
                        name="roleName"
                        required
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                </div>

                <div className="form-actions" onClick={() => createRole()}>
                    <button className="btn btn-outline-primary">Сохранить</button>
                </div>
            </Modal>
        </>
    );
};

export default RoleCreateModal;