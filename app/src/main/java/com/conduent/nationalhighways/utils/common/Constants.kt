package com.conduent.nationalhighways.utils.common

object Constants {

    const val EXPIRED = "EXPIRED"
    const val RETRY_COUNT = 3
    const val ITEM_COUNT : Long = 20
    const val SPLASH_TIME_OUT = 500L
    const val COUNTRY_TYPE = "country_type"
    const val CHECK_PAID_CROSSING_VRM_ENTERED = "CHECK_PAID_CROSSING_VRM_ENTERED"
    const val CHECK_PAID_CROSSING_VRM_EXISTS = "CHECK_PAID_CROSSING_VRM_EXISTS"
    const val CHECK_PAID_CROSSINGS_VRM_DETAILS = "CHECK_PAID_CROSSINGS_VRM_DETAILS"
    const val CHECK_PAID_CHARGE_DATA_KEY = "from_check_paid_ref_data_to_check_list"
    const val CHECK_PAID_REF_VRM_DATA_KEY = "from_check_paid_ref_data_to_ref_vrm"
    const val FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT =
        "FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT"
    const val FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL =
        "FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL"
    const val FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE =
        "FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE"
    const val FROM_DASHBOARD_TO_CROSSING_HISTORY = 1001
    const val FROM = "from"
    const val UK_VEHICLE_DATA_NOT_FOUND_KEY = "uk_vehicle_data_not_found_key"
    const val ONE_OF_PAYMENTS_PAY_RESP = "one_of_payments_model_res"
    const val OPTIONS_TYPE = "Options_type"
    const val NOMINATED_ACCOUNT_DATA = "nominee_data"
    const val NOMINATED_ACCOUNT = "NOMINATED"
    const val PENDING_STATUS = "PENDING"
    const val CATEGORY_RECEIPTS = "Receipts"
    const val LRDS_ELIGIBILITY_CHECK="lrdsEligibilityCheck"

    const val COUNTRY_TYPE_UK = "UK"
    const val COUNTRY_TYPE_NON_UK = "NON UK"
    const val CASE_COMMENTS_KEY = "case_comments_key"
    const val CASES_CATEGORY = "cases_category"
    const val CASES_SUB_CATEGORY = "cases_sub_category"
    const val PAYG = "PAYG"
    const val PAYMENT_HISTORY = "payment_history"
    const val PAYMENT_METHOD = "payment_method"
    const val PAYMENT_TOP_UP = "payment_top_up"
    const val PERSONAL_TYPE = "personal_type_pre_pay_or_pay_as_go"
    const val FROM_DART_CHARGE_FLOW = "from_dart_charge_flow"
    const val NORMAL_LOGIN_FLOW_CODE = 1001
    const val DART_CHARGE_FLOW_CODE: Int = 1002
    const val FROM_LOGIN_TO_CASES_VALUE = 1003
    const val FROM_CASES_TO_CASES_VALUE = 1004
    const val FROM_ANSWER_TO_CASE_VALUE = 1012

    const val PERSONAL_TYPE_PREPAY = 1005
    const val PERSONAL_TYPE_PAY_AS_U_GO = 1006
    const val UK_VEHICLE_DATA_NOT_FOUND = 1007
    const val FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_PAYMENT_KEY = 1008
    const val FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_EMAIL_KEY = 1009
    const val FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY = 1010
    const val FROM_CREATE_ACCOUNT_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE = 1011


    const val FILE_NAMES_KEY = " file_names_key"

    const val CASES_PROVIDE_DETAILS_KEY = "provide_case_details"

    const val FROM_LOGIN_TO_CASES = "FROM_LOGIN_TO_CASES"
    const val PERSONAL_DETAILS = "personal_details"
    const val POST_CODE_ADDRESS = "post_code_address"
    const val PASSWORD = "password"
    const val PIN = "pin"
    const val LRDS_SCREEN = "lrds_screen"
    const val STATUS_TERMINATED = "TERMINATED"

    const val TRANSACTION_DATE = "Transaction Date"
    const val MAIN_ACCOUNT = "main_account"
    const val SUB_ACCOUNT = "sub_account"
    const val VPN_ERROR = "Check your network connection."
    const val REFRESH_TOKEN = "refresh_token_to_start"
    const val TYPE = "type"
    const val LOGIN = "login"
    const val START_NOW = "start_now"
    const val SESSION_TIME_OUT = "session_time_out"
    const val START_NOW_SCREEN = "start_now"
    const val LOGOUT_SCREEN = "log_out"
    const val FAILED_RETRY_SCREEN = "failed_retry"
    const val LANDING_SCREEN = "landing"
    const val TOLL_TRANSACTION = "Toll_Transaction"
    const val ALL_TRANSACTION = "ALL"
    const val SPREAD_SHEET = "spreadsheet"
    const val PDF = "pdf"
    const val VIEW_ALL = "view_all"
    const val ALL = "ALL"
    const val DATE_FILTER = "date_filter"
    const val NO = "no"
    const val YES = "yes"
    const val Y = "Y"
    const val N = "N"
    const val APPLE_PAY = "apple_pay"
    const val GOOGLE_PAY = "google_pay"
    const val PAY_PAL = "pay_pal"
    const val QUICK_PAYMENT = "quick_payment"
    const val BANK_TRANSFER = "bank_transfer"
    const val CARD_PAYMENT = "card_payment"
    const val OPTIONS = "options"
    const val EMAIL_MODE = 1
    const val MESSAGE_MODE = 2
    const val POST_MAIL_MODE = 3
    const val POST_CODE = "post_code"
    const val DATA = "data"
    const val THRESHOLD_AMOUNT="threshold_amount"
    const val CREATE_ACCOUNT_DATA = "create_account_data"
    const val PLATE_NUMBER = "plateNumber"
    const val OLD_PLATE_NUMBER = "oldPlateNumber"
    const val VEHICLE_INDEX = "vehicle_position"
    const val IS_DBLA_AVAILABLE = "isDblaAvailable"
    const val DATA2 = "list2"
    const val POST_MAIL = "MAIL"
    const val SMS = "SMS"
    const val EMAIL = "EMAIL"
    const val MODE = "mode"
    const val CREATE_ACCOUNT = "CREATE ACCOUNT"
    const val VIEW_CHARGES = "VIEW CHARGES"
    const val CHECK_FOR_PAID = "CHECK FOR PAID CROSSINGS"
    const val RESOLVE_PENALTY = "RESOLVE PENALTY"
    const val ONE_OFF_PAYMENT = "ONE OFF PAYMENT"
    const val PAYMENT_RESPONSE = "payment_resp"
    const val PAYMENT_DATA = "payment_data"
    const val VEHICLE_DATA = "vehicle_data"
    const val VEHICLE_SCREEN_TYPE_LIST = 1
    const val VEHICLE_SCREEN_TYPE_ADD = 2
    const val VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT = 3
    const val VEHICLE_SCREEN_TYPE_HISTORY = 4
    const val VEHICLE_SCREEN_TYPE_CROSSING_HISTORY = 5
    const val VEHICLE_SCREEN_KEY = "com.heandroid.VehicleMgmtActivity.SCREEN"
    const val FROM_DASHBOARD_TO_VEHICLE_LIST = "com_heandroid_dashboard_to_vehiclelist"
    const val VEHICLE_RESPONSE = "vehicle_api_resp"
    const val SHOW_SCREEN = "show_screen"
    const val ABOUT_SERVICE = "about_service"
    const val CONTACT_DART_CHARGES = "contact_dart_charges"
    const val CROSSING_SERVICE_UPDATE = "crossing_service_update"
    const val DART_CHARGE_GUIDANCE_AND_DOCUMENTS = "dart_charge_guidance_documents"

    const val LANGUAGE = "ENU"
    const val ALERT_ITEM_KEY = "cscLookupKey"
    const val PERSONAL_ACCOUNT = "PRIVATE"
    const val BUSINESS_ACCOUNT = "BUSINESS"
    const val EXEMPT_ACCOUNT = "NONREVENUE"
    const val PRE_PAY_ACCOUNT = "pre_pay_account"
    const val STANDARD ="STANDARD"
    const val PAYG_ACCOUNT = "pay_as_you_go_account"
    const val UK = "UK"
    const val NON_UK = "Non UK"
    const val PAYMENT_PAGE = "payment page"
    const val NMI = "nmi"
    const val MAX_VEHICLE_SIZE = 5
    const val MAX_VEHICLE_SIZE_ONE_OFF_PAY = 1

    const val EMAIL_SELECTION_TYPE = "Email"
    const val REFERENCE_ID = "Reference Id"
    const val AGENCY_ID = "18"
    const val NOT_IN_THE_LIST = "Not in the list"
    const val PHONE_COUNTRY_CODE= "PHONE_COUNTRY_CODE"
    const val FIND_VEHICLE_DATA = "FIND_VEHICLE_DATA"
    const val CASE_NUMBER = "case number"
    const val LAST_NAME = "last name"
    const val CLOSED = "Closed"
    const val PDF_EXTENSION = ".pdf"
    const val CSV_EXTENSION = ".csv"
    const val PCN_RESOLVE_URL =
        "https://www.dartford-crossing-charge.service.gov.uk/PcnPayment/SearchMultiplePCN"
    const val CREATE_ACCOUNT_NON_UK = "CreateAccountNonUKModel"

    const val CHOOSE_FILE_1 = "Choose_first_file"
    const val CHOOSE_FILE_2 = "Choose_second_file"
    const val CHOOSE_FILE_3 = "Choose_third_file"
    const val CHOOSE_FILE_4 = "Choose_fourth_file"
    const val PAYMENT = "Payment"
    const val PAYMENT_FILTER_VEHICLE = "Payment_filter_with_vehicle_number"
    const val PAYMENT_FILTER_SPECIFIC = "Payment_filter_with_specified_date"
    const val PAYMENT_DATE_RANGE = "Payment_filter_with_date_range"
    const val UPDATE_PIN_FLOW = "update pin flow"
    const val VEHICLE_NO = "VehicleNo"
    const val COUNTRY_BUSINESS = "country"
    const val NON_UK_VEHICLE_DATA = "Non_UK_Model"
    const val NO_OF_CROSSING_BUSINESS = "Number_of_crossing"
    const val NO_OF_VEHICLE_BUSINESS = "Number_of_vehicle"
    const val IS_CREATE_VEHICLE_GROUP = "is this flow for delete"
    const val VEHICLE_GROUP = "vehicle_group"
    const val VEHICLE_ROW_ITEM = "Vehicle_Row_Item"
    const val ERROR_DIALOG = "error_dialog"
    const val LOADER_DIALOG = "loader_dialog"
    const val RETRY_DIALOG = "retry_dialog"
    const val SESSION_DIALOG = "session_dialog"
    const val LOGOUT_DIALOG = "logout_dialog"
    const val SEARCH_VEHICLE_DIALOG = "search_vehicle_dialog"
    const val DELETE_VEHICLE_GROUP_DIALOG = "delete_vehicle_group_dialog"
    const val DATE_PICKER_DIALOG = "date_picker_dialog"
    const val CROSSING_HISTORY_FILTER_DIALOG = "crossing_history_filter_dialog"
    const val DOWNLOAD_FORMAT_SELECTION_DIALOG = "download_format_selection_dialog"
    const val LIVE = "LIVE"
    const val FROM_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE ="FROM_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE"
    const val VEHICLE_DETAIL="vehicle detail"


    const val EMAIL_ADDRESS = "Email address"
    const val PHONE_NUMBER = "Phone number"

    //Biometric
    const val FROM_LOGIN_TO_BIOMETRIC = "FROM_LOGIN_TO_BIOMETRIC"
    const val FROM_LOGIN_TO_BIOMETRIC_VALUE = 1003
    const val FROM_ACCOUNT_TO_BIOMETRIC_VALUE = 1004


    //Error code for api response
    const val NO_DATA_FOR_GIVEN_INDEX = 1020
    const val NO_DATA_FOR_NOTIFICATIONS = 1220
    const val CASES_GIVEN_DATE_WRONG = 1042


    const val NAV_FLOW_KEY = "navFlowKey"
    const val ACCOUNT_CREATION_EMAIL_FLOW = "accountCreationEmailFlow"
    const val FORGOT_PASSWORD_FLOW = "forgotPasswordFlow"
    const val ACCOUNT_CREATION_MOBILE_FLOW="accountCreationMobileFlow"
    const val IS_PERSONAL_ACCOUNT="isPersonalAccount"
    const val POSTCODE="postcode"
    const val UK_COUNTRY="United Kingdom"
    const val USA="USA"
    const val USA_CODE="USA +1"
    const val UK_CODE="UK +44"
    const val LIST="list"
    const val REMOVE_VEHICLE="remove_vehicle"
    const val EDIT_VEHICLE="edit_vehicle"



}
