package com.detons97gmail.progetto_embedded;

public class Values {
    //String extras
    private static final String BASE = "it.unipd.dei.esp1920.REM.";
    public static final String EXTRA_IMAGE_PATH = BASE + "ImagePath";
    public static final String EXTRA_COUNTRY = BASE + "Country";
    public static final String EXTRA_CATEGORY = BASE + "Category";
    public static final String EXTRA_NAME = BASE + "Name";
    public static final String EXTRA_SPECIES = BASE + "Species";
    public static final String EXTRA_DIET = BASE + "Diet";
    public static final String EXTRA_SYMPTOMS = BASE + "Symptoms";

    //Ids of countries for localization
    public static final int[] COUNTRIES_IDS = {
            R.string.in,
            R.string.cn,
            R.string.it
    };

    //Countries default values for database queries
    public static final String[] SUPPORTED_COUNTRIES_NAMES = {
            "India",
            "China",
            "Italy"
    };

    //Ids of contact type for localization
    public static final int[] CONTACTS_IDS = {
            R.string.contact_bite,
            R.string.contact_stung,
            R.string.contact_eaten,
            R.string.contact_contact
    };

    //Contacts default values for database queries
    public static final String[] CONTACTS_STRINGS = {
            "bite",
            "stung",
            "eaten",
            "contact"
    };

    public static final String CATEGORY_ANIMALS = "Animals";
    public static final String CATEGORY_PLANTS = "Plants";
    public static final String CATEGORY_INSECTS = "Insects";

    //Ids of symptoms for localization
    public static final int[] SYMPTOMS_IDS = {
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

    //Symptoms default values for database queries
    public static final String[] SYMPTOMS_STRINGS = {
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
            "swollen__lymph_glands"};
}
