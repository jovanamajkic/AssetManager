package com.example.assetsmanager.ui.employees;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.assetsmanager.R;
import com.example.assetsmanager.async.EmployeeAsync;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Employee;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEmployeeFragment extends Fragment {
    private AssetsManagerDatabase assetsManagerDatabase;
    private Employee employee;
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private MaterialButton btnSaveEmployee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_employee, container, false);

        etName = root.findViewById(R.id.et_employee_name);
        etEmail = root.findViewById(R.id.et_employee_email);
        btnSaveEmployee = root.findViewById(R.id.btn_save_employee);

        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());

        if (getArguments() != null && getArguments().containsKey("employee")) {
            employee = (Employee) getArguments().getSerializable("employee");
            etName.setText(employee.getName());
            etEmail.setText(employee.getEmail());
            btnSaveEmployee.setText(R.string.employee_edit);
            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.employee_edit);
            }
        } else {
            employee = new Employee();
        }

        btnSaveEmployee.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                if (name.isEmpty()) {
                    ((TextInputLayout) root.findViewById(R.id.name_input_layout)).setError("Unesite ime");
                } else {
                    ((TextInputLayout) root.findViewById(R.id.name_input_layout)).setError(null);
                }

                if (email.isEmpty()) {
                    ((TextInputLayout) root.findViewById(R.id.email_input_layout)).setError("Unesite email");
                } else {
                    ((TextInputLayout) root.findViewById(R.id.email_input_layout)).setError(null);
                }

                if (!name.isEmpty() && !email.isEmpty()) {
                    employee.setName(name);
                    employee.setEmail(email);
                    if (getArguments() != null && getArguments().containsKey("employee")) {
                        new EmployeeAsync.UpdateTask(AddEmployeeFragment.this, employee).execute();
                        Toast.makeText(requireContext(), "Zaposleni je uspješno ažuriran", Toast.LENGTH_SHORT).show();
                    } else {
                        new EmployeeAsync.InsertTask(AddEmployeeFragment.this, employee).execute();
                        Toast.makeText(requireContext(), "Zaposleni je uspješno dodat", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return root;
    }

    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }

    public Employee getEmployee() {
        return employee;
    }
}