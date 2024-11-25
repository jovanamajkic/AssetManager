package com.example.assetsmanager.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetsmanager.R;
import com.example.assetsmanager.db.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.ViewHolder> {
    private List<Employee> employees;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnEmployeesItemClick onEmployeesItemClick;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        return new EmployeesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Employee employee = employees.get(position);
        holder.tvName.setText(employee.getName());
        holder.tvEmail.setText(employee.getEmail());
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvEmail;
        public ImageButton btnDelete;
        public ImageButton btnEdit;
        public View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            tvName = itemView.findViewById(R.id.tv_title);
            tvEmail = itemView.findViewById(R.id.tv_description);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEmployeesItemClick.onDeleteEmployee(getAdapterPosition());
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEmployeesItemClick.onEditEmployee(getAdapterPosition());
                }
            });
        }
    }

    public EmployeesAdapter(List<Employee> employees, Context context, OnEmployeesItemClick onEmployeesItemClick){
        this.employees = employees;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.onEmployeesItemClick = onEmployeesItemClick;
    }

    public interface OnEmployeesItemClick {
        void onEditEmployee(int pos);
        void onDeleteEmployee(int pos);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(List<Employee> employees){
        this.employees = employees;
        notifyDataSetChanged();
    }
}
