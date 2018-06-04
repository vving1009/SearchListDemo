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
import android.widget.TextView;

import com.example.administrator.searchlistdemo.persistence.DBWrapper;
import com.example.administrator.searchlistdemo.persistence.DbContract;
import com.example.administrator.searchlistdemo.persistence.Salesman;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    SearchView mSearchView;
    DBWrapper mDbWrapper;
    RecyclerView mCompanyList;
    RecyclerView mPersonList;
    CompanyListAdapter mCompanyListAdapter;
    PersonListAdapter mPersonListAdapter;
    boolean listChanged = false;

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
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initList();
        initSearchView();
        mCompanyList.requestFocus();
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Salesman> list;
                if (newText.equals("")) {
                    initListAdapter();
                    return true;
                }
                mPersonListAdapter.setBlankContent();
                if ((list = mDbWrapper.queryPerson(DbContract.Salesman.COLUMN_COMPANY, newText)) != null && list.size() != 0) {
                    mCompanyListAdapter.setItemSelected(list.get(0).getCompany());
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else if ((list = mDbWrapper.queryPerson(DbContract.Salesman.COLUMN_NAME, newText)) != null && list.size() != 0) {
                    mCompanyListAdapter.setNoneSelected();
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else if ((list = mDbWrapper.queryPerson(DbContract.Salesman.COLUMN_WORK_ID, newText)) != null && list.size() != 0) {
                    mCompanyListAdapter.setNoneSelected();
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                    return true;
                } else {
                    initListAdapter();
                }
                return false;
            }
        });
        //动态显示删除✖键
        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(true);
    }

    private void initList() {
        mCompanyListAdapter = new CompanyListAdapter(mDbWrapper.queryCompany());
        mPersonListAdapter = new PersonListAdapter(new ArrayList<Salesman>());
        mCompanyList.setLayoutManager(new LinearLayoutManager(this));
        mPersonList.setLayoutManager(new LinearLayoutManager(this));
        mCompanyList.setAdapter(mCompanyListAdapter);
        mPersonList.setAdapter(mPersonListAdapter);
        /*mCompanyList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            private int mLastY;

            @Override
            public boolean onInterceptTouchEvent(@NonNull final RecyclerView rv, @NonNull final
            MotionEvent e) {
                Log.d("debug", "LEFT: onInterceptTouchEvent");

                final Boolean ret = rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
                if (!ret) {
                    onTouchEvent(rv, e);
                }
                return Boolean.FALSE;
            }

            @Override
            public void onTouchEvent(@NonNull final RecyclerView rv, @NonNull final MotionEvent e) {
                Log.d("debug", "LEFT: onTouchEvent");

                final int action;
                if ((action = e.getAction()) == MotionEvent.ACTION_DOWN && mPersonList
                        .getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    mLastY = rv.getScrollY();
                    rv.addOnScrollListener(mLeftOSL);
                }
                else {
                    if (action == MotionEvent.ACTION_UP && rv.getScrollY() == mLastY) {
                        rv.removeOnScrollListener(mLeftOSL);
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {
                Log.d("debug", "LEFT: onRequestDisallowInterceptTouchEvent");
            }
        });
        mPersonList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            private int mLastY;

            @Override
            public boolean onInterceptTouchEvent(@NonNull final RecyclerView rv, @NonNull final
            MotionEvent e) {
                Log.d("debug", "RIGHT: onInterceptTouchEvent");

                final Boolean ret = rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
                if (!ret) {
                    onTouchEvent(rv, e);
                }
                return Boolean.FALSE;
            }

            @Override
            public void onTouchEvent(@NonNull final RecyclerView rv, @NonNull final MotionEvent e) {
                Log.d("debug", "RIGHT: onTouchEvent");

                final int action;
                if ((action = e.getAction()) == MotionEvent.ACTION_DOWN && mCompanyList
                        .getScrollState
                                () == RecyclerView.SCROLL_STATE_IDLE) {
                    mLastY = rv.getScrollY();
                    rv.addOnScrollListener(mRightOSL);
                }
                else {
                    if (action == MotionEvent.ACTION_UP && rv.getScrollY() == mLastY) {
                        rv.removeOnScrollListener(mRightOSL);
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {
                Log.d("debug", "RIGHT: onRequestDisallowInterceptTouchEvent");
            }
        });*/
    }


    /*private final RecyclerView.OnScrollListener mLeftOSL = new SelfRemovingOnScrollListener() {
        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mCompanyList.scrollBy(dx, dy);
        }
    }, mRightOSL = new SelfRemovingOnScrollListener() {

        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mPersonList.scrollBy(dx, dy);
        }
    };

    public class SelfRemovingOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public final void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                recyclerView.removeOnScrollListener(this);
            }
        }
    }*/

    private void initListAdapter() {
        mCompanyListAdapter.setNoneSelected();
        mPersonListAdapter.setBlankContent();
        listChanged = false;
    }

    private class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder> {

        private List<String> companys;

        private int selectPos = -1;

        CompanyListAdapter(List<String> companys) {
            this.companys = companys;
        }

        @NonNull
        @Override
        public CompanyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.salescompany_list_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull CompanyListAdapter.ViewHolder holder, final int position) {
            holder.text.setText(companys.get(position));
            if (position == selectPos) {
                holder.rootView.setBackgroundColor(getColor(android.R.color.white));
            } else {
                holder.rootView.setBackgroundColor(getColor(R.color.list_item_bg));
            }
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPos = position;
                    notifyDataSetChanged();
                    List<Salesman> list = mDbWrapper.queryPerson(DbContract.Salesman.COLUMN_COMPANY, companys.get(position));
                    mPersonListAdapter.notifyListChange(list);
                    listChanged = true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return companys.size();
        }

        public void setItemSelected(String company) {
            selectPos = companys.indexOf(company);
            notifyDataSetChanged();
        }

        public void setNoneSelected() {
            selectPos = -1;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView text;
            View rootView;

            ViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                rootView = itemView.findViewById(R.id.root_view);
            }
        }
    }

    private class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.ViewHolder> {

        private List<Salesman> salesmen;

        private int selectPos = -1;

        PersonListAdapter(List<Salesman> persons) {
            salesmen = persons;
        }

        @NonNull
        @Override
        public PersonListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.salesman_list_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull PersonListAdapter.ViewHolder holder, final int position) {
            holder.name.setText(salesmen.get(position).getName());
            holder.phone.setText(salesmen.get(position).getWorkId());
            if (position == selectPos) {
                holder.name.setTextColor(getColor(R.color.list_text_selected));
                holder.phone.setTextColor(getColor(R.color.list_text_selected));
            } else {
                holder.name.setTextColor(getColor(R.color.list_text));
                holder.phone.setTextColor(getColor(R.color.list_text));
            }
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPos = position;
                    notifyDataSetChanged();
                    listChanged = true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return salesmen.size();
        }

        public void notifyListChange(List<Salesman> list) {
            Log.d(TAG, "PersonListAdapter notifyListChange: ");
            salesmen = list;
            notifyDataSetChanged();
        }

        public void setBlankContent() {
            selectPos = -1;
            salesmen.clear();
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView id;
            TextView name;
            TextView phone;
            View rootView;

            ViewHolder(View itemView) {
                super(itemView);
                id = itemView.findViewById(R.id.id);
                name = itemView.findViewById(R.id.name);
                phone = itemView.findViewById(R.id.phone);
                rootView = itemView.findViewById(R.id.root_view);
            }
        }

    }


    @Override
    public void onBackPressed() {
        if (listChanged) {
            initListAdapter();
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
