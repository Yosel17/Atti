package yosel.dev.atti.core.utils

object Constants {
    //Room
    const val TABLE_NAME = "atti_database"

    //Supabase
    const val CLIENTS_SUPABASE = "clients"
    const val PATIENTS_SUPABASE = "patients"
    const val APP_CATALOGS_SUPABASE = "app_catalogs"
    const val PRODUCTS_SUPABASE = "products"
    const val SERVICES_SUPABASE = "services"
    const val SUPPLIERS_SUPABASE = "suppliers"
    const val SERVICE_SUPPLIES_SUPABASE = "service_supplies"
    const val CONSULTATIONS_SUPABASE = "consultations"

    //inputs
    const val FIRST_NAME_FIELD = 0
    const val LAST_NAME_FIELD = 1
    const val DOCUMENT_ID_FIELD = 2
    const val PHONE_NUMBER_FIELD = 3
    const val EMAIL_FIELD = 4
    const val ADDRESS_FIELD = 5

    //inputs patient
    const val PATIENT_NAME_FIELD = 6
    const val PATIENT_BREED_FIELD = 7
    const val PATIENT_AGE_YEARS_FIELD = 8
    const val PATIENT_AGE_MONTHS_FIELD = 9
    const val PATIENT_COLOR_FIELD = 10

    //inputs supplier
    const val SUPPLIER_NAME_FIELD = 0
    const val SUPPLIER_TAX_ID_FIELD = 1
    const val SUPPLIER_PHONE_FIELD = 2
    const val SUPPLIER_ADDRESS_FIELD = 3

    //inputs Product
    const val PRODUCT_COMMERCIAL_NAME_FIELD = 0
    const val PRODUCT_BRAND_FIELD = 1
    const val PRODUCT_CATEGORY_FIELD = 2
    const val PRODUCT_UNIT_TYPE_FIELD = 3
    const val PRODUCT_PURCHASE_PRICE_FIELD = 4
    const val PRODUCT_SALE_PRICE_FIELD = 5
    const val PRODUCT_STOCK_FIELD = 6
    const val PRODUCT_MIN_STOCK_FIELD = 7
    const val PRODUCT_SUPPLIER_FIELD = 8

    //inputs Service
    const val SERVICE_NAME_FIELD = 0
    const val SERVICE_CATEGORY_FIELD = 1
    const val SERVICE_SALE_PRICE_FIELD = 2
    const val SERVICE_ESTIMATED_COST_FIELD = 3

    //tipos de catalogos
    const val SPECIES_TYPE_CATALOG = 1
    const val GENDER_TYPE_CATALOG = 2
    const val PRODUCT_CATEGORY_TYPE_CATALOG = 3
    const val PRODUCT_UNIT_OF_MEASURE_TYPE_CATALOG = 4
    const val SERVICE_CATEGORY_TYPE_CATALOG = 5
    const val CONSULTATION_REASON_TYPE_CATALOG = 6

    //Catalogos
    const val CANINE_SPECIES_CATALOG = 1
    const val FELINE_SPECIES_CATALOG = 3
    const val WILD_SPECIES_CATALOG = 4
    const val FEMALE_GENDER_CATALOG = 2
    const val MALE_GENDER_CATALOG = 5

    //Estados Globales
    const val ACTIVE_STATUS = 1
    const val INACTIVE_STATUS = 2
    const val DELETED_STATUS = 3

    //Estados de los pacientes
    const val ACTIVE_PATIENT_STATUS = 1
    const val INACTIVE_PATIENT_STATUS = 2
    const val DELETED_PATIENT_STATUS = 3

    //Estados de los clientes
    const val ACTIVE_CLIENT_STATUS = 1
    const val INACTIVE_CLIENT_STATUS = 2
    const val DELETED_CLIENT_STATUS = 3
}