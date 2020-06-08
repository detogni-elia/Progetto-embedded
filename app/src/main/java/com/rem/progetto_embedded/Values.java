package com.rem.progetto_embedded;

/**
 * Class contains values used throughout the app and maps ids of important strings
 */
public class Values {
    //String extras
    private static final String BASE = "com.rem.progetto_embedded.";
    public static final String EXTRA_IMAGE_PATH = BASE + "ImagePath";
    public static final String EXTRA_COUNTRY = BASE + "Country";
    public static final String EXTRA_LANGUAGE = BASE + "Language";
    public static final String EXTRA_CATEGORY = BASE + "Category";
    public static final String EXTRA_NAME = BASE + "Name";
    public static final String EXTRA_SPECIES = BASE + "Species";
    public static final String EXTRA_DIET = BASE + "Diet";
    public static final String EXTRA_CONTACT = BASE + "Contact";
    public static final String EXTRA_SYMPTOMS = BASE + "Symptoms";
    public static final String EXTRA_IMAGE_QUALITY = BASE + "ImageQuality";

    //SharedPreferences keys
    public static final String PREFERENCES_NAME = BASE;
    public static final String DOWNLOAD_LOCATION = BASE + "downloadLocation";
    public static final String LOCATION_INTERNAL = BASE + "locationInternal";
    public static final String LOCATION_EXTERNAL = BASE + "locationExternal";
    public static final String NOT_YET_DECIDED = BASE + "notYetDecided";

    //Ids of countries for localization
    private static final int[] COUNTRIES_IDS = {
            R.string.in,
            R.string.s_africa,
            R.string.it
    };

    static int[] getCountriesIds(){
        return COUNTRIES_IDS;
    }

    //Countries english names for database queries
    private static final String[] COUNTRIES_DEFAULT_NAMES = {
            "India",
            "South-Africa",
            "Italy"
    };

    public static String[] getCountriesDefaultNames(){
        return COUNTRIES_DEFAULT_NAMES;
    }

    //Ids of contact type for localization
    private static final int[] CONTACTS_IDS = {
            R.string.contact_bite,
            R.string.contact_stung,
            R.string.contact_eaten,
            R.string.contact_contact
    };

    private static final int[] LANGUAGES_IDS = {
            R.string.italian,
            R.string.english
    };

    private static final String[] LANGUAGES_DEFAULT_NAMES = {
            "it",
            "en"
    };

    private static final int[] IMAGE_QUALITY_IDS = {
            R.string.low,
            R.string.medium,
            R.string.high
    };

    private static final String[] IMAGE_QUALITY_NAMES = {
            "Low",
            "Medium",
            "High"
    };

    static int[] getImageQualityIds(){
        return IMAGE_QUALITY_IDS;
    }

    public static String[] getImageQualityNames(){
        return IMAGE_QUALITY_NAMES;
    }

    public static int[] getLanguagesIds(){
        return LANGUAGES_IDS;
    }

    public static String[] getLanguagesDefaultNames(){
        return LANGUAGES_DEFAULT_NAMES;
    }

    public static int[] getContactsTypeIds(){
        return CONTACTS_IDS;
    }

    //Contacts default values for database queries
    private static final String[] CONTACTS_STRINGS = {
            "bite",
            "stung",
            "eaten",
            "contact"
    };

    public static String[] getContactTypesDefaultNames(){
        return CONTACTS_STRINGS;
    }

    public static final String CATEGORY_ANIMALS = "Animals";
    public static final String CATEGORY_PLANTS = "Plants";
    public static final String CATEGORY_INSECTS = "Insects";

    private static final int[] SPECIES_IDS = {
            R.string.animals,
            R.string.insects,
            R.string.plants
    };

    static int[] getSpeciesIds(){
        return SPECIES_IDS;
    }

    private static final String[] SPECIES_STRINGS = {
            "Animals",
            "Insects",
            "Plants"
    };

    public static String[] getSpeciesDefaultNames(){
        return SPECIES_STRINGS;
    }

    //Ids of symptoms for localization
    private static final int[] SYMPTOMS_IDS = {
            R.string.symptom_swelling,
            R.string.symptom_pain,
            R.string.symptom_burning,
            R.string.weakess,
            R.string.symptom_breathing,
            R.string.symptom_vomiting,
            R.string.symptom_blurred,
            R.string.symptom_sweating,
            R.string.symptom_salivating,
            R.string.symptom_numbness,
            R.string.symptom_itching,
            R.string.symptom_rash,
            R.string.symptom_headache,
            R.string.symptom_cramping,
            R.string.symptom_fever,
            R.string.sympton_chills,
            R.string.irregular_heartbeat,
            R.string.symptoms_swollen_lymph_glands};

    static int[] getSymptomsIds(){
        return SYMPTOMS_IDS;
    }

    //Symptoms default values for database queries
    private static final String[] SYMPTOMS_STRINGS = {
            "swelling",
            "pain",
            "burning",
            "weakness",
            "breathing",
            "vomiting",
            "blurred_vision",
            "sweating",
            "salivating",
            "numbness",
            "itching",
            "rash",
            "headache",
            "cramping",
            "fever",
            "chills",
            "irregular_hearbeat",
            "swollen_lymph_glands"};

    public static String[] getSymptomsDefaultNames(){
        return SYMPTOMS_STRINGS;
    }
}