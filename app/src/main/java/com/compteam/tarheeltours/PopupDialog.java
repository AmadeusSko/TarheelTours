package com.compteam.tarheeltours;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PopupDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private String location;
    public PopupDialog(String location){
        location = location;
    }
    public interface Listener{
        void onAcceptedListener();
        void onCancelledListener();
    }


    private Listener mListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement MyDialog.Listener!");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Want more info?", this);
        builder.setNegativeButton("Cancel", this);
        builder.setTitle("Approaching a new location!");
        builder.setMessage("Now approaching: "+ location);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Toast.makeText(getContext(), "Displaying info!", Toast.LENGTH_SHORT).show();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                Toast.makeText(getContext(), "Maybe next time", Toast.LENGTH_SHORT).show();
                break;

            default:
                // This should not happen.
                break;
        }
    }
}
