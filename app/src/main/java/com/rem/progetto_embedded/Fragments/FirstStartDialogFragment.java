package com.rem.progetto_embedded.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Values;

public class FirstStartDialogFragment extends DialogFragment {
    public interface FirstStartListener{
        void onClosingFirstStartDialog();
    }

    private FirstStartListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener = (FirstStartListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setCancelable(false);
        View view = inflater.inflate(R.layout.first_start_permissions_dialog_layout, container, false);
        if(getDialog() != null && getDialog().getWindow()!= null){
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Button okButton = view.findViewById(R.id.ok_button);
        setCancelable(false);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Values.PREFERENCES_NAME, Context.MODE_PRIVATE);
                // set false if first run is completed
                sharedPreferences.edit().putBoolean(Values.FIRST_RUN, false).apply();

                listener.onClosingFirstStartDialog();
            }
        });
    }

    @Override
    public void onDetach(){
        listener = null;
        super.onDetach();
    }
}