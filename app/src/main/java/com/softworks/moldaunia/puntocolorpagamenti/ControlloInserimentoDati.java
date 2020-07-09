package com.softworks.moldaunia.puntocolorpagamenti;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ControlloInserimentoDati {
    //METODO PER CONTROLLARE CHE I CAMPI NON SIANO VUOTI
    public boolean controllaCampi(Context context, EditText editTextImporto, EditText editTextEnte, EditText editTextModPag, TextView textViewData){
        boolean result = true;
        String importo = editTextImporto.getText().toString();
        String ente = editTextEnte.getText().toString();
        String modalita = editTextModPag.getText().toString();
        String data = textViewData.getText().toString();
        Log.d("ControlloDati", "I valori sono: " + importo + " " + ente + " " + modalita + " " + data);
        if(importo.isEmpty()){
            result = false;
            Toast.makeText(context, "Controllare il campo IMPORTO", Toast.LENGTH_SHORT).show();
        }
        if(ente.isEmpty()){
            result = false;
            Toast.makeText(context, "Controllare il campo ENTE", Toast.LENGTH_SHORT).show();
        }
        if(modalita.isEmpty()){
            result = false;
            Toast.makeText(context, "Controllare il campo Modalità Pagamento", Toast.LENGTH_SHORT).show();
        }
        if(data.equals("Data")){
            result = false;
            Toast.makeText(context, "Selezionare una data", Toast.LENGTH_SHORT).show();
        }
        return result;
    }


}
