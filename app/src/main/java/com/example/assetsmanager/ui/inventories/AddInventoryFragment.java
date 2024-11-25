package com.example.assetsmanager.ui.inventories;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.assetsmanager.R;
import com.example.assetsmanager.async.AssetAsync;
import com.example.assetsmanager.async.InventoryAsync;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Asset;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Inventory;
import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.recyclerview.InventoryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;


public class AddInventoryFragment extends Fragment {
    private AssetsManagerDatabase assetsManagerDatabase;
    private Inventory inventory;
    private Asset asset;
    private Employee currentEmployee;
    private Location currentLocation;
    private List<Employee> employees;
    private List<Location> locations;
    private TextInputEditText etBarcode, etCurrEmployee, etCurrLocation;
    private Spinner spinnerLocation, spinnerEmployee;
    private ToggleButton toggleBtnLocation, toggleBtnEmployee;
    private ArrayAdapter<String> employeeAdapter;
    private ArrayAdapter<String> locationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_inventory, container, false);

        etBarcode = root.findViewById(R.id.et_barcode_inventory);
        etCurrEmployee = root.findViewById(R.id.et_curr_emp);
        etCurrLocation = root.findViewById(R.id.et_curr_loc);
        spinnerEmployee = root.findViewById(R.id.spinner_new_employee);
        spinnerLocation = root.findViewById(R.id.spinner_new_location);
        toggleBtnEmployee = root.findViewById(R.id.toggleButton_employee);
        toggleBtnLocation = root.findViewById(R.id.toggleButton_location);
        ImageButton btnScan = root.findViewById(R.id.btn_scan_inventory);
        ImageButton btnFind = root.findViewById(R.id.btn_search_barcode);
        MaterialButton btnSaveInventory = root.findViewById(R.id.btn_save_inventory);

        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());

        employees = new ArrayList<>();
        locations = new ArrayList<>();

        populateSpinners();

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBarcode.getText() != null){
                    long barcode = Long.parseLong(etBarcode.getText().toString());
                    new AssetAsync.GetByIdTask(AddInventoryFragment.this, barcode).execute();
                }
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });

        toggleBtnLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    spinnerLocation.setVisibility(View.VISIBLE);
                else
                    spinnerLocation.setVisibility(View.GONE);
            }
        });

        toggleBtnEmployee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    spinnerEmployee.setVisibility(View.VISIBLE);
                else
                    spinnerEmployee.setVisibility(View.GONE);
            }
        });

        InventoryItem item = null;

        if (getArguments() != null && getArguments().containsKey("inventory")) {
            item = (InventoryItem) getArguments().getSerializable("inventory");
            inventory = item.getInventory();
            etBarcode.setText(String.valueOf(inventory.getBarcode()));
            etCurrEmployee.setText(item.getCurrEmployee());
            if(item.getCurrEmployee().equals(item.getNewEmployee())){
                toggleBtnEmployee.setChecked(false);
                spinnerEmployee.setVisibility(View.GONE);
            } else {
                toggleBtnEmployee.setChecked(true);
                spinnerEmployee.setVisibility(View.VISIBLE);
            }
            etCurrLocation.setText(item.getCurrLocation());
            if(item.getCurrLocation().equals(item.getNewLocation())){
                toggleBtnLocation.setChecked(false);
                spinnerLocation.setVisibility(View.GONE);
            } else {
                toggleBtnLocation.setChecked(true);
                spinnerLocation.setVisibility(View.VISIBLE);
            }
            btnSaveInventory.setText(R.string.inventory_edit);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.inventory_edit);
            }
        } else {
            inventory = new Inventory();
        }

        btnSaveInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = etBarcode.getText().toString();
                int newEmployeeId = -1;
                int newLocationId = -1;
                if(spinnerEmployee.getVisibility() == View.VISIBLE)
                    newEmployeeId = employees.get(spinnerEmployee.getSelectedItemPosition()).getId();

                if(spinnerLocation.getVisibility() == View.VISIBLE)
                    newLocationId = locations.get(spinnerLocation.getSelectedItemPosition()).getId();

                if (barcode.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.inventory_error, Toast.LENGTH_SHORT).show();
                } else {
                    inventory.setBarcode(Long.parseLong(barcode));
                    if(currentEmployee != null)
                        inventory.setCurrEmployeeId(currentEmployee.getId());
                    if(currentLocation != null)
                        inventory.setCurrLocationId(currentLocation.getId());

                    if(newEmployeeId != -1) {
                        inventory.setNewEmployeeId(newEmployeeId);
                    } else if(currentEmployee != null) {
                        inventory.setNewEmployeeId(currentEmployee.getId());
                    } else {
                        inventory.setNewEmployeeId(inventory.getCurrEmployeeId());
                    }

                    if(newLocationId != -1) {
                        inventory.setNewLocationId(newLocationId);
                    } else if (currentLocation != null) {
                        inventory.setNewLocationId(currentLocation.getId());
                    } else {
                        inventory.setNewLocationId(inventory.getCurrLocationId());
                    }

                    if (getArguments() != null && getArguments().containsKey("inventory")) {
                        new InventoryAsync.UpdateTask(AddInventoryFragment.this, inventory).execute();
                        Toast.makeText(requireContext(), R.string.inventory_edit_msg, Toast.LENGTH_SHORT).show();
                    } else {
                        new InventoryAsync.InsertTask(AddInventoryFragment.this, inventory).execute();
                        Toast.makeText(requireContext(), R.string.inventory_add_msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return root;
    }

    private void populateSpinners() {
        new InventoryAsync.RetrieveEmployeesTask(this).execute();
        new InventoryAsync.RetrieveLocationsTask(this).execute();
    }

    private void startScan(){
        ScanOptions options = new ScanOptions();
        options.setPrompt(getString(R.string.qr_scanner));
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureActivity.class);
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if(result.getContents() != null) {
                    etBarcode.setText(result.getContents());
                    new AssetAsync.GetByIdTask(this, Long.parseLong(result.getContents())).execute();
                }
            }
    );

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startScan();
                } else {
                    Toast.makeText(getContext(), R.string.camera_perm, Toast.LENGTH_SHORT).show();
                }
            });

    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Spinner getSpinnerLocation() {
        return spinnerLocation;
    }

    public Spinner getSpinnerEmployee() {
        return spinnerEmployee;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public ArrayAdapter<String> getEmployeeAdapter() {
        return employeeAdapter;
    }

    public void setEmployeeAdapter(ArrayAdapter<String> employeeAdapter) {
        this.employeeAdapter = employeeAdapter;
    }

    public ArrayAdapter<String> getLocationAdapter() {
        return locationAdapter;
    }

    public void setLocationAdapter(ArrayAdapter<String> locationAdapter) {
        this.locationAdapter = locationAdapter;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public TextInputEditText getEtCurrEmployee() {
        return etCurrEmployee;
    }

    public TextInputEditText getEtCurrLocation() {
        return etCurrLocation;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public ToggleButton getToggleBtnLocation() {
        return toggleBtnLocation;
    }

    public ToggleButton getToggleBtnEmployee() {
        return toggleBtnEmployee;
    }
}