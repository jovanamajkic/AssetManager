package com.example.assetsmanager.ui.assets;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.assetsmanager.R;
import com.example.assetsmanager.async.AssetAsync;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Asset;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.util.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddAssetFragment extends Fragment {
    private AssetsManagerDatabase assetsManagerDatabase;
    private Asset asset;
    private TextInputEditText etName, etDescription, etBarcode, etPrice, etDate;
    private Spinner spinnerEmployee, spinnerLocation;
    private ImageView image;
    private Uri selectedImageUri;
    private File photoFile;
    private String imagePath;
    private Bitmap bitmap;
    private List<Employee> employees;
    private ArrayAdapter<String> employeeAdapter;
    private List<Location> locations;
    private ArrayAdapter<String> locationAdapter;
    private boolean scan = true;
    private boolean imageChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_add_asset, container, false);

        etName = root.findViewById(R.id.et_asset_name);
        etDescription = root.findViewById(R.id.et_description);
        etBarcode = root.findViewById(R.id.et_barcode);
        etPrice = root.findViewById(R.id.et_price);
        etDate = root.findViewById(R.id.et_date);
        spinnerEmployee = root.findViewById(R.id.spinner_employee);
        spinnerLocation = root.findViewById(R.id.spinner_location);
        MaterialButton btnSave = root.findViewById(R.id.btn_save_asset);
        ImageButton btnScan = root.findViewById(R.id.btn_scan_barcode);
        image = root.findViewById(R.id.image_asset);

        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());

        employees = new ArrayList<>();
        locations = new ArrayList<>();

        populateSpinners();

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan = false;
                showImagePickerDialog();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan = true;
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        if (getArguments() != null && getArguments().containsKey("asset")) {
            asset = (Asset) getArguments().getSerializable("asset");
            etName.setText(asset.getName());
            etDescription.setText(asset.getDescription());
            etBarcode.setText(String.valueOf(asset.getBarcode()));
            etDate.setText(format.format(asset.getCreationDate()));
            etPrice.setText(String.valueOf(asset.getPrice()));
            bitmap = BitmapFactory.decodeFile(asset.getImage());
            loadImage(asset.getImage(), false);
            btnSave.setText(R.string.asset_edit);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.asset_edit);
            }
        } else {
            asset = new Asset();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    if(imageChanged){
                        imagePath = saveImageToInternalStorage();
                        asset.setImage(imagePath);
                        if (imagePath == null)
                            Toast.makeText(requireContext(), R.string.img_error, Toast.LENGTH_SHORT).show();
                    }
                    String name = etName.getText().toString();
                    String description = etDescription.getText().toString();
                    String barcode = etBarcode.getText().toString();
                    String price = etPrice.getText().toString();
                    String dateString = etDate.getText().toString();
                    int employeeId = employees.get(spinnerEmployee.getSelectedItemPosition()).getId();
                    int locationId = locations.get(spinnerLocation.getSelectedItemPosition()).getId();

                    if (name.isEmpty() || description.isEmpty() || barcode.isEmpty() || price.isEmpty()
                        || dateString.isEmpty() || employeeId == 0 || locationId == 0 || (imageChanged && imagePath == null)) {
                        Toast.makeText(requireContext(), R.string.asset_error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Date date = null;
                    try {
                        date = format.parse(dateString);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    asset.setBarcode(Long.parseLong(barcode));
                    asset.setName(name);
                    asset.setDescription(description);
                    asset.setPrice(Double.parseDouble(price));
                    asset.setCreationDate(date);
                    asset.setEmployeeId(employeeId);
                    asset.setLocationId(locationId);

                    if (getArguments() != null && getArguments().containsKey("asset")) {
                        new AssetAsync.UpdateTask(AddAssetFragment.this, asset).execute();
                        Toast.makeText(requireContext(), R.string.asset_edit_msg, Toast.LENGTH_SHORT).show();
                    } else {
                        imagePath = saveImageToInternalStorage();
                        asset.setImage(imagePath);
                        new AssetAsync.InsertTask(AddAssetFragment.this, asset).execute();
                        Toast.makeText(requireContext(), R.string.asset_add_msg, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        return root;
    }

    private void populateSpinners() {
        new AssetAsync.RetrieveEmployeesTask(this).execute();
        new AssetAsync.RetrieveLocationsTask(this).execute();
    }

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if(scan)
                        startScan();
                    else
                        openCamera();
                } else {
                    Toast.makeText(getContext(), R.string.camera_perm, Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> requestGalleryPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(getContext(), R.string.gallery_perm, Toast.LENGTH_SHORT).show();
                }
            });

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.pick_option)
                .setItems(new String[]{getString(R.string.gallery), getString(R.string.camera)}, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                                == PackageManager.PERMISSION_GRANTED) {
                            openGallery();
                        } else {
                            requestGalleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    } else {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            openCamera();
                        } else {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                        }
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = null;
        try {
            photoFile = createImageFile();
            selectedImageUri = FileProvider.getUriForFile(requireContext(),
                    "com.example.assetsmanager.fileprovider", photoFile);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
            startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST_CODE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = requireContext().getFilesDir();
        return File.createTempFile("asset_image_" + System.currentTimeMillis(), ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(getArguments() != null && getArguments().containsKey("asset")){
            imageChanged = true;
        } else {
            imageChanged = false;
        }

        bitmap = null;
        if (resultCode == requireActivity().RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                loadImage(null, true);
            } else if (requestCode == Constants.CAMERA_REQUEST_CODE) {
                if (photoFile != null && photoFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    loadImage(photoFile.getAbsolutePath(), false);
                }
            }
        }
    }

    private void startScan() {
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
                }
            }
    );

    private String saveImageToInternalStorage() {
        String fileName = "asset_image_" + System.currentTimeMillis() + ".jpg";
        File directory = requireContext().getFilesDir();
        File imagePath = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(imagePath)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return imagePath.getAbsolutePath();
    }

    private void loadImage(String imagePath, boolean isUri){
        Uri uri;
        if(isUri){
            uri = selectedImageUri;
        } else {
            File file = new File(imagePath);
            uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);
        }
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_menu_gallery)
                .error(R.drawable.ic_error)
                .into(image);
    }


    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setEmployeeAdapter(ArrayAdapter<String> employeeAdapter) {
        this.employeeAdapter = employeeAdapter;
    }

    public ArrayAdapter<String> getEmployeeAdapter() {
        return employeeAdapter;
    }

    public void setLocationAdapter(ArrayAdapter<String> locationAdapter) {
        this.locationAdapter = locationAdapter;
    }

    public ArrayAdapter<String> getLocationAdapter() {
        return locationAdapter;
    }

    public Spinner getSpinnerEmployee() {
        return spinnerEmployee;
    }

    public Spinner getSpinnerLocation() {
        return spinnerLocation;
    }

    public Asset getAsset() {
        return asset;
    }
}