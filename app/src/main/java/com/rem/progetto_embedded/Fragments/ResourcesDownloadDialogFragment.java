package com.rem.progetto_embedded.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Services.FakeDownloadIntentService;
import com.rem.progetto_embedded.Utilities;
import com.rem.progetto_embedded.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dialog fragment asks the user to download resources when those are not present
 */

public class ResourcesDownloadDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.resources_download_dialog_layout, container, false);
        if(getDialog() != null && getDialog().getWindow()!= null){
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        final Context context = getContext();
        if(context == null)
            return;

        //Populate country spinner
        final Spinner countries_spinner = view.findViewById(R.id.countries_spinner);
        String[] supportedCountries = Utilities.getLocalizedSupportedCountries(context);
        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, supportedCountries);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countries_spinner.setAdapter(countriesAdapter);
        countriesAdapter.notifyDataSetChanged();

        //Populate image quality spinner
        final Spinner image_quality_spinner = view.findViewById(R.id.image_quality_spinner);
        String[] supportedImageQuality = Utilities.getLocalizedImagesQualities(getContext());
        ArrayAdapter<String> imagesQualityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, supportedImageQuality);
        imagesQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        image_quality_spinner.setAdapter(imagesQualityAdapter);

        Button okButton = view.findViewById(R.id.ok_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get selected item for each spinner
                String country = Values.getCountriesDefaultNames()[countries_spinner.getSelectedItemPosition()];
                String imageQuality = Values.getImageQualityNames()[image_quality_spinner.getSelectedItemPosition()];

                //Start download of selected resources
                startDownloadService(country, imageQuality);

                //Close the dialog
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * Start FakeDownloadIntentService to simulate download of resources
     * @param country The english name of the selected country
     * @param imageQuality The english name of the selected image quality
     */
    private void startDownloadService(String country, String imageQuality){
        Intent startIntent = new Intent(getContext(), FakeDownloadIntentService.class);
        startIntent.putExtra(Values.EXTRA_COUNTRY, country);
        //Set english language if system language is not supported
        String locale = Locale.getDefault().getLanguage();
        String[] languages = Values.getLanguagesDefaultNames();
        boolean supported = false;
        for(String lang: languages){
            if(locale.equals(lang)) {
                supported = true;
                break;
            }
        }
        if(!supported)
            locale = "en";

        startIntent.putExtra(Values.EXTRA_LANGUAGE, locale);
        startIntent.putExtra(Values.EXTRA_IMAGE_QUALITY, imageQuality);

        if(getActivity() != null) {
            //We need to start download service as foreground service starting from Android O
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                getActivity().startForegroundService(startIntent);
            else
                getActivity().startService(startIntent);
        }
    }
}
