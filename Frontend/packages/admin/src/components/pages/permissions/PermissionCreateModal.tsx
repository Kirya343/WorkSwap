import Modal from "@core/components/ui/Modal/Modal";
import { useState } from "react";
import { permissionsService } from "@core/lib/user/permissionsService";
import { IPermission } from "@core/lib/user/types";

const PermissionCreateModal = ({addPermission, onClose, isOpen}: {addPermission: (permisson: IPermission) => void, onClose: () => void, isOpen: boolean}) => {

    const [name, setName] = useState("");

    async function createPerm() {
        if (!name.trim()) return;
        const newPermId = await permissionsService.createPermission(name);
        if (newPermId) {
            addPermission({id: newPermId, name: name, comment: ""})
            setName("");
            onClose();
        }
    }

    return (
        <>
            <Modal isOpen={isOpen} onClose={onClose} title="Создать Разрешение">
                <div className="form-group">
                    <label htmlFor="permissionName">Имя:</label>
                    <span>(на англиском, заглавными буквами)</span>
                    <input
                        type="text"
                        id="permissionName"
                        name="permissionName"
                        required
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                </div>

                <div className="form-actions" onClick={() => createPerm()}>
                    <button className="btn btn-outline-primary">Сохранить</button>
                </div>
            </Modal>
        </>
    );
};

export default PermissionCreateModal;