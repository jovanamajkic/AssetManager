package com.example.assetsmanager.ui.locations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetsmanager.R;
import com.example.assetsmanager.async.LocationAsync;
import com.example.assetsmanager.databinding.FragmentLocationsBinding;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.recyclerview.LocationsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocationsFragment extends Fragment implements LocationsAdapter.OnLocationsItemClick {

    private FragmentLocationsBinding binding;
    private AssetsManagerDatabase assetsManagerDatabase;
    private LocationsAdapter adapter;
    private List<Location> locations;
    private List<Location> allLocations;
    private String cityQuery = "";
    private String latLongQuery = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLocationsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.recyclerLocations;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        locations = new ArrayList<>();
        allLocations = new ArrayList<>();
        adapter = new LocationsAdapter(locations, requireContext(), this);
        recyclerView.setAdapter(adapter);

        SearchView searchViewCity = binding.searchViewCity;
        searchViewCity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cityQuery = newText.toLowerCase();
                filter();
                return false;
            }
        });

        SearchView searchViewLatLong = binding.searchViewLatlong;
        searchViewLatLong.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                latLongQuery = newText.toLowerCase();
                filter();
                return false;
            }
        });

        displayList();
    }

    private void filter(){
        if(cityQuery.isEmpty() && latLongQuery.isEmpty()){
            locations = new ArrayList<>(allLocations);
        } else {
            locations = allLocations.stream()
                    .filter(l -> {
                        boolean cityMatch = l.getCity().toLowerCase().contains(cityQuery);
                        boolean latLongMatch = String.valueOf(l.getLatitude()).contains(latLongQuery)
                                || String.valueOf(l.getLongitude()).contains(latLongQuery);
                        return cityMatch && latLongMatch;
                    })
                    .collect(Collectors.toList());
        }

        adapter.filter(locations);
    }

    private void displayList(){
        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());
        new LocationAsync.RetrieveTask(this).execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }

    public LocationsAdapter getAdapter() {
        return adapter;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Location> getAllLocations() {
        return allLocations;
    }

    @Override
    public void onEditLocation(int pos) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("location", locations.get(pos));
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_locations_to_addLocationFragment, bundle);
    }

    @Override
    public void onDeleteLocation(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_alert_title)
                .setMessage(R.string.delete_alert_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    new LocationAsync.DeleteTask(LocationsFragment.this, locations.get(pos)).execute();
                })
                .setNegativeButton(R.string.No, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}