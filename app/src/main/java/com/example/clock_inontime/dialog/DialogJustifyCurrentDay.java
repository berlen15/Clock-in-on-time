package com.example.clock_inontime.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.clock_inontime.R;

public class DialogJustifyCurrentDay extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedIntanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogOutTime);
        builder.setTitle("JUSTIFICACIÓN DEL DÍA ACTUAL")
                .setMessage("La justificación de una fecha concreta solo podrá realizarse en dicha fecha.\n" +
                        "No podrá justificar fechas anteriores a la actual.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
