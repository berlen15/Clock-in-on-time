package com.example.clock_inontime.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.clock_inontime.R;

public class DialogLocationDenied extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedIntanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogOutTime);
        builder.setTitle("SERVICIO DE LOCALIZACIÓN NO ACTIVADO")
                .setMessage("Para poder fichar se nesecitan permisos de localización.\n")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
