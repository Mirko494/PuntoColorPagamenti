package com.softworks.moldaunia.puntocolorpagamenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InsPagamentoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<Item> itemList;
    private Item item = new Item();
    private ControlloInserimentoDati controlloDati = new ControlloInserimentoDati();
    private Data data = new Data();

    private TextView insData;
    private EditText insImporto;
    private EditText insEnte;
    private EditText insMod;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private BottomNavigationView mBottomNavigationView;
    private FloatingActionButton fabInsPag;

    private AlertDialog dialog;

    private String TAG = "InsPagamentoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ins_pagamento);

        insImporto = findViewById(R.id.insImporto);
        insEnte = findViewById(R.id.insEnte);
        insMod = findViewById(R.id.insMod);
        insData = (TextView) findViewById(R.id.insData);
        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        fabInsPag = findViewById(R.id.floatingActionButton);
        //AL CLICK DELLA TEXTVIEW insData
        insData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //data.modificaData(InsPagamentoActivity.this, insData);
                inserisciData();
                /*
                Calendar calendar = Calendar.getInstance();
                int anno = calendar.get(Calendar.YEAR);
                int mese = calendar.get(Calendar.MONTH);
                int giorno = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InsPagamentoActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog,
                        mDataSetListener,
                        anno, mese, giorno);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
                */
            }
        });
        /*
        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anno, int mese, int giorno) {
                mese++;
                Log.d("InsPagamentoActivity", "Questa è la data: " + anno + "/" + mese + "/" + giorno);
                insData.setText(giorno + "/" + mese + "/" + anno);
                //Item item = new Item();
                item.setGiorno(giorno);
                item.setMese(mese);
                item.setAnno(anno);
            }
        };
        */


        mBottomNavigationView.getMenu().removeItem(R.id.app_bar_aggiungi);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.app_bar_home:
                        returnHome();
                        break;
                }

                return false;
            }
        });
        //AL CLICK DEL PULSANTE AGGIUNGI
        fabInsPag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //insPagamento();
                boolean controllaCampi = controlloDati.controllaCampi(InsPagamentoActivity.this, insImporto, insEnte, insMod, insData);
                if (controllaCampi){
                    mostraConferma();
                }
            }
        });

    }

    private void returnHome() {
        Intent intent = new Intent(InsPagamentoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    //METODO PER INSERIRE IL PAGAMENTO NEL DB OTTENENDO I VALORI DALLE RISPETTIVE EDIT TEXT
    private void insPagamento(){
        String importo = insImporto.getText().toString();
        String ente = insEnte.getText().toString();
        String modalita = insMod.getText().toString();
        //String data = insData.getText().toString();
        int giorno = item.getGiorno();
        int mese = item.getMese();
        int anno = item.getAnno();
        //itemList = mainActivity.getItemList();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Map<String, Object> item = new HashMap<>();
        item.put("importo", importo);
        item.put("ente", ente);
        item.put("modalita", modalita);
        //item.put("data", data);
        item.put("giorno", giorno);
        item.put("mese", mese);
        item.put("anno", anno);

        firebaseFirestore.collection("Pagamenti")
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot aggiunto con ID: " + documentReference.getId());
                        Toast.makeText(InsPagamentoActivity.this, "Inserimento avvenuto con successo!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Errore nell'aggiunta del documento", e);
                        Toast.makeText(InsPagamentoActivity.this, "Errore nell'inserimento..", Toast.LENGTH_SHORT).show();
                    }
                });
        //itemList.add(new Item(importo, ente, modPag, data));
        returnHome();
    }

    //METODO PER CHIEDERE CONFERMA ALL'UTENTE TRAMITE DIALOG
    private void mostraConferma() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vuoi confermare l'inserimento del pagamento?")
                .setTitle("Conferma inserimento")
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        //IN CASO AFFERMATIVO, MODIFICARE IL PAGAMENTO
                        insPagamento();
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
    //METODO PER INSERIRE LA DATA
    private void inserisciData(){
        //OTTENGO LA DATA ODIERNA
        Calendar calendar = Calendar.getInstance();
        int anno = calendar.get(Calendar.YEAR);
        int mese = calendar.get(Calendar.MONTH);
        int giorno = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                InsPagamentoActivity.this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                this,
                anno, mese, giorno);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }
    //OTTENGO LA DATA SCELTA TRAMITE DIALOG E LA INSERISCO NELLA TEXTVIEW
    @Override
    public void onDateSet(DatePicker datePicker, int anno, int mese, int giorno) {
        mese++;
        Log.d(TAG, "Questa è la data: " + anno + "/" + mese + "/" + giorno);
        insData.setText(giorno + "/" + mese + "/" + anno);
        item.setGiorno(giorno);
        item.setMese(mese);
        item.setAnno(anno);
    }
}
