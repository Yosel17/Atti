package yosel.dev.atti.core.utils

object Constants {

    //Room
    const val TABLE_NAME = "atti_database"

    //Supabase
    const val CLIENTS_SUPABASE = "clients"
    const val PATIENTS_SUPABASE = "patients"
    const val APP_CATALOGS_SUPABASE = "app_catalogs"

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

    //tipos de catalogos
    const val SPECIES_TYPE_CATALOG = 1
    const val GENDER_TYPE_CATALOG = 2

    //Catalogos
    const val CANINE_SPECIES_CATALOG = 1
    const val FELINE_SPECIES_CATALOG = 3
    const val WILD_SPECIES_CATALOG = 4

    const val FEMALE_GENDER_CATALOG = 2
    const val MALE_GENDER_CATALOG = 5

    //Estados de los pacientes
    const val ACTIVE_PATIENT_STATUS = 1
    const val INACTIVE_PATIENT_STATUS = 2
    const val DELETED_PATIENT_STATUS = 3

    //Estados de los clientes
    const val ACTIVE_CLIENT_STATUS = 1
    const val INACTIVE_CLIENT_STATUS = 2
    const val DELETED_CLIENT_STATUS = 3
}