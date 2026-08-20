package yosel.dev.atti.core.utils

import yosel.dev.atti.core.models.dto.AppCatalogDto
import yosel.dev.atti.core.models.dto.ClientDto
import yosel.dev.atti.core.models.dto.ConsultationDto
import yosel.dev.atti.core.models.dto.PatientDto
import yosel.dev.atti.core.models.dto.ProductDto
import yosel.dev.atti.core.models.dto.ServiceDto
import yosel.dev.atti.core.models.dto.ServiceSupplyDto
import yosel.dev.atti.core.models.dto.SupplierDto
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ClientWithPatientsModel
import yosel.dev.atti.core.models.model.ClientWithPatientsWithCatalogsModel
import yosel.dev.atti.core.models.model.ConsultationModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceModel
import yosel.dev.atti.core.models.model.ServiceSupplyModel
import yosel.dev.atti.core.models.model.ServiceSupplyWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.client.ClientEntity
import yosel.dev.atti.core.room.tables.client.ClientWithPatientsEntity
import yosel.dev.atti.core.room.tables.client.ClientWithPatientsWithCatalogsEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationWithDetailsEntity
import yosel.dev.atti.core.room.tables.patient.PatientEntity
import yosel.dev.atti.core.room.tables.patient.PatientWithCatalogsEntity
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.product.ProductWithDetailsEntity
import yosel.dev.atti.core.room.tables.service.ServiceEntity
import yosel.dev.atti.core.room.tables.service.ServiceWithDetailsEntity
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyEntity
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyWithDetailsEntity
import yosel.dev.atti.core.room.tables.supplier.SupplierEntity
import yosel.dev.atti.screens.add_client.ui.AddClientFormState
import yosel.dev.atti.screens.add_patient.ui.AddPatientFormState
import yosel.dev.atti.screens.detail_client.ui.EditClientFormState
import yosel.dev.atti.screens.detail_supplier.ui.EditSupplierFormState
import yosel.dev.atti.screens.product_form.ui.ProductFormInputsState
import yosel.dev.atti.screens.service_form.ui.ExpenseMode
import yosel.dev.atti.screens.service_form.ui.ServiceFormInputsState
import java.text.Normalizer
import java.util.Locale

fun ClientDto.toEntity() = ClientEntity(
    id = id.orEmpty(),
    firstName = firstName,
    lastName = lastName,
    documentId = documentId.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    email = email.orEmpty(),
    address = address.orEmpty(),
    createdAt = createdAt.orEmpty(),
    status = status
)


fun ClientEntity.toModel() = ClientModel(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    createdAt = createdAt,
    status = status
)

fun ClientModel.toEntity() = ClientEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    createdAt = createdAt,
    status = status
)

fun ClientModel.toDtoForInsert() = ClientDto(
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email.ifBlank { null },
    address = address,
    status = status
)

fun ClientModel.toDtoForUpdate() = ClientDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email.ifBlank { null },
    address = address,
    status = status
)

fun ClientDto.toModel() = ClientModel(
    id = id.orEmpty(),
    firstName = firstName,
    lastName = lastName,
    documentId = documentId.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    email = email.orEmpty(),
    address = address.orEmpty(),
    createdAt = createdAt.orEmpty(),
    status = status
)

// DTO -> Entity
fun PatientDto.toEntity() = PatientEntity(
    id = id.orEmpty(),
    clientId = clientId,
    name = name,
    speciesId = speciesId ?: 0,
    genderId = genderId ?: 0,
    breed = breed.orEmpty(),
    ageYears = ageYears ?: 0,
    ageMonths = ageMonths ?: 0,
    color = color.orEmpty(),
    isNeutered = isNeutered ?: false,
    photoUrl = photoUrl.orEmpty(),
    createdAt = createdAt.orEmpty(),
    status = status
)

// Entity -> Model
fun PatientEntity.toModel() = PatientModel(
    id = id,
    clientId = clientId,
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears,
    ageMonths = ageMonths,
    color = color,
    isNeutered = isNeutered,
    photoUrl = photoUrl,
    createdAt = createdAt,
    status = status
)

fun PatientModel.toEntity() = PatientEntity(
    id = id,
    clientId = clientId,
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears,
    ageMonths = ageMonths,
    color = color,
    isNeutered = isNeutered,
    photoUrl = photoUrl,
    createdAt = createdAt,
    status = status
)

fun PatientModel.toDtoInsert() = PatientDto(
    clientId = clientId,
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears,
    ageMonths = ageMonths,
    color = color,
    isNeutered = isNeutered,
    status = status
)

fun PatientModel.toDtoForUpdate() = PatientDto(
    id = id,
    clientId = clientId,
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears,
    ageMonths = ageMonths,
    color = color,
    isNeutered = isNeutered,
    photoUrl = photoUrl.ifBlank { null },
    createdAt = createdAt.ifBlank { null },
    status = status
)

fun PatientModel.toAddPatientFormState(client: ClientModel?) = AddPatientFormState(
    name = name,
    speciesId = speciesId,
    breed = breed,
    genderId = genderId,
    ageYears = if (ageYears > 0) ageYears.toString() else "",
    ageMonths = if (ageMonths > 0) ageMonths.toString() else "",
    color = color,
    isNeutered = isNeutered,
    selectedClient = client
)

fun AddClientFormState.toModel() = ClientModel(
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address
)

fun ClientModel.toEditFormState() = EditClientFormState(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    createdAt = createdAt
)

fun EditClientFormState.toModel(status: Int) = ClientModel(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    createdAt = createdAt,
    status = status
)

fun ClientWithPatientsEntity.toModel() = ClientWithPatientsModel(
    client = client.toModel(),
    patients = patients.map { it.toModel() }
)

fun AppCatalogDto.toEntity() = AppCatalogEntity(
    id = id ?: 0,
    catalogTypeId = catalogTypeId,
    name = name,
    description = description.orEmpty(),
    isActive = isActive ?: true,
    createdAt = createdAt.orEmpty()
)

fun AppCatalogDto.toModel() = AppCatalogModel(
    id = id ?: 0,
    catalogTypeId = catalogTypeId,
    name = name,
    description = description.orEmpty(),
    isActive = isActive ?: true,
    createdAt = createdAt.orEmpty()
)

fun AppCatalogModel.toDtoForInsert() = AppCatalogDto(
    catalogTypeId = catalogTypeId,
    name = name,
    description = description.ifBlank { null },
    isActive = isActive
)

fun AppCatalogEntity.toModel() = AppCatalogModel(
    id = id,
    catalogTypeId = catalogTypeId,
    name = name,
    description = description,
    isActive = isActive,
    createdAt = createdAt
)

fun AddPatientFormState.toInsertModel() = PatientModel(
    clientId = selectedClient?.id ?: "",
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears.toIntOrNull() ?: 0,
    ageMonths = ageMonths.toIntOrNull() ?: 0,
    color = color,
    isNeutered = isNeutered
)

fun AddPatientFormState.toUpdateModel(
    patientId: String,
    photoUrl: String = "",
    createdAt: String = "",
    status: Int = 1
) = PatientModel(
    id = patientId,
    clientId = selectedClient?.id ?: "",
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears.toIntOrNull() ?: 0,
    ageMonths = ageMonths.toIntOrNull() ?: 0,
    color = color,
    isNeutered = isNeutered,
    photoUrl = photoUrl,
    createdAt = createdAt,
    status = status
)

fun PatientWithCatalogsEntity.toModel() = PatientWithCatalogsModel(
    patient = patient.toModel(),
    species = species?.toModel() ?: AppCatalogModel(),
    gender = gender?.toModel() ?: AppCatalogModel()
)

fun ClientWithPatientsWithCatalogsEntity.toModel() = ClientWithPatientsWithCatalogsModel(
    client = client.toModel(),
    patients = patients.map { it.toModel() }
)

fun ProductDto.toEntity() = ProductEntity(
    id = id.orEmpty(),
    supplierId = supplierId,
    categoryId = categoryId ?: 0,
    unitTypeId = unitTypeId ?: 0,
    commercialName = commercialName,
    brand = brand.orEmpty(),
    purchasePrice = purchasePrice,
    salePrice = salePrice,
    stock = stock,
    minStock = minStock ?: 0,
    createdAt = createdAt.orEmpty() ,
    status = status
)

fun ProductDto.toModel() = ProductModel(
    id = id.orEmpty(),
    supplierId = supplierId.orEmpty(),
    categoryId = categoryId ?: 0,
    unitTypeId = unitTypeId ?: 0,
    commercialName = commercialName,
    brand = brand.orEmpty(),
    purchasePrice = purchasePrice,
    salePrice = salePrice,
    stock = stock,
    minStock = minStock ?: 0,
    createdAt = createdAt.orEmpty(),
    status = status,
)

fun ServiceDto.toEntity() = ServiceEntity(
    id = id.orEmpty(),
    categoryId = categoryId ?: 0,
    name = name,
    description = description.orEmpty(),
    salePrice = salePrice,
    estimatedCost = estimatedCost ?: 0.00,
    createdAt = createdAt.orEmpty(),
    status = status
)

fun ServiceDto.toModel() = ServiceModel(
    id = id.orEmpty(),
    categoryId = categoryId ?: 0,
    name = name,
    description = description.orEmpty(),
    salePrice = salePrice,
    estimatedCost = estimatedCost ?: 0.00,
    createdAt = createdAt.orEmpty(),
    status = status
)

fun SupplierDto.toEntity() = SupplierEntity(
    id = id.orEmpty(),
    name = name,
    taxId = taxId.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    address = address.orEmpty(),
    createdAt = createdAt.orEmpty(),
    status = status
)

fun SupplierDto.toModel() = SupplierModel(
    id = id.orEmpty(),
    name = name,
    taxId = taxId.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    address = address.orEmpty(),
    createdAt = createdAt.orEmpty(),
    status = status
)

fun ProductEntity.toModel() = ProductModel(
    id = id,
    supplierId = supplierId,
    categoryId = categoryId,
    unitTypeId = unitTypeId,
    commercialName = commercialName,
    brand = brand,
    purchasePrice = purchasePrice,
    salePrice = salePrice,
    stock = stock,
    minStock = minStock,
    createdAt = createdAt,
    status = status
)

fun ProductModel.toDtoForInsert() = ProductDto(
    supplierId = supplierId,
    categoryId = categoryId,
    unitTypeId = unitTypeId,
    commercialName = commercialName,
    brand = brand,
    purchasePrice = purchasePrice,
    salePrice = salePrice,
    stock = stock,
    minStock = minStock,
    status = status
)

fun ProductModel.toDtoForUpdate() = ProductDto(
    id = id,
    supplierId = supplierId,
    categoryId = categoryId,
    unitTypeId = unitTypeId,
    commercialName = commercialName,
    brand = brand,
    purchasePrice = purchasePrice,
    salePrice = salePrice,
    stock = stock,
    minStock = minStock,
    status = status
)

fun ProductModel.toEntity() = ProductEntity(
    id = id,
    supplierId = supplierId,
    categoryId = categoryId,
    unitTypeId = unitTypeId,
    commercialName = commercialName,
    brand = brand,
    purchasePrice = purchasePrice,
    salePrice = salePrice,
    stock = stock,
    minStock = minStock,
    createdAt = createdAt,
    status = status
)

fun ProductModel.toProductFormInputsState(
    category: AppCatalogModel?,
    unitType: AppCatalogModel?,
    supplier: SupplierModel?
) = ProductFormInputsState(
    commercialName = commercialName,
    brand = brand,
    selectedCategory = category,
    selectedUnitType = unitType,
    purchasePrice = if (purchasePrice > 0.0) purchasePrice.toString() else "",
    salePrice = if (salePrice > 0.0) salePrice.toString() else "",
    stock = stock.toString(),
    minStock = minStock.toString(),
    selectedSupplier = supplier
)

fun ServiceModel.toDtoForInsert() = ServiceDto(
    categoryId = categoryId,
    name = name,
    description = description,
    salePrice = salePrice,
    estimatedCost = estimatedCost,
    status = status
)

fun ServiceModel.toDtoForUpdate() = ServiceDto(
    id = id,
    categoryId = categoryId,
    name = name,
    description = description,
    salePrice = salePrice,
    estimatedCost = estimatedCost,
    status = status
)

fun ServiceModel.toEntity() = ServiceEntity(
    id = id,
    categoryId = categoryId,
    name = name,
    description = description,
    salePrice = salePrice,
    estimatedCost = estimatedCost,
    createdAt = createdAt,
    status = status
)

fun ServiceEntity.toModel() = ServiceModel(
    id = id,
    categoryId = categoryId,
    name = name,
    description = description,
    salePrice = salePrice,
    estimatedCost = estimatedCost,
    createdAt = createdAt,
    status = status
)

fun SupplierEntity.toModel() = SupplierModel(
    id = id,
    name = name,
    taxId = taxId,
    phoneNumber = phoneNumber,
    address = address,
    createdAt = createdAt,
    status = status
)

fun SupplierModel.toDtoForInsert() = SupplierDto(
    name = name,
    taxId = taxId,
    phoneNumber = phoneNumber,
    address = address,
    status = status
)

fun SupplierModel.toDtoForUpdate() = SupplierDto(
    id = id,
    name = name,
    taxId = taxId,
    phoneNumber = phoneNumber,
    address = address,
    status = status
)

fun SupplierModel.toEntity() = SupplierEntity(
    id = id,
    name = name,
    taxId = taxId,
    phoneNumber = phoneNumber,
    address = address,
    createdAt = createdAt,
    status = status
)

fun SupplierModel.toEditFormState() = EditSupplierFormState(
    id = id,
    name = name,
    taxId = taxId,
    phoneNumber = phoneNumber,
    address = address,
    createdAt = createdAt
)

fun ProductWithDetailsEntity.toModel() = ProductWithDetailsModel(
    product = product.toModel(),
    supplier = supplier?.toModel() ?: SupplierModel(),
    category = category?.toModel() ?: AppCatalogModel(),
    unitType = unitType?.toModel() ?: AppCatalogModel()
)

fun ServiceWithDetailsEntity.toModel() = ServiceWithDetailsModel(
    service = service.toModel(),
    category = category?.toModel() ?: AppCatalogModel(),
    supplies = supplies.map { it.toModel() }
)

fun ServiceWithDetailsModel.toServiceFormInputsState(category: AppCatalogModel?): ServiceFormInputsState {
    val hasSupplies = supplies.isNotEmpty()
    return ServiceFormInputsState(
        name = service.name,
        selectedCategory = category ?: this.category.takeIf { it.id != 0 },
        salePrice = if (service.salePrice > 0.0) service.salePrice.toString() else "",
        expenseMode = if (hasSupplies) ExpenseMode.LINK_PRODUCTS else ExpenseMode.MANUAL,
        estimatedCost = if (!hasSupplies && service.estimatedCost > 0.0) service.estimatedCost.toString() else "",
        selectedProducts = if (hasSupplies) {
            supplies.map { item ->
                yosel.dev.atti.screens.service_form.ui.SelectedProductSupply(
                    product = item.product.product,
                    quantity = item.supply.quantityRequired
                )
            }
        } else {
            emptyList()
        }
    )
}

// --- SERVICE SUPPLIES ---

fun ServiceSupplyDto.toEntity() = ServiceSupplyEntity(
    id = id ?: 0,
    serviceId = serviceId,
    productId = productId,
    quantityRequired = quantityRequired,
    createdAt = createdAt.orEmpty(),
    status = status
)

fun ServiceSupplyEntity.toModel() = ServiceSupplyModel(
    id = id,
    serviceId = serviceId,
    productId = productId,
    quantityRequired = quantityRequired,
    createdAt = createdAt,
    status = status
)

fun ServiceSupplyModel.toEntity() = ServiceSupplyEntity(
    id = id,
    serviceId = serviceId,
    productId = productId,
    quantityRequired = quantityRequired,
    createdAt = createdAt,
    status = status
)

fun ServiceSupplyDto.toModel() = ServiceSupplyModel(
    id = id ?: 0,
    serviceId = serviceId,
    productId = productId,
    quantityRequired = quantityRequired,
    createdAt = createdAt.orEmpty(),
    status = status
)

fun ServiceSupplyModel.toDtoForInsert() = ServiceSupplyDto(
    serviceId = serviceId,
    productId = productId,
    quantityRequired = quantityRequired,
    status = status
)

fun ServiceSupplyModel.toDtoForUpdate() = ServiceSupplyDto(
    id = id,
    serviceId = serviceId,
    productId = productId,
    quantityRequired = quantityRequired,
    status = status
)

fun ServiceSupplyWithDetailsEntity.toModel() = ServiceSupplyWithDetailsModel(
    supply = supply.toModel(),
    product = product?.toModel() ?: ProductWithDetailsModel()
)

fun EditSupplierFormState.toModel(status: Int) = SupplierModel(
    id = id,
    name = name,
    taxId = taxId,
    phoneNumber = phoneNumber,
    address = address,
    createdAt = createdAt,
    status = status
)

fun ProductFormInputsState.toInsertModel() = ProductModel(
    supplierId = selectedSupplier?.id ?: "",
    categoryId = selectedCategory?.id ?: 0,
    unitTypeId = selectedUnitType?.id ?: 0,
    commercialName = commercialName,
    brand = brand,
    purchasePrice = purchasePrice.parseToDouble(),
    salePrice = salePrice.parseToDouble(),
    stock = stock.parseToInt(),
    minStock = minStock.parseToInt(),
    status = Constants.ACTIVE_STATUS
)

fun ProductFormInputsState.toUpdateModel(
    productId: String,
    createdAt: String = "",
    status: Int = Constants.ACTIVE_STATUS
) = ProductModel(
    id = productId,
    supplierId = selectedSupplier?.id,
    categoryId = selectedCategory?.id ?: 0,
    unitTypeId = selectedUnitType?.id ?: 0,
    commercialName = commercialName.trim(),
    brand = brand.trim(),
    purchasePrice = purchasePrice.parseToDouble(),
    salePrice = salePrice.parseToDouble(),
    stock = stock.parseToInt(),
    minStock = minStock.parseToInt(),
    createdAt = createdAt,
    status = status
)

fun ServiceFormInputsState.toInsertModel(): ServiceModel {
    val calculatedEstimatedCost = when (expenseMode) {
        ExpenseMode.MANUAL -> estimatedCost.parseToDouble()
        ExpenseMode.LINK_PRODUCTS -> selectedProducts.sumOf { it.product.purchasePrice * it.quantity }
    }
    return ServiceModel(
        categoryId = selectedCategory?.id ?: 0,
        name = name.trim(),
        salePrice = salePrice.parseToDouble(),
        estimatedCost = calculatedEstimatedCost,
        status = Constants.ACTIVE_STATUS
    )
}

fun ServiceFormInputsState.toServiceSupplyModels(serviceId: String): List<ServiceSupplyModel> {
    if (expenseMode != ExpenseMode.LINK_PRODUCTS) return emptyList()
    return selectedProducts.map { supply ->
        ServiceSupplyModel(
            serviceId = serviceId,
            productId = supply.product.id,
            quantityRequired = supply.quantity,
            status = Constants.ACTIVE_STATUS
        )
    }
}

fun ServiceFormInputsState.toUpdateModel(
    serviceId: String,
    createdAt: String = "",
    status: Int = Constants.ACTIVE_STATUS
): ServiceModel {
    val calculatedEstimatedCost = when (expenseMode) {
        ExpenseMode.MANUAL -> estimatedCost.parseToDouble()
        ExpenseMode.LINK_PRODUCTS -> selectedProducts.sumOf { it.product.purchasePrice * it.quantity }
    }
    return ServiceModel(
        id = serviceId,
        categoryId = selectedCategory?.id ?: 0,
        name = name.trim(),
        salePrice = salePrice.parseToDouble(),
        estimatedCost = calculatedEstimatedCost,
        createdAt = createdAt,
        status = status
    )
}

// --- CONSULTATIONS ---
fun ConsultationDto.toEntity() = ConsultationEntity(
    id = id.orEmpty(),
    patientId = patientId,
    consultationTypeId = consultationTypeId ?: 0,
    startedAt = startedAt.orEmpty(),
    completedAt = completedAt.orEmpty(),
    createdAt = createdAt.orEmpty(),
    status = status
)

fun ConsultationEntity.toModel() = ConsultationModel(
    id = id,
    patientId = patientId,
    consultationTypeId = consultationTypeId,
    startedAt = startedAt,
    completedAt = completedAt,
    createdAt = createdAt,
    status = status
)

fun ConsultationModel.toEntity() = ConsultationEntity(
    id = id,
    patientId = patientId,
    consultationTypeId = consultationTypeId,
    startedAt = startedAt,
    completedAt = completedAt,
    createdAt = createdAt,
    status = status
)

fun ConsultationDto.toModel() = ConsultationModel(
    id = id.orEmpty(),
    patientId = patientId,
    consultationTypeId = consultationTypeId ?: 0,
    startedAt = startedAt.orEmpty(),
    completedAt = completedAt.orEmpty(),
    createdAt = createdAt.orEmpty(),
    status = status
)

fun ConsultationModel.toDtoForInsert() = ConsultationDto(
    patientId = patientId,
    consultationTypeId = consultationTypeId,
    startedAt = startedAt.ifBlank { null },
    completedAt = completedAt.ifBlank { null },
    status = status
)

fun ConsultationModel.toDtoForUpdate() = ConsultationDto(
    id = id,
    patientId = patientId,
    consultationTypeId = consultationTypeId,
    startedAt = startedAt.ifBlank { null },
    completedAt = completedAt.ifBlank { null },
    status = status
)

fun ConsultationWithDetailsEntity.toModel() = ConsultationWithDetailsModel(
    consultation = consultation.toModel(),
    patient = patient?.toModel() ?: PatientModel(),
    consultationType = consultationType?.toModel() ?: AppCatalogModel()
)

fun String.normalize(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").lowercase()
}

fun String.parseToDouble(): Double {
    return this.trim()
        .replace(',', '.') // Maneja teclados que insertan coma como decimal
        .toDoubleOrNull() ?: 0.0
}

fun String.parseToInt(defaultValue: Int = 0): Int {
    val cleanInput = this.trim()

    // Intenta parsear directamente como entero
    return cleanInput.toIntOrNull()
    // Si el usuario ingresó decimales (ej. "12.0" o "12,5"), extrae la parte entera
        ?: cleanInput.replace(',', '.')
            .toDoubleOrNull()
            ?.toInt()
        ?: defaultValue
}

fun Double.formatPrice(): String {
    return String.format(Locale.US, "%.2f", this)
}