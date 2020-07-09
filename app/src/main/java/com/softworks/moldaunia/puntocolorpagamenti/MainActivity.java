package com.softworks.moldaunia.puntocolorpagamenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemListener {
    private FirebaseFirestore firebaseFirestore;

    public CoordinatorLayout coordinatorLayout;
    private ItemTouchHelper itemTouchHelper;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;
    private TextView textViewDataMain;
    private ImageView meseSuccessivo, mesePrecedente, listaVuota;

    private AlertDialog dialog;

    private BottomNavigationView mbottomNavigationView;
    private MenuItem menuItem;

    private Item item;
    private Data data  = new Data();

    private String TAG = "MainActivity";
    private String mese;
    private int anno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        mbottomNavigationView = findViewById(R.id.bottomNavigationView);
        meseSuccessivo = findViewById(R.id.meseSuccessivo);
        mesePrecedente = findViewById(R.id.mesePrecedente);
        textViewDataMain = findViewById(R.id.textMeseAnno);
        listaVuota = findViewById(R.id.listaVuota);

        createItemList();

        buildRecyclerView();
        Log.d(TAG, "onCreate: Vado in getData per ottenere la data corrente!");
        getData();

        //setUpRecyclerView();

        //setSupportActionBar(mbottomNavigationView);

        mbottomNavigationView.getMenu().removeItem(R.id.app_bar_home);

        mbottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.app_bar_aggiungi:
                        inserisciPagamento();
                        break;
                }

                return false;
            }
        });
        //AL CLICK PASSARE AL MESE SUCCESSIVO
        meseSuccessivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createDialogWithoutDateField().show();
                //show();
               //int[] result = data.nextMonth(mese, anno);
                Log.d(TAG, "data prima del click: " + mese + "/" + anno);
                Pair<String, Integer> p = data.nextMonth(mese, anno);
                mese = p.first;
                anno = p.second;
                textViewDataMain.setText(mese + " " + anno);
                Log.d(TAG, "data dopo del click: " + mese + "/" + anno);
                mostraPagamentiMese();
            }
        });
        //AL CLICK PASSARE AL MESE PRECEDENTE
        mesePrecedente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pair<String, Integer> p = data.prevMonth(mese, anno);
                mese = p.first;
                anno = p.second;
                textViewDataMain.setText(mese + " " + anno);
                mostraPagamentiMese();
            }
        });

    }

    private void inserisciPagamento() {
        Intent intent = new Intent(MainActivity.this, InsPagamentoActivity.class);
        startActivity(intent);
    }
    /*
    @Override
    protected void onStart() {
        super.onStart();
        aggiornaLista();
        Log.d(TAG, "onStart: si aggiorna la lista!");
    }
    */
    @Override
    protected void onResume() {
        super.onResume();
        //aggiornaLista();
        //listaDatabase();
    }

    public void createItemList(){

        itemList = new ArrayList<>();
        /* ELEMENTI DI ESEMPIO
        itemList.add(new Item("350", "Enel", "Bonifico", "30/04/2020"));
        itemList.add(new Item("200", "Dima", "Assegno", "30/04/2020"));
        itemList.add(new Item("600", "Lanteri", "RiBa", "30/04/2020"));
        itemList.add(new Item("175", "Manzone", "Rimessa Diretta", "30/04/2020"));
        */
        //ATTIVARE QUESTO METODO IN FASE DI DEBUG PER VISUALIZZARE TUTTI I PAGAMENTI
        //metodoDiProva();
    }

    public void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.listPagamenti);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(this);
        mAdapter = new ItemAdapter(this, itemList, this);

        mRecyclerView.setLayoutManager(mlayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //AZIONARE SNACKBAR
        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeItem(this, itemAdapter));
        //itemTouchHelper.attachToRecyclerView(mRecyclerView);
        //setUpRecyclerView();
    }


    public List<Item> getItemList() {
        return itemList;
    }

    public void aggiornaLista(){
        Log.d(TAG, "aggiornaLista: Controllo se qualcosa è cambiato e controllo se la lista è vuota");
        mAdapter.notifyDataSetChanged();
        controllaListaVuota();
        //listaDatabase();
    }

    private void setUpRecyclerView(){
        //mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //itemAdapter = new ItemAdapter(this, itemList);
        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeItem(itemAdapter));
        //itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void getData(){
        //this.mese = "";
        mese = data.getDataMain();
        anno = data.getThisYear();

        textViewDataMain.setText(mese + " " + anno);
        Log.d(TAG, "getData: Ottengo i pagamenti del mese corrente andando in mostraPagamentiMese()");
        mostraPagamentiMese();

    }
    //METODO SEGUENTE FUNZIONANTE
    private void mostraPagamentiMese(){
        Log.d(TAG, "mostraPagamentiMese: mese corrente " + mese);
        int month = data.monthStringToNum(mese);
        month++;
        itemList.clear();
        //MOSTRA PAGAMENTI SOLO DEL MESE DESIDERATO
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Pagamenti")
                .whereEqualTo("mese", month)
                .whereEqualTo("anno", anno)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot d : list){
                                Log.d(TAG, "MostraPagamentiMese: " + d.getId() + " => " + d.getData());
                                Item item = d.toObject(Item.class);
                                item.setId(d.getId());
                                itemList.add(item);
                                Log.d(TAG, "MostraPagamentiMese: elemento aggiunto!");
                            }
                            Log.d(TAG, "MostraPagamentiMese: aggiorno l'intera lista che vale: " + itemList.size());
                            //mAdapter.notifyDataSetChanged();
                            aggiornaLista();
                        } else {
                            Log.d(TAG, "MostraPagamentiMese: non ho trovato pagamenti e aggiorno l'intera lista che vale: " + itemList.size());
                            //mAdapter.notifyDataSetChanged();
                            aggiornaLista();
                        }
                    }
                });
    }
    //METODO CHE SI AZIONA AD OGNI CLICK SUL PAGAMENTO (FUNZIONANTE)
    @Override
    public void onItemLongClick(final int position) {
        Log.d(TAG, "onItemClick: " + position);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cosa vuoi fare?")
                .setTitle("Impostazioni pagamento")
                .setPositiveButton("Cancella", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mostraConferma(position);
                        //deleteItem(position);
                    }
                });
        builder.setNeutralButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(MainActivity.this, "Operazione annullata", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Modifica", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                modificaPagamento(position);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    //METODO PER MODIFICARE IL PAGAMENTO
    private void modificaPagamento(int position) {
        Bundle extras = new Bundle();
        extras.putString("id", itemList.get(position).getId());
        extras.putString("importo", itemList.get(position).getImporto());
        extras.putString("ente", itemList.get(position).getEnte());
        extras.putString("modalita", itemList.get(position).getModalita());
        extras.putInt("giorno", itemList.get(position).getGiorno());
        extras.putInt("mese", itemList.get(position).getMese());
        extras.putInt("anno", itemList.get(position).getAnno());

        Intent intent = new Intent(MainActivity.this, ModificaActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }
    //METODO PER MOSTRARE IL DIALOG DI CONFERMA ALL'UTENTE PER LA CANCELLAZIONE DEL PAGAMENTO
    private void mostraConferma(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vuoi eliminare definitivamente questo pagamento?")
                .setTitle("Elimina pagamento")
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        //IN CASO AFFERMATIVO, CANCELLARE IL PAGAMENTO
                        deleteItem(position);
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
    //METODO PER CANCELLARE IL PAGAMENTO
    private void deleteItem(int position) {
        final String itemId = itemList.get(position).getId();
        final int currentPosition = position;
        firebaseFirestore.collection("Pagamenti").document(itemId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        itemList.remove(currentPosition);
                        mAdapter.notifyItemRemoved(currentPosition);
                        Toast.makeText(MainActivity.this, "Pagamento eliminato con successo!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Documento eliminato: " + itemId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Errore nella cancellazione del pagamento..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void controllaListaVuota () {
        Log.d(TAG, "controllaListaVuota: Controllo la lista se è vuota o meno");
        if(itemList.isEmpty()){
            mRecyclerView.setVisibility(View.INVISIBLE);
            listaVuota.setVisibility(View.VISIBLE);
            Log.d(TAG, "controllaListaVuota: Lista vuota quindi ho reso l'immagine visibile");
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            listaVuota.setVisibility(View.INVISIBLE);
            Log.d(TAG, "controllaListaVuota: Lista NON vuota quindi ho reso l'immagine invisibile");
        }
    }

    //METODO DI PROVA PER VISUALIZZARE TUTTI I PAGAMENTI
    private void metodoDiProva(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Pagamenti")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot d : list){
                                Log.d(TAG, d.getId() + " => " + d.getData());
                                Item item = d.toObject(Item.class);
                                item.setId(d.getId());
                                itemList.add(item);
                            }
                            aggiornaLista();
                        }
                    }
                });
    }

    //METODO SNACKBAR
    public void showUndoSnackbar() {
        View view = findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar.make(view, "Elemento eliminato!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Ripristina", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemAdapter.undoDelete();
            }
        });
        snackbar.show();
    }


}
