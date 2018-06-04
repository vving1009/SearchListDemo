package com.example.administrator.searchlistdemo.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.searchlistdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DBWrapper {

    private static DBWrapper INSTANCE;
    private SQLiteDatabase db;
    private String[] companysArray;
    private String[] firstNamesArray;
    private String[] lastNamesArray;
    private String[] phonePrefixsArray;

    public synchronized static DBWrapper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBWrapper(context);
        }
        return INSTANCE;
    }

    private DBWrapper(Context context) {
        db = new DbHelper(context).getWritableDatabase();
        companysArray = context.getResources().getStringArray(R.array.company_items);
        firstNamesArray = context.getResources().getStringArray(R.array.first_name);
        lastNamesArray = context.getResources().getStringArray(R.array.last_name);
        phonePrefixsArray = context.getResources().getStringArray(R.array.phone_prefix);
        initSalesTable();
    }

    private void initSalesTable() {
        //db.execSQL("drop table if exists " + DbContract.Salesman.TABLE_NAME);
        //db.execSQL("delete from " + DbContract.Salesman.TABLE_NAME);
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            String company = companysArray[random.nextInt(companysArray.length)];
            StringBuilder name = new StringBuilder();
            name.append(lastNamesArray[random.nextInt(lastNamesArray.length)]);
            name.append(firstNamesArray[random.nextInt(firstNamesArray.length)]);
            if (random.nextBoolean()) {
                name.append(firstNamesArray[random.nextInt(firstNamesArray.length)]);
            }
            String phone = phonePrefixsArray[random.nextInt(phonePrefixsArray.length)] + (random.nextInt(90000000) + 10000000);
            db.execSQL("insert into " + DbContract.Salesman.TABLE_NAME + " (" +
                    DbContract.Salesman.COLUMN_COMPANY + "," +
                    DbContract.Salesman.COLUMN_NAME + "," +
                    DbContract.Salesman.COLUMN_WORK_ID +") values ('" + company + "','" +
                    name.toString() + "','" + phone + "')");
        }
    }

    public List<Salesman> queryPerson(String column, String value) {
        List<Salesman> salesmen = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + DbContract.Salesman.TABLE_NAME + " where " + column + " like ?" +
                "order by " + DbContract.Salesman.COLUMN_NAME, new String[]{"%" + value + "%"});
        while (cursor.moveToNext()) {
            Salesman sp = new Salesman();
            sp.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Salesman.COLUMN_COMPANY)));
            sp.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Salesman.COLUMN_NAME)));
            sp.setWorkId(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Salesman.COLUMN_WORK_ID)));
            salesmen.add(sp);
        }
        cursor.close();
        return salesmen;
    }

    public List<String> queryCompany() {
        List<String> companys = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + DbContract.Salesman.COLUMN_COMPANY +
                " from " + DbContract.Salesman.TABLE_NAME + " order by " + DbContract.Salesman.COLUMN_COMPANY, null);
        String company;
        while (cursor.moveToNext()) {
            company = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Salesman.COLUMN_COMPANY));
            if (!companys.contains(company)) {
                companys.add(company);
            }
        }
        cursor.close();
        return companys;
    }
}
