package com.example.administrator.searchlistdemo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.searchlistdemo.persistence.DBWrapper;
import com.example.administrator.searchlistdemo.persistence.DbContract;
import com.example.administrator.searchlistdemo.persistence.SalePerson;

import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final int COMPANY_ADAPTER = 101;
    private final int PERSON_ADAPTER = 102;
    SearchView mSearchView;
    DBWrapper mDbWrapper;
    RecyclerView mCompanyList;
    RecyclerView mPersonList;
    ListAdapter mCompanyListAdapter, mPersonListAdapter;
    List<SalePerson> mSalePersons;
    boolean listChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        mDbWrapper = DBWrapper.getInstance(this);
        mSearchView = findViewById(R.id.search_view);
        mCompanyList = findViewById(R.id.company_list);
        mPersonList = findViewById(R.id.person_list);
        initSearchView();
        initList();
    }

    private void initSearchView() {
        Log.d(TAG, "initSearchView: ");
        //int id = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        textView.setTextColor(Color.BLACK);//字体颜色
        textView.setTextSize(14);//字体、提示字体大小
        textView.setHintTextColor(Color.GRAY);//提示字体颜色
        textView.setGravity(Gravity.CENTER_VERTICAL);

        SpannableString spanText = new SpannableString("请输入分公司名、姓名、手机号");
        // 设置字体大小
        spanText.setSpan(new AbsoluteSizeSpan(14, true), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // 设置字体颜色
        /*spanText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);*/
        mSearchView.setQueryHint(spanText);

        try {
            Class<?> argClass = mSearchView.getClass();
            Field searchPlate = argClass.getDeclaredField("mSearchPlate");
            searchPlate.setAccessible(true);
            View view = (View) searchPlate.get(mSearchView);
            view.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: ");
                List<SalePerson> list;
                if ((list = mDbWrapper.queryPerson(DbContract.Salesperson.COLUMN_NAME_COMPANY, query)) != null && list.size() != 0) {
                    mCompanyListAdapter.notifyListChange(list);
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else if ((list = mDbWrapper.queryPerson(DbContract.Salesperson.COLUMN_NAME_NAME, query)) != null && list.size() != 0) {
                    mCompanyListAdapter.notifyListChange(list);
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else if ((list = mDbWrapper.queryPerson(DbContract.Salesperson.COLUMN_NAME_PHONE, query)) != null && list.size() != 0) {
                    mCompanyListAdapter.notifyListChange(list);
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else if ((list = mDbWrapper.queryAllPerson()) != null && list.size() != 0) {
                    mCompanyListAdapter.notifyListChange(list);
                    mPersonListAdapter.notifyListChange(list);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: ");
                List<SalePerson> list;
                if ((list = mDbWrapper.queryPerson(DbContract.Salesperson.COLUMN_NAME_COMPANY, newText)) != null && list.size() != 0) {
                    mCompanyListAdapter.notifyListChange(list);
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else if ((list = mDbWrapper.queryPerson(DbContract.Salesperson.COLUMN_NAME_NAME, newText)) != null && list.size() != 0) {
                    mCompanyListAdapter.notifyListChange(list);
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else if ((list = mDbWrapper.queryPerson(DbContract.Salesperson.COLUMN_NAME_PHONE, newText)) != null && list.size() != 0) {
                    mCompanyListAdapter.notifyListChange(list);
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else if ((list = mDbWrapper.queryAllPerson()) != null && list.size() != 0) {
                    mCompanyListAdapter.notifyListChange(list);
                    mPersonListAdapter.notifyListChange(list);
                    return true;
                }
                return false;
            }
        });
    }

    private void initList() {
        Log.d(TAG, "initList: ");
        mSalePersons = mDbWrapper.queryAllPerson();
        mCompanyListAdapter = new ListAdapter(mSalePersons, COMPANY_ADAPTER);
        mPersonListAdapter = new ListAdapter(mSalePersons, PERSON_ADAPTER);
        mCompanyList.setLayoutManager(new LinearLayoutManager(this));
        mPersonList.setLayoutManager(new LinearLayoutManager(this));
        mCompanyList.setAdapter(mCompanyListAdapter);
        mPersonList.setAdapter(mPersonListAdapter);
    }

    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        private List<SalePerson> salePersons;
        private int mode;

        ListAdapter(List<SalePerson> persons, int mode) {
            this.mode = mode;
            salePersons = persons;
        }

        @NonNull
        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: " + mode);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, final int position) {
            Log.d(TAG, "onBindViewHolder: " + mode);
            switch (mode) {
                case COMPANY_ADAPTER:
                    holder.text.setText(salePersons.get(position).getCompany());
                    holder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listChanged = true;
                            List<SalePerson> list = mDbWrapper.queryPerson(DbContract.Salesperson.COLUMN_NAME_COMPANY, salePersons.get(position).getCompany());
                            notifyListChange(list);
                            mPersonListAdapter.notifyListChange(list);
                        }
                    });
                    break;
                case PERSON_ADAPTER:
                    holder.text.setText(salePersons.get(position).getName() + "(" + salePersons.get(position).getPhone() + ")");
                    holder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listChanged = true;
                            List<SalePerson> list = mDbWrapper.queryPerson(DbContract.Salesperson.COLUMN_NAME_NAME, salePersons.get(position).getName());
                            notifyListChange(list);
                            mCompanyListAdapter.notifyListChange(list);
                        }
                    });
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return salePersons.size();
        }

        void notifyListChange(List<SalePerson> list) {
            salePersons = list;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView text;
            ImageView image;
            View item;

            public ViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                image = itemView.findViewById(R.id.image);
                item = itemView.findViewById(R.id.item);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (listChanged) {
            List<SalePerson> list = mDbWrapper.queryAllPerson();
            mCompanyListAdapter.notifyListChange(list);
            mPersonListAdapter.notifyListChange(list);
            listChanged = false;
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
