package com.example.assetsmanager.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.util.Constants;

import java.util.List;

@Dao
public interface EmployeeDao {
    @Insert
    long insertEmployee(Employee employee);
    @Update
    void updateEmployee(Employee employee);
    @Delete
    void deleteEmployee(Employee employee);
    @Delete
    void deleteEmployees(Employee... employees);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_EMPLOYEE)
    List<Employee> getEmployees();
    @Query("SELECT * FROM " + Constants.TABLE_NAME_EMPLOYEE + " WHERE id=:employeeId")
    Employee getById(int employeeId);
}
