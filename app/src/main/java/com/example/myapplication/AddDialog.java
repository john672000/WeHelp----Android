package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddDialog extends AppCompatDialogFragment {
    private EditText editTextName, editTextQuantity;
    private AddDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);

        builder.setView(view).setTitle("Add Item").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = editTextName.getText().toString().trim();
                String itemQuantity = editTextQuantity.getText().toString().trim();

                listener.applyTexts(itemName, itemQuantity);
            }
        });
        editTextName = view.findViewById(R.id.dialogName);
        editTextQuantity = view.findViewById(R.id.dialogQuantity);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddDialogListener");
        }
    }

    public interface AddDialogListener {
        void applyTexts(String itemName, String itemQuantity);
    }
}
