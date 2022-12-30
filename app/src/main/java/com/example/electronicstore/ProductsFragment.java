package com.example.electronicstore;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {

    private Cursor cursor;
    private SQLiteDatabase db;
    ElectronicDatabaseHelper dbHelper;
    RecyclerView recyclerView;
    ArrayList<String> Iid, Icategory, Iname, Iprice, Idescription, IresourceId, Istar;
    CardViewAdapter cardViewAdapter;
    String SearchValue;
    SearchView searchView;
    Toolbar toolbar ;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu,MenuInflater  inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.bt_search);


         searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());

       SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("main activity", newText);
                cardViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchItem.setActionView(searchView);







         super.onCreateOptionsMenu(menu,inflater);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_products, container, false);

        dbHelper= new ElectronicDatabaseHelper(view.getContext());
        Iid = new ArrayList<>();
        Icategory = new ArrayList<>();
        Iname = new ArrayList<>();
        Iprice = new ArrayList<>();
        Idescription = new ArrayList<>();
        IresourceId = new ArrayList<>();
        Istar = new ArrayList<>();

        Bundle bundle = this.getArguments();
        SearchValue = bundle.getString("SearchValue");

        toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity  activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar();
      //  activity.getSupportActionBar().setTitle("aaa");





        DatabaseToArrays();

        recyclerView= view.findViewById(R.id.recyclerProducts);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        cardViewAdapter = new CardViewAdapter(Icategory, Iname, Iprice, Idescription, IresourceId, Istar);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardViewAdapter);

        cardViewAdapter.setListener(new CardViewAdapter.Listener() {
            public void onClick(int position) {
                Bundle bundle = new Bundle();

                bundle.putString("passName",Iname.get(position));
                bundle.putString("passPrice",Iprice.get(position));
                bundle.putString("passDescription",Idescription.get(position));
                bundle.putString("passResorceId",IresourceId.get(position));

                InformationFragment informationFragment= new InformationFragment();
                informationFragment.setArguments(bundle);

                FragmentTransaction ft= getParentFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,informationFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

            }

            @Override
            public void onUpdateClick(int position) {
                String tempId= Iid.get(position);
                String tempCategory= Icategory.get(position);
                String tempName= Iname.get(position);
                String tempPrice= Iprice.get(position);
                String tempDescription= Idescription.get(position);
                int tempIresourceId= Integer.parseInt(IresourceId.get(position));
                int tempStar= Integer.parseInt(Istar.get(position));

                dbHelper.UpdateOneRow(db, tempId, tempCategory, tempName, tempPrice, tempDescription, tempIresourceId, tempStar);
            }
        });

        return view;

    }


    void DatabaseToArrays(){
        cursor= dbHelper.SearchDueToCategory(SearchValue);
        if(cursor.getCount()==0){
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                Iid.add(cursor.getString(0));
                Icategory.add(cursor.getString(1));
                Iname.add(cursor.getString(2));
                Iprice.add(cursor.getString(3));
                Idescription.add(cursor.getString(4));
                IresourceId.add(cursor.getString(5));
                Istar.add(cursor.getString(6));

            }
        }
    }


}