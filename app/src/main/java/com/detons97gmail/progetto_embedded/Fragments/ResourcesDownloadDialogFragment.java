package com.detons97gmail.progetto_embedded.Fragments;

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

import com.detons97gmail.progetto_embedded.R;
import com.detons97gmail.progetto_embedded.Services.FakeDownloadIntentService;
import com.detons97gmail.progetto_embedded.Utilities;
import com.detons97gmail.progetto_embedded.Values;

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
        final Spinner countries_spinner = view.findViewById(R.id.countries_spinner);
        String[] supportedCountries = Utilities.getLocalizedSupportedCountries(getContext());
        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, supportedCountries);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countries_spinner.setAdapter(countriesAdapter);
        countriesAdapter.notifyDataSetChanged();

        final Spinner languages_spinner = view.findViewById(R.id.languages_spinner);
        String[] supportedLanguages = Utilities.getLocalizedSupportedLanguages(getContext());
        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, supportedLanguages);
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languages_spinner.setAdapter(languagesAdapter);

        final Spinner image_quality_spinner = view.findViewById(R.id.image_quality_spinner);
        String[] supportedImageQuality = Utilities.getSupportedImageQuality(getContext());
        ArrayAdapter<String> imagesQualityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, supportedImageQuality);
        imagesQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        image_quality_spinner.setAdapter(imagesQualityAdapter);

        Button ok_button = view.findViewById(R.id.ok_button);
        Button cancel_button = view.findViewById(R.id.cancel_button);

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country = (String) countries_spinner.getSelectedItem();
                String language = (String)languages_spinner.getSelectedItem();
                String imageQuality = (String)image_quality_spinner.getSelectedItem();
                //save in SharedPreference for settings activity display information layout
                SharedPreferences prefs = getContext().getSharedPreferences("com.detons97gmail.progetto_embedded",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putString("selectedLanguage",language);
                editor.putString("selectedImageQuality",imageQuality);
                editor.apply();
                startDownloadService(
                        Utilities.getCountryNameInEnglish(getContext(), country),
                        Utilities.getLanguageNameInEnglish(getContext(), language),
                        Utilities.getQualityValueInEnglish(getContext(),imageQuality));
                dismiss();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void startDownloadService(String country, String language, String imageQuality){
        Intent startIntent = new Intent(getContext(), FakeDownloadIntentService.class);
        startIntent.putExtra(Values.EXTRA_COUNTRY, country);
        startIntent.putExtra(Values.EXTRA_LANGUAGE, language);
        startIntent.putExtra(Values.EXTRA_IMAGE_QUALITY, imageQuality);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            getActivity().startForegroundService(startIntent);
        else
            getActivity().startService(startIntent);
    }
}
