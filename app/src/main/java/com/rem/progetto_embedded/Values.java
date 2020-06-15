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
    public static final String EXTRA_DESCRIPTION = BASE + "Description";
    public static final String EXTRA_IMAGE_QUALITY = BASE + "ImageQuality";
    public static final String EXTRA_SKIP_DATABASE = BASE + "SkipDatabase";

    //SharedPreferences keys
    public static final String PREFERENCES_NAME = BASE;
    static final String DOWNLOAD_LOCATION = BASE + "downloadLocation";
    static final String LOCATION_INTERNAL = BASE + "locationInternal";
    static final String LOCATION_EXTERNAL = BASE + "locationExternal";
    static final String NOT_YET_DECIDED = BASE + "notYetDecided";
    public static final String FIRST_RUN = BASE + "firstRun";
    public static final String DELETE_CACHE = BASE +"deleteCache";
    public static final String LANGUAGE_CHANGED = BASE +"langChanged";
    public static final String DESTROYED = BASE + "destroyed";
    public static final String UPDATE_POSITION = BASE + "updatePosition";
    public static final String LANGUAGE = BASE + "language";
    public static final String IMAGE_QUALITY = BASE +"selectedImageQuality";

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

    static int[] getLanguagesIds(){
        return LANGUAGES_IDS;
    }

    public static String[] getLanguagesDefaultNames(){
        return LANGUAGES_DEFAULT_NAMES;
    }

    static int[] getContactsTypeIds(){
        return CONTACTS_IDS;
    }

    //Contacts default values for database queries
    private static final String[] CONTACTS_STRINGS = {
            "Bite",
            "Sting",
            "Eaten",
            "Skin contact"
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
            R.string.symptoms_ptosis,
            R.string.symptoms_vomiting,
            R.string.symptoms_dizziness,
            R.string.symptoms_allergic_reaction,
            R.string.symptoms_pain_at_bite_site,
            R.string.symptoms_swelling,
            R.string.symptoms_local_burning,
            R.string.symptoms_urticaria,
            R.string.symptoms_inflammation,
            R.string.symptoms_redness,
            R.string.symptoms_vesication,
            R.string.symptoms_tetanic_convulsion,
            R.string.symptoms_headache,
            R.string.symptoms_convulsions,
            R.string.symptoms_endema,
            R.string.symptoms_discoloration,
            R.string.symptoms_acute_pain,
            R.string.symptoms_soreness,
            R.string.symptoms_ulcer,
            R.string.symptoms_pain,
            R.string.symptoms_muscular_paralysis,
            R.string.symptoms_chocking,
            R.string.symptoms_diarrhea,
            R.string.symptoms_necrosis,
            R.string.symptoms_sleepiness,
            R.string.symptoms_itchiness,
            R.string.symptoms_abdominal_cramps,
            R.string.symptoms_irritation,
            R.string.symptoms_hallucinations
    };

    static int[] getSymptomsIds(){
        return SYMPTOMS_IDS;
    }

    //Symptoms default values for database queries
    private static final String[] SYMPTOMS_STRINGS = {
            "Ptosis",
            "Vomiting",
            "Dizziness",
            "Allergic Reactions",
            "Pain at the site of the bite",
            "Swelling",
            "Local burning sensation",
            "Urticaria",
            "Inflammation",
            "Redness",
            "Vesication",
            "Tetanic convulsions",
            "Headache",
            "Convulsions",
            "Endema",
            "Discoloration",
            "Acute Pain",
            "Soreness",
            "Ulcer",
            "Pain",
            "Muscular paralysis",
            "Choking sensation",
            "Diarrhea",
            "Necrosis",
            "Sleepiness",
            "Itchiness",
            "Abdominal cramps",
            "Irritation",
            "Hallucinations"
    };

    public static String[] getSymptomsDefaultNames(){
        return SYMPTOMS_STRINGS;
    }
}
