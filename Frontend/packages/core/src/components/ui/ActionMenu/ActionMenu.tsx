"use client"

import * as DropdownMenu from "@radix-ui/react-dropdown-menu";
import styles from "./ActionMenu.module.scss";
import EllipsisVerticalIcon from "@core/components/common/icons/EllipsisVerticalIcon";
import clsx from "clsx";

export interface IKebabAction {
    title: string,
    func: () => void,
    icon?: string,
    access?: boolean
};

interface ActionMenuProps {
    actions: IKebabAction[];
    className?: string;
}

const ActionMenu = ({ actions, className }: ActionMenuProps) => {
    const filtered = actions.filter((action) => action.access ?? true);

    if (filtered.length === 0) {
        return null;
    }

    return (
        <DropdownMenu.Root>
            <DropdownMenu.Trigger asChild>
                <button
                    type="button"
                    className={clsx(styles.button, className, "hover")}
                    aria-label="Действия"
                >
                    <EllipsisVerticalIcon className={styles.icon} />
                </button>
            </DropdownMenu.Trigger>

            <DropdownMenu.Portal>
                <DropdownMenu.Content
                    className={styles.list}
                    sideOffset={4}
                    align="start"
                >
                    {filtered.map((action) => (
                        <DropdownMenu.Item
                            key={action.title}
                            className={`${styles.item} hover`}
                            onSelect={action.func}
                        >
                            {action.icon && (
                                <div className={styles.itemIcon}>
                                    <i
                                        className={`fa-regular fa-${action.icon} fa-lg`}
                                    />
                                </div>
                            )}

                            <span>{action.title}</span>
                        </DropdownMenu.Item>
                    ))}
                </DropdownMenu.Content>
            </DropdownMenu.Portal>
        </DropdownMenu.Root>
    );
};

export default ActionMenu;