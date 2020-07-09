package com.softworks.moldaunia.puntocolorpagamenti;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.Pair;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class Data{

    private String TAG = "DATA";

    private Item item = new Item();

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener mDataSetListener;

    private int thisDay, thisMonth, thisYear;
    private String mese = "";

    //Ottengo la data odierna
    public String getDataMain(){

        calendar = Calendar.getInstance();
        thisYear = calendar.get(Calendar.YEAR);
        Log.d(TAG, "# thisYear : " + thisYear);

        thisMonth = calendar.get(Calendar.MONTH);
        Log.d(TAG, "@ thisMonth : " + thisMonth);

        thisDay = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "$ thisDay : " + thisDay);

        mese = monthIntToString(thisMonth);

        /*
        switch (thisMonth){
            case 0:
                mese = "Gennaio";
                break;
            case 1:
                mese = "Febbraio";
                break;
            case 2:
                mese = "Marzo";
                break;
            case 3:
                mese = "Aprile";
                break;
            case 4:
                mese = "Maggio";
                break;
            case 5:
                mese = "Giugno";
                break;
            case 6:
                mese = "Luglio";
                break;
            case 7:
                mese = "Agosto";
                break;
            case 8:
                mese = "Settembre";
                break;
            case 9:
                mese = "Ottobre";
                break;
            case 10:
                mese = "Novembre";
                break;
            case 11:
                mese = "Dicembre";
                break;
        }
        */
        return mese;
    }
    //METODO CHE MI RESTITUISCE IL MESE SUCCESSIVO
    public Pair<String, Integer> nextMonth(String month, int year){
        int mese = monthStringToNum(month);
        //int[] result = new int[2];
        Log.d(TAG, "data ad inizio metodo: " + mese + "/" + year);
        if(mese == 11){
            year++;
            mese = 0;
        } else {
            mese++;
        }
        Log.d(TAG, "data dopo blocco if: " + mese + "/" + year);

        String meseStringa = monthIntToString(mese);
        //result[0] = mese;
        //result[1] = year;
        Log.d(TAG, "data a fine metodo: " + mese + "/" + year);
        Log.d(TAG, "torna i seguenti valori: " + meseStringa + year);
        //return result;
        return new Pair<String, Integer>(meseStringa, year);
    }
    //METODO CHE MI RESTITUISCE IL MESE PRECEDENTE
    public Pair<String, Integer> prevMonth (String month, int year){
        int mese = monthStringToNum(month);

        if(mese == 0){
            year--;
            mese = 11;
        } else {
            mese--;
        }

        String meseStringa = monthIntToString(mese);

        return new Pair<String, Integer>(meseStringa, year);
    }
    //METODO CHE CONVERTE IL MESE DA STRINGA AD INTERO
    public int monthStringToNum (String month){
        int mese = 0;
        switch (month){
            case "Gennaio":
                mese = 0;
                break;
            case "Febbraio":
                mese = 1;
                break;
            case "Marzo":
                mese = 2;
                break;
            case "Aprile":
                mese = 3;
                break;
            case "Maggio":
                mese = 4;
                break;
            case "Giugno":
                mese = 5;
                break;
            case "Luglio":
                mese = 6;
                break;
            case "Agosto":
                mese = 7;
                break;
            case "Settembre":
                mese = 8;
                break;
            case "Ottobre":
                mese = 9;
                break;
            case "Novembre":
                mese = 10;
                break;
            case "Dicembre":
                mese = 11;
                break;
        }
        return mese;
    }
    //METODO CHE CONVERTE IL MESE DA INTERO A STRINGA
    private String monthIntToString (int month){
        String mese = "";
        Log.d(TAG, "valore di mese in monthIntToString: " + month);
        switch (month){
            case 0:
                mese = "Gennaio";
                break;
            case 1:
                mese = "Febbraio";
                break;
            case 2:
                mese = "Marzo";
                break;
            case 3:
                mese = "Aprile";
                break;
            case 4:
                mese = "Maggio";
                break;
            case 5:
                mese = "Giugno";
                break;
            case 6:
                mese = "Luglio";
                break;
            case 7:
                mese = "Agosto";
                break;
            case 8:
                mese = "Settembre";
                break;
            case 9:
                mese = "Ottobre";
                break;
            case 10:
                mese = "Novembre";
                break;
            case 11:
                mese = "Dicembre";
                break;
        }
        Log.d(TAG, "a fine metodo monthIntToString se mese è " + month + " allora siamo nel mese di: " + mese);
        return mese;
    }

    public void modificaData(Context context, final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int anno = calendar.get(Calendar.YEAR);
        int mese = calendar.get(Calendar.MONTH);
        int giorno = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "modificaData: data attuale: " + giorno + "/" + mese + "/" + anno);
        DatePickerDialog dialog = new DatePickerDialog(
                context,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mDataSetListener,
                anno, mese, giorno);
        Log.d(TAG, "modificaData: ho raccolto la data: " + giorno + "/" + mese + "/" + anno);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();


        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anno, int mese, int giorno) {
                mese++;
                Log.d(TAG, "onDateSet: Questa è la data modificata: " + anno + "/" + mese + "/" + giorno);
                textView.setText(giorno + "/" + mese + "/" + anno);
                item.setGiorno(giorno);
                item.setMese(mese);
                item.setAnno(anno);
            }
        };

    }

    public int getThisYear() {
        return thisYear;
    }


}
