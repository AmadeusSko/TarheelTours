package com.compteam.tarheeltours;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InfoDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private String mInfo;
    private String mTitle;
    public InfoDialog(String info, String title){
        mInfo = info;
        mTitle = title;
    }
    public interface InfoListener{
        void infoCancelledListener();
    }


    private InfoListener mListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (InfoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement MyDialog.Listener!");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton("Cancel", this);
        builder.setTitle(mTitle);
        builder.setMessage(mInfo);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
    }
}
