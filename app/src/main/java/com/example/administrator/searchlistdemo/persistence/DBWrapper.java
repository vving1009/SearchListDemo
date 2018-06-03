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
    private Context mContext;
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private String[] companys;
    private String[] firstNames;
    private String[] lastNames;
    private String[] phonePrefixs;

    public synchronized static DBWrapper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBWrapper(context);
        }
        return INSTANCE;
    }

    private DBWrapper(Context context) {
        mContext = context;
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        companys = mContext.getResources().getStringArray(R.array.company_items);
        firstNames = mContext.getResources().getStringArray(R.array.first_name);
        lastNames = mContext.getResources().getStringArray(R.array.last_name);
        phonePrefixs = mContext.getResources().getStringArray(R.array.phone_prefix);
        initSalesTable();
    }

    private void initSalesTable() {
        //db.execSQL("drop table if exists salesperson");
        db.execSQL("delete from salesperson");
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            String company = companys[random.nextInt(companys.length)];
            StringBuilder name = new StringBuilder();
            name.append(lastNames[random.nextInt(lastNames.length)]);
            name.append(firstNames[random.nextInt(firstNames.length)]);
            if (random.nextBoolean()) {
                name.append(firstNames[random.nextInt(firstNames.length)]);
            }
            String phone = phonePrefixs[random.nextInt(phonePrefixs.length)] + (random.nextInt(90000000) + 10000000);
            db.execSQL("insert into salesperson (company,name,phone) values ('" + company + "','" + name.toString() + "','" + phone + "')");
        }
    }

    public List<SalePerson> queryAllPerson() {
        List<SalePerson> salePersons = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from salesperson order by company,name asc", null);
        while (cursor.moveToNext()) {
            SalePerson sp = new SalePerson();
            sp.setCompany(cursor.getString(cursor.getColumnIndexOrThrow("company")));
            sp.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            sp.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            salePersons.add(sp);
        }
        cursor.close();
        return salePersons;
    }

    public List<SalePerson> queryPerson(String column, String value) {
        List<SalePerson> salePersons = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from salesperson where " + column + " like ?" +
                "order by name asc", new String[]{"%" + value + "%"});
        while (cursor.moveToNext()) {
            SalePerson sp = new SalePerson();
            sp.setCompany(cursor.getString(cursor.getColumnIndexOrThrow("company")));
            sp.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            sp.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            salePersons.add(sp);
        }
        cursor.close();
        return salePersons;
    }
}
