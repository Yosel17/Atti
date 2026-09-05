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
    const val CONSULTATION_TYPE_STEPS_SUPABASE = "consultation_type_steps"
    const val ANAMNESIS_SUPABASE = "anamnesis"
    const val ANAMNESIS_ENV_OPTIONS_SUPABASE = "anamnesis_environment_options"
    const val ANAMNESIS_VACCINES_SUPABASE = "anamnesis_vaccines"
    const val ANAMNESIS_DEWORMINGS_SUPABASE = "anamnesis_dewormings"
    const val CLINICAL_EXAMINATIONS_SUPABASE = "clinical_examinations"
    const val PHYSIOLOGICAL_CONSTANTS_SUPABASE = "physiological_constants"
    const val DIAGNOSES_SUPABASE = "diagnoses"
    const val TREATMENTS_SUPABASE = "treatments"
    const val PRESCRIPTIONS_SUPABASE = "prescriptions"
    const val PRESCRIPTION_ITEMS_SUPABASE = "prescription_items"
    const val OBSERVATIONS_SUPABASE = "observations"
    const val FOLLOW_UPS_SUPABASE = "follow_ups"
    const val RECEIPTS_SUPABASE = "receipts"
    const val RECEIPT_ITEMS_SUPABASE = "receipt_items"

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
    const val CONSULTATION_STEPS_TYPE_CATALOG = 7
    const val ANIMAL_LIFESTYLE_TYPE_CATALOG = 8
    const val VACCINE_NAME_TYPE_CATALOG = 9
    const val VACCINATION_SCHEDULE_TYPE_CATALOG = 10
    const val INTERNAL_DEWORMER_TYPE_CATALOG = 11
    const val EXTERNAL_DEWORMER_TYPE_CATALOG = 12
    const val CONCENTRATE_BRAND_TYPE_CATALOG = 13
    const val CONCENTRATE_UNIT_OF_MEASURE_TYPE_CATALOG = 14
    const val COAT_TYPE_CATALOG = 15
    const val LYMPH_NODE_TYPE_CATALOG = 16
    const val UNIT_OF_WEIGHT_TYPE_CATALOG = 17
    const val DIAGNOSIS_TYPE_CATALOG = 18
    const val PRESETS_CATALOG_TYPE = 19
    const val QUICK_REASONS_CATALOG_TYPE = 20

    //tipo de consultas
    const val GENERAL_CONSULTATION_TYPE = 23

    //pasos de una consulta
    const val CONSULTATION_STEP_ANAMNESIS = 30
    const val CONSULTATION_STEP_CLINICAL_EXAM = 31
    const val CONSULTATION_STEP_PHYSIOLOGICAL_CONSTS = 32
    const val CONSULTATION_STEP_DIAGNOSIS = 33
    const val TREATMENT_STEP_DIAGNOSIS = 35
    const val PRESCRIPTION_STEP_DIAGNOSIS = 36
    const val OBSERVATION_STEP_DIAGNOSIS = 37
    const val FOLLOW_UP_STEP_DIAGNOSIS = 38
    const val RECEIPT_STEP_DIAGNOSIS = 75


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