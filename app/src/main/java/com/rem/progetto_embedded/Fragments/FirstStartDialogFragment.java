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
        void closingStartDialog();
    }

    private FirstStartListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener = (FirstStartListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
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
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("com.rem.progetto_embedded", Context.MODE_PRIVATE);
                // set false if first run is completed
                sharedPreferences.edit().putBoolean("firstrun", false).apply();
                //set update position to true on first run
                sharedPreferences.edit().putBoolean("updatePosition",true).apply();
                //set delete cache to false on first run
                sharedPreferences.edit().putBoolean("deleteCache",false).apply();
                listener.closingStartDialog();
            }
        });
    }

    @Override
    public void onDetach(){
        listener = null;
        super.onDetach();
    }
}