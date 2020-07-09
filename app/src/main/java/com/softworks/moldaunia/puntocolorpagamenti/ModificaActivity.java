package com.softworks.moldaunia.puntocolorpagamenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class ModificaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private FirebaseFirestore firebaseFirestore;

    private Item item = new Item();
    private ControlloInserimentoDati controlloDati = new ControlloInserimentoDati();
    private Data data = new Data();

    private AlertDialog dialog;

    private EditText modImporto, modEnte, modModalita;
    private TextView modData;
    private FloatingActionButton fab;
    private BottomNavigationView mBottomNavigationView;

    private DatePickerDialog.OnDateSetListener mDataSetListener;

    private String TAG = "ModificaActivity";
    private String id, importo, ente, modalita;
    private int giorno, mese, anno;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica);

        modImporto = findViewById(R.id.modImporto);
        modEnte = findViewById(R.id.modEnte);
        modModalita = findViewById(R.id.modModalita);
        modData = findViewById(R.id.modData);
        fab = findViewById(R.id.floatingActionButtonM);
        mBottomNavigationView = findViewById(R.id.bottomNavigationViewM);

        caricaRisorse();

        mBottomNavigationView.getMenu().removeItem(R.id.app_bar_aggiungi);

        //Torna alla home
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
        //AL CLICK DELLA TEXTVIEW modData
        modData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //data.modificaData(ModificaActivity.this, modData);
                modificaData();
            }
        });
        //AL CLICK DEL PULSANTE AGGIUNGI
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CONTROLLIAMO I CAMPI E CHIEDIAMO CONFERMA ALL'UTENTE
                boolean controlloCampi = controlloDati.controllaCampi(ModificaActivity.this, modImporto, modEnte, modModalita, modData);
                if(controlloCampi){
                    mostraConferma();
                }
            }
        });

    }
    //METODO PER OTTENERE I DATI PASSATI DAL MAINACTIVITY E VISUALIZZARLI DELLE RISPETTIVE EDIT TEXT
    private void caricaRisorse() {
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        this.id = extras.getString("id");
        this.importo = extras.getString("importo");
        this.ente = extras.getString("ente");
        this.modalita = extras.getString("modalita");
        item.setGiorno(extras.getInt("giorno"));
        item.setMese(extras.getInt("mese"));
        item.setAnno(extras.getInt("anno"));

        modImporto.setText(importo);
        modEnte.setText(ente);
        modModalita.setText(modalita);
        modData.setText(item.getGiorno() + "/" + item.getMese() + "/" + item.getAnno());
    }
    //METODO PER MODIFICARE IL PAGAMENTO SUL DATABASE
    private void modPagamento() {

        this.importo = modImporto.getText().toString();
        this.ente = modEnte.getText().toString();
        this.modalita = modModalita.getText().toString();
        this.giorno = item.getGiorno();
        this.mese = item.getMese();
        this.anno = item.getAnno();

        firebaseFirestore = FirebaseFirestore.getInstance();

        final DocumentReference itemRef = firebaseFirestore.collection("Pagamenti")
                .document(this.id);
                itemRef.update(
                "importo", this.importo,
                "ente", this.ente,
                "modalita", this.modalita,
                "giorno", this.giorno,
                "mese", this.mese,
                "anno", this.anno
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Pagamento modificato");
                    Toast.makeText(ModificaActivity.this, "Pagamento modificato con successo!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Errore nel pagamento");
                    Toast.makeText(ModificaActivity.this, "Errore nel modificare il pagamento..", Toast.LENGTH_SHORT).show();
                }
                }
        });
        returnHome();
    }
    //METODO PER CHIEDERE CONFERMA ALL'UTENTE TRAMITE DIALOG
    private void mostraConferma() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vuoi confermare la modifica?")
                .setTitle("Conferma modifica")
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        //IN CASO AFFERMATIVO, MODIFICARE IL PAGAMENTO
                        modPagamento();
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

    private void returnHome() {
        Intent intent = new Intent(ModificaActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    //METODO PER MODIFICARE LA DATA DELLA SUA TEXTVIEW
    private void modificaData() {
        //OTTENGO LA DATA DEL PAGAMENTO DA MODIFICARE
        Calendar calendar = Calendar.getInstance();
        int anno = item.getAnno();
        int mese = item.getMese() - 1; //RICORDA MESI DA 0 A 11 E NEL DB INVECE SONO DA 1 A 12
        int giorno = item.getGiorno();
        //int anno = calendar.get(Calendar.YEAR);
        //int mese = calendar.get(Calendar.MONTH);
        //int giorno = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "data attuale: " + giorno + "/" + mese + "/" + anno);
        DatePickerDialog dialog = new DatePickerDialog(
                ModificaActivity.this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                this,//mDataSetListener
                anno, mese, giorno);
        Log.d(TAG, "modificaData: ho raccolto la data: " + giorno + "/" + mese + "/" + anno);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();

        /*
        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anno, int mese, int giorno) {
                mese++;
                Log.d(TAG, "Questa è la data modificata: " + anno + "/" + mese + "/" + giorno);
                modData.setText(giorno + "/" + mese + "/" + anno);
                //Item item = new Item();
                item.setGiorno(giorno);
                item.setMese(mese);
                item.setAnno(anno);
            }
        };
        */
    }
    //OTTENGO LA DATA SCELTA TRAMITE DIALOG E LA INSERISCO NELLA TEXTVIEW
    @Override
    public void onDateSet(DatePicker datePicker, int anno, int mese, int giorno) {
        mese++;
        Log.d(TAG, "Questa è la data modificata: " + anno + "/" + mese + "/" + giorno);
        modData.setText(giorno + "/" + mese + "/" + anno);
        //Item item = new Item();
        item.setGiorno(giorno);
        item.setMese(mese);
        item.setAnno(anno);
    }
}
