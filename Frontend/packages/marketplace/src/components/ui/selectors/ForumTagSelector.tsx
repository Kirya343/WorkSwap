"use client"

import { useEffect, useState } from "react";
import { ForumTag } from '@core/lib/forum/types'
import { useI18n } from "@core/lib/common/contexts/I18nContext";

const ForumTagSelector = ({
    tags, 
    currentTag, 
    onChange
}: {
    tags: ForumTag[], 
    currentTag: ForumTag | null, 
    onChange: (tag: ForumTag | null) => void
}) => {

    const { dict } = useI18n();

    const [selectedPath, setSelectedPath] = useState<number[]>([]);

    useEffect(() => {
        if (currentTag?.id) {
            const path = findPathToCategory(tags, currentTag.id);
            setSelectedPath(path);
        }
        
        function findPathToCategory(categories: ForumTag[], categoryId: number): number[] {
            const category = categories.find(c => c.id === categoryId);
            if (!category) return [];

            // если есть parentId → рекурсивно ищем путь к родителю
            if (category.parentId) {
                return [...findPathToCategory(categories, category.parentId), category.id];
            } else {
                return [category.id];
            }
        }
    }, [currentTag, tags])

    const getChildren = (parentId: number | null) =>
        tags.filter((c) => c.parentId === parentId) ?? [];

    const handleSelect = (level: number, categoryId: number) => {
        const newPath = [...selectedPath.slice(0, level), categoryId];
        setSelectedPath(newPath);

        if (onChange) {
            onChange(tags.find(c => c.id === categoryId) ?? null); // пробрасываем выбранный id и путь
        }
    };
    
    const renderSelectors = () => {
        const selectors = [];
        let parentId = null;

        for (let level = 0; ; level++) {
            const children = getChildren(parentId);
            if (children.length === 0) break;

            const selected = selectedPath[level] || "";
            selectors.push(
                <select
                    key={level}
                    id="categorySelector"
                    value={selected}
                    onChange={(e) => handleSelect(level, Number(e.target.value))}
                >
                    <option value="" disabled>
                        {dict.common.placeholders.category}
                    </option>
                    {children.map((c) => (
                        <option key={c.id} value={c.id}>
                            {dict.forumtags[c.name]}
                        </option>
                    ))}
                </select>
            );

            if (!selected) break; // пока не выбрали — дальше не идём
            parentId = selected;
        }

        return selectors;
    };

    return (
        <div id="categories" className="form-group">
            {renderSelectors()}
        </div>
    );
};

export default ForumTagSelector;