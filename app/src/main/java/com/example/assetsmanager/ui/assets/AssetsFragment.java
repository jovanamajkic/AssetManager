package com.example.assetsmanager.ui.assets;

import android.os.Bundle;
import android.util.Log;
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
import com.example.assetsmanager.async.AssetAsync;
import com.example.assetsmanager.databinding.FragmentAssetsBinding;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Asset;
import com.example.assetsmanager.recyclerview.AssetsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssetsFragment extends Fragment implements AssetsAdapter.OnAssetsItemClick {
    private FragmentAssetsBinding binding;
    private SearchView searchViewName;
    private SearchView searchViewBarcode;
    private RecyclerView recyclerView;
    private AssetsManagerDatabase assetsManagerDatabase;
    private List<Asset> assets;
    private List<Asset> allAssets;
    private AssetsAdapter assetsAdapter;
    private int pos;
    private String nameQuery = "";
    private String barcodeQuery = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAssetsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.recyclerAssets;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        assets = new ArrayList<>();
        allAssets = new ArrayList<>();
        assetsAdapter = new AssetsAdapter(assets, requireContext(), this);
        recyclerView.setAdapter(assetsAdapter);

        searchViewName = binding.searchViewName;
        searchViewName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                nameQuery = newText.toLowerCase();
                filter();
                return false;
            }
        });

        searchViewBarcode = binding.searchViewBarcode;
        searchViewBarcode.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                barcodeQuery = newText.toLowerCase();
                filter();
                return false;
            }
        });

        displayList();
    }

    private void displayList(){
        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());
        new AssetAsync.RetrieveTask(this).execute();
    }

    private void filter(){
        if(nameQuery.isEmpty() && barcodeQuery.isEmpty()){
            assets = new ArrayList<>(allAssets);
        } else if(!nameQuery.isEmpty() && barcodeQuery.isEmpty()) {
            assets = allAssets.stream()
                    .filter(a -> a.getName().toLowerCase().contains(nameQuery))
                    .collect(Collectors.toList());
        } else {
            assets = allAssets.stream()
                    .filter(a -> {
                        boolean nameMatch = a.getName().toLowerCase().contains(nameQuery);
                        boolean barcodeMatch = String.valueOf(a.getBarcode()).contains(barcodeQuery);
                        return nameMatch && barcodeMatch;
                    })
                    .collect(Collectors.toList());
        }

        assetsAdapter.filter(assets);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAssetClick(int pos) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("asset", assets.get(pos));
        Log.d("AssetDetails", "asset: " + (assets.get(pos) == null));
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_assets_to_assetDetailsFragment, bundle);
    }

    @Override
    public void onEditAsset(int pos) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("asset", assets.get(pos));
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_assets_to_addAssetFragment, bundle);
    }

    @Override
    public void onDeleteAsset(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_alert_title)
                .setMessage(R.string.delete_alert_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    new AssetAsync.DeleteTask(AssetsFragment.this, assets.get(pos)).execute();
                })
                .setNegativeButton(R.string.No, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public AssetsAdapter getAssetsAdapter() {
        return assetsAdapter;
    }

    public List<Asset> getAllAssets() {
        return allAssets;
    }
}