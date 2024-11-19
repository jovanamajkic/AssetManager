package com.example.assetsmanager.ui.employees;

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
import com.example.assetsmanager.async.EmployeeAsync;
import com.example.assetsmanager.databinding.FragmentEmployeesBinding;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.recyclerview.EmployeesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeesFragment extends Fragment implements EmployeesAdapter.OnEmployeesItemClick {

    private FragmentEmployeesBinding binding;
    private SearchView searchViewName;
    private SearchView searchViewEmail;
    private RecyclerView recyclerView;
    private AssetsManagerDatabase assetsManagerDatabase;
    private List<Employee> employees;
    private List<Employee> allEmployees;
    private EmployeesAdapter employeesAdapter;
    private int pos;
    private String nameQuery = "";
    private String emailQuery = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEmployeesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.recyclerEmployees;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        employees = new ArrayList<>();
        allEmployees = new ArrayList<>();
        employeesAdapter = new EmployeesAdapter(employees, requireContext(), this);
        recyclerView.setAdapter(employeesAdapter);

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

        searchViewEmail = binding.searchViewEmail;
        searchViewEmail.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                emailQuery = newText.toLowerCase();
                filter();
                return false;
            }
        });

        displayList();
    }

    private void filter(){
        if(nameQuery.isEmpty() && emailQuery.isEmpty()){
            employees = new ArrayList<>(allEmployees);
        } else {
            employees = allEmployees.stream()
                    .filter(e -> {
                        boolean nameMatch = e.getName().toLowerCase().contains(nameQuery);
                        boolean emailMatch = e.getEmail().toLowerCase().contains(emailQuery);
                        return nameMatch && emailMatch;
                    })
                    .collect(Collectors.toList());
        }

        employeesAdapter.filter(employees);
    }

    private void displayList(){
        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());
        new EmployeeAsync.RetrieveTask(this).execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Employee> getAllEmployees() {
        return allEmployees;
    }

    public EmployeesAdapter getEmployeesAdapter() {
        return employeesAdapter;
    }

    @Override
    public void onEditEmployee(int pos) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("employee", employees.get(pos));
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_employees_to_addEmployeeFragment, bundle);
    }

    @Override
    public void onDeleteEmployee(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_alert_title)
                .setMessage(R.string.delete_alert_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    new EmployeeAsync.DeleteTask(EmployeesFragment.this, employees.get(pos)).execute();
                })
                .setNegativeButton(R.string.No, (dialog, which) -> dialog.dismiss())
                .create()
                .show();

    }
}