package com.softworks.moldaunia.puntocolorpagamenti;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> mItemAdapters;
    private Context context;
    private CoordinatorLayout coordinatorLayout;
    private MainActivity mainActivity = new MainActivity();
    private String TAG = "ItemAdapter";
    private Item mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private OnItemListener mOnItemListener;

    public ItemAdapter(){}


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textImporto;
        public TextView textEnte;
        public TextView textModPagamento;
        public TextView textData;
        //public CoordinatorLayout coordinatorLayout;
        public OnItemListener onItemListener;


        public ItemViewHolder (View itemView, OnItemListener onItemListener) {
            super(itemView);
            textImporto = itemView.findViewById(R.id.textImporto);
            textEnte = itemView.findViewById(R.id.textEnte);
            textModPagamento = itemView.findViewById(R.id.textModalità);
            textData = itemView.findViewById(R.id.textData);
            //coordinatorLayout = itemView.findViewById(R.id.coordinator_layout);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }
        //AL CLICK DELL'ELEMENTO NELLA LISTA
        @Override
        public void onClick(View view) {
            onItemListener.onItemLongClick(getAdapterPosition());
        }
    }

    public ItemAdapter(Context context, List<Item> itemList, OnItemListener onItemListener) {
        this.context = context;
        mItemAdapters = itemList;
        this.mOnItemListener = onItemListener;
    }

    public Context getContext() {
        //context.getApplicationContext();
        return context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_view, parent, false);//parent.getContext
        ItemViewHolder ivh = new ItemViewHolder(v, mOnItemListener);
        this.context = parent.getContext();
        return ivh;
    }
    //VISUALIZZO IL PAGAMENTO PER OGNI ELEMENTO IN LISTA
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = mItemAdapters.get(position);
        /*
        final String importo = mItemAdapters.get(position).getImporto();
        final String ente = mItemAdapters.get(position).getEnte();
        final String modPag = mItemAdapters.get(position).getModPagamento();
        final String data = mItemAdapters.get(position).getData();

        Log.d(TAG, "ItemAdapter size: " + mItemAdapters.size());
        Log.d(TAG, "importo: " + importo);
        Log.d(TAG, "ente: " + ente);
        Log.d(TAG, "modPag: " + modPag);
        Log.d(TAG, "data: " + data);

         */

        holder.textImporto.setText(currentItem.getImporto() + "€");
        //holder.textData.setText(currentItem.getData());
        holder.textEnte.setText(currentItem.getEnte());
        holder.textModPagamento.setText(currentItem.getModalita());
        holder.textData.setText(currentItem.getGiorno() + "/" + currentItem.getMese() + "/" + currentItem.getAnno());

        Log.d(TAG, "ItemAdapter size: " + mItemAdapters.size());
        Log.d(TAG, "importo: " + currentItem.getImporto());
        Log.d(TAG, "ente: " + currentItem.getEnte());
        Log.d(TAG, "modalita: " + currentItem.getModalita());
        //Log.d(TAG, "data: " + currentItem.getData());
        Log.d(TAG, "giorno" + currentItem.getGiorno());
        Log.d(TAG, "mese" + currentItem.getMese());
        Log.d(TAG, "anno" + currentItem.getAnno());

        /*
        holder.textImporto.setText(importo);
        holder.textData.setText(data);
        holder.textEnte.setText(ente);
        holder.textModPagamento.setText(modPag);

         */
    }

    @Override
    public int getItemCount() {
        return mItemAdapters.size();
    }
    /*
    private void listaDatabase(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Pagamenti")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String importo = document.getString("Importo");

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    */

    public interface OnItemListener{
        void onItemLongClick(int position);
    }

    //METODI SNACKBAR

    public void deleteItem(int position){
        mRecentlyDeletedItem = mItemAdapters.get(position);
        mRecentlyDeletedItemPosition = position;
        mItemAdapters.remove(position);
        notifyItemRemoved(position);
        mainActivity.showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = mainActivity.findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar.make(view, "Elemento eliminato!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Ripristina", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoDelete();
            }
        });
        snackbar.show();
    }

    public void undoDelete() {
        mItemAdapters.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        Log.d(TAG, "Elemento riposizionato!");
        //Toast.makeText(context, "Elemento ripristinato!", Toast.LENGTH_SHORT).show();
    }

}
