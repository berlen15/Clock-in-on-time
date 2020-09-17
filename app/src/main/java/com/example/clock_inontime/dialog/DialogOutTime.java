package com.example.clock_inontime.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.clock_inontime.R;

public class DialogOutTime extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedIntanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogOutTime);
        builder.setTitle("FICHAJE DE ENTRADA NO REALIZADO")
                .setMessage("Para poder fichar la salida se debe haber registrado el horario de entrada.\n" +
                        "Si tiene problemas, póngase en contacto con el admistrador de su compañía")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
