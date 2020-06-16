package com.rem.progetto_embedded.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.rem.progetto_embedded.R;

public class ConnectionDialogFragment extends DialogFragment {
    public static final String ALERT_NO_DIALOG = "alert-no-dialog";

    public interface ConnectionDialogDismissListener{
        void onConnectionDialogDismiss();
    }

    private ConnectionDialogDismissListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(getActivity() instanceof ConnectionDialogDismissListener)
            listener = (ConnectionDialogDismissListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.connection_alert_dialog_layout, container, false);
        if(getDialog() != null && getDialog().getWindow()!= null){
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Button okButton = view.findViewById(R.id.ok_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        setCancelable(false);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String tag = getTag();
                //ALERT_NO_DIALOG means that the listener wants to handle itself the download
                if(tag != null && tag.equals(ALERT_NO_DIALOG)){
                    listener.onConnectionDialogDismiss();
                }
                else {
                    //Show dialog to download resources
                    FragmentManager manager = getParentFragmentManager();
                    new ResourcesDownloadDialogFragment().show(manager, "download");
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
