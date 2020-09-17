package com.example.clock_inontime.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.clock_inontime.R;

public class DialogReminderRegisterLocation extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedIntanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogOutTime);
        builder.setTitle("RECORDATORIO")
                .setMessage("Recuerde no estar en movimiento al realizar los fichajes. \n" +
                        "Si usted se está en movimiento, no se podrá obtener \n" +
                        "la ubicación en la que se encuentra y no se realizará dicho registro.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
