package com.example.assetsmanager.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assetsmanager.R;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.ui.assets.AssetDetailsFragment;
import com.example.assetsmanager.ui.employees.AddEmployeeFragment;
import com.example.assetsmanager.ui.employees.EmployeesFragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class EmployeeAsync {
    public static class RetrieveTask extends AsyncTask<Void, Void, List<Employee>> {
        private WeakReference<EmployeesFragment> weakReference;

        public RetrieveTask(EmployeesFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Employee> doInBackground(Void... voids) {
            if (weakReference.get() != null)
                return weakReference.get().getAssetsManagerDatabase().getEmployeeDao().getEmployees();
            else
                return null;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<Employee> employees) {
            if (employees != null && !employees.isEmpty()) {
                weakReference.get().getAllEmployees().clear();
                weakReference.get().getEmployees().clear();
                weakReference.get().getAllEmployees().addAll(employees);
                weakReference.get().getEmployees().addAll(employees);
                weakReference.get().getEmployeesAdapter().notifyDataSetChanged();
            }
        }
    }

    public static class InsertTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddEmployeeFragment> weakReference;
        private Employee employee;

        public InsertTask(AddEmployeeFragment fragment, Employee employee){
            weakReference = new WeakReference<>(fragment);
            this.employee = employee;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            long id = weakReference.get().getAssetsManagerDatabase().getEmployeeDao().insertEmployee(employee);
            employee.setId((int) id);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                NavController navController = Navigation.findNavController(weakReference.get().requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_addEmployeeFragment_to_nav_employees);
            }
        }
    }

    public static class DeleteTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<EmployeesFragment> weakReference;
        private Employee employee;
        private int pos;

        public DeleteTask(EmployeesFragment fragment, Employee employee){
            weakReference = new WeakReference<>(fragment);
            this.employee = employee;
            this.pos = weakReference.get().getEmployees().indexOf(employee);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getEmployeeDao().deleteEmployee(employee);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                weakReference.get().getEmployees().remove(employee);
                weakReference.get().getAllEmployees().remove(employee);
                weakReference.get().getEmployeesAdapter().notifyItemRemoved(pos);
            }
        }
    }

    public static class UpdateTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddEmployeeFragment> weakReference;
        private Employee employee;

        public UpdateTask(AddEmployeeFragment fragment, Employee employee){
            weakReference = new WeakReference<>(fragment);
            this.employee = employee;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getEmployeeDao().updateEmployee(employee);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                NavController navController = Navigation.findNavController(weakReference.get().requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_addEmployeeFragment_to_nav_employees);
            }
        }
    }

    public static class GetByIdTask extends AsyncTask<Void, Void, Employee>{
        private WeakReference<AssetDetailsFragment> weakReference;
        private int id;

        public GetByIdTask(AssetDetailsFragment fragment, int id){
            weakReference = new WeakReference<>(fragment);
            this.id = id;
        }

        @Override
        protected Employee doInBackground(Void... voids) {
            return weakReference.get().getAssetsManagerDatabase().getEmployeeDao().getById(id);
        }

        @Override
        protected void onPostExecute(Employee employee) {
            if(employee != null) {
                weakReference.get().setEmployee(employee);
                weakReference.get().getTvEmployee().setText(employee.getName());
            }
        }
    }
}
