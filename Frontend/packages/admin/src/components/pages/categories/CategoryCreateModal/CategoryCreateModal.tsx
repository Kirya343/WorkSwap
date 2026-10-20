import SliderCheckbox from "@core/components/common/checkbox/SliderCheckbox/SliderCheckbox";
import Modal from "@core/components/ui/Modal/Modal"
import { categoryService } from "@core/lib/category/services";
import { ICategory } from "@core/lib/category/types";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { ListingTypeValue } from "@core/lib/listing/constants/listingTypes";
import { useState } from "react"

interface CategoryCreateModalProps {
    listingType: ListingTypeValue;
    parentCategory: ICategory | null;
    addCategory: (listingType: ListingTypeValue, category: ICategory) => void
    isOpen: boolean
    onClose: () => void
}

const CategoryCreateModal = ({listingType, parentCategory, addCategory, isOpen, onClose}: CategoryCreateModalProps) => {
    
    const { dict } = useI18n();

    const [leaf, setLeaf] = useState<boolean>(false);
    const [categoryName, setCategoryName] = useState("");

    const createCategory = async () => {
        const newCategory: ICategory = {
            id: 0,
            name: categoryName,
            parentId: parentCategory?.id || null,
            leaf
        }

        const newCategoryId = await categoryService.createCategory(newCategory, listingType);

        if (!newCategoryId) throw new Error("Ошибка создания категории");

        addCategory(listingType, {...newCategory, id: Number(newCategoryId)})

        onClose()
    } 

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={"Создать категорию"}>
            <div className="form-group">
                <br/>
                <span>Тип категории: {listingType}</span>
                <br/>
                {parentCategory ? (
                    <span>Родительская категория: {dict.categories.category[listingType][parentCategory.name]}</span>
                ) : (
                    <span>Вы создаёте корневую категорию</span>
                )}
                <label htmlFor="categoryName">Имя:</label>
                <input
                    type="text"
                    id="categoryName"
                    name="categoryName"
                    required
                    value={categoryName}
                    onChange={(e) => setCategoryName(e.target.value)}
                />
                <br/>
                <span>Конечная категория?</span>
                <SliderCheckbox 
                    checked={leaf} 
                    onChange={(e) => setLeaf(e.target.checked)} 
                    id="isCategoryLeaf" 
                />
            </div>

            <div className="form-actions" onClick={() => createCategory()}>
                <button className="btn btn-outline-primary">Сохранить</button>
            </div>
        </Modal>
    )
}

export default CategoryCreateModal;