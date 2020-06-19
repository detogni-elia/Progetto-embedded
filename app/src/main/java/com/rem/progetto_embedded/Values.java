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
    public static final String EXTRA_LATITUDE = BASE + "latitude";
    public static final String EXTRA_LONGITUDE = BASE + "longitude";

    //SharedPreferences keys
    public static final String PREFERENCES_NAME = BASE;
    static final String DOWNLOAD_LOCATION = BASE + "downloadLocation";
    static final String LOCATION_INTERNAL = BASE + "locationInternal";
    static final String LOCATION_EXTERNAL = BASE + "locationExternal";
    static final String NOT_YET_DECIDED = BASE + "notYetDecided";
    public static final String FIRST_RUN = BASE + "firstRun";
    public static final String UPDATE_POSITION = BASE + "updatePosition";
    public static final String LATITUDE = BASE + "latitude";
    public static final String LONGITUDE = BASE + "longitude";

    //Countries english names for database queries
    public static final String[] COUNTRIES_DEFAULT_NAMES = {
            "India",
            "South-Africa",
            "Italy"
    };

    //Ids of contact type for localization
    static final int[] CONTACTS_IDS = {
            R.string.contact_bite,
            R.string.contact_stung,
            R.string.contact_eaten,
            R.string.contact_contact
    };

    //Languages default names used when selecting download resources
    public static final String[] LANGUAGES_DEFAULT_NAMES = {
            "it",
            "en"
    };

    //Image quality names used when selecting download resources
    public static final String[] IMAGE_QUALITY_NAMES = {
            "Low",
            "Medium",
            "High"
    };

    //Contacts default values for database queries
    public static final String[] CONTACTS_DEFAULT_NAMES = {
            "Bite",
            "Sting",
            "Eaten",
            "Skin contact"
    };

    //Categories default names
    public static final String CATEGORY_ANIMALS = "Animals";
    public static final String CATEGORY_PLANTS = "Plants";
    public static final String CATEGORY_INSECTS = "Insects";

    //Symptoms default values for database queries
    public static final String[] SYMPTOMS_DEFAULT_NAMES = {
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
}
