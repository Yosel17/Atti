package yosel.dev.atti.screens.product_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.utils.Constants

data class ProductFormInputsState(
    val commercialName: String = "",
    val brand: String = "",
    val selectedCategory: AppCatalogModel? = null,
    val selectedUnitType: AppCatalogModel? = null,
    val purchasePrice: String = "",
    val salePrice: String = "",
    val stock: String = "",
    val minStock: String = "",
    val supplierId: SupplierModel? = null,
    val touchedFields: Set<Int> = emptySet()
){
    val isValid: Boolean
        get() = commercialName.isNotBlank() &&
                brand.isNotBlank() &&
                selectedCategory != null &&
                selectedUnitType != null &&
                purchasePrice.isBlank() &&
                salePrice.isBlank() &&
                supplierId != null &&
                stock.isBlank() &&
                minStock.isBlank()

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when(field){
            Constants.PRODUCT_COMMERCIAL_NAME_FIELD -> commercialName.isBlank()
            Constants.PRODUCT_BRAND_FIELD -> brand.isBlank()
            Constants.PRODUCT_CATEGORY_FIELD -> selectedCategory == null
            Constants.PRODUCT_UNIT_TYPE_FIELD -> selectedUnitType == null
            Constants.PRODUCT_PURCHASE_PRICE_FIELD -> purchasePrice.isBlank()
            Constants.PRODUCT_SALE_PRICE_FIELD -> salePrice.isBlank()
            Constants.PRODUCT_STOCK_FIELD -> stock.isBlank()
            Constants.PRODUCT_MIN_STOCK_FIELD -> minStock.isBlank()
            Constants.PRODUCT_SUPPLIER_FIELD -> supplierId == null
            else -> false
        }
    }

    fun hasChangesFrom(initial: ProductFormInputsState): Boolean {
        return commercialName != initial.commercialName ||
                brand != initial.brand ||
                selectedCategory?.id != initial.selectedCategory?.id ||
                selectedUnitType?.id != initial.selectedUnitType?.id ||
                purchasePrice != initial.purchasePrice ||
                salePrice != initial.salePrice ||
                stock != initial.stock ||
                minStock != initial.minStock ||
                supplierId?.id != initial.supplierId?.id
    }

}
