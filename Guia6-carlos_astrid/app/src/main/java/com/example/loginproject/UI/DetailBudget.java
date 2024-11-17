package com.example.loginproject.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.example.loginproject.Models.GastoDia;
import com.example.loginproject.Models.Presupuesto;
import com.example.loginproject.R;
import com.example.loginproject.UI.adapters.DailyExpenseRecycleAdapter;

import com.example.loginproject.UI.viewModels.DailyExpensesVM;
import com.example.loginproject.databinding.ActivityDetailBudgetBinding;

import java.util.ArrayList;
import java.util.Objects;

public class DetailBudget extends AppCompatActivity {

    private ActivityDetailBudgetBinding binding;
    private DailyExpensesVM viewModel;
    private DailyExpenseRecycleAdapter mainRecyclerAdapter;

    private Presupuesto receivedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el layout usando View Binding
        binding = ActivityDetailBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recuperar el objeto pasado por Intent
        Intent intent = getIntent();
        receivedObject = (Presupuesto) intent.getSerializableExtra("mainBudget");

        // Verificar si el objeto recibido es nulo
        if (receivedObject == null) {
            Toast.makeText(this, "Error: No se recibió el presupuesto", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar valores iniciales y listeners
        setDefaultValues();
        setupListeners();

        // Configurar el ViewModel
        viewModel = new ViewModelProvider(this).get(DailyExpensesVM.class);

        // Configurar el RecyclerView
        mainRecyclerAdapter = new DailyExpenseRecycleAdapter(new ArrayList<>());
        binding.rcvCompras.setAdapter(mainRecyclerAdapter);
        binding.rcvCompras.setHasFixedSize(true);

        // Observar cambios en los datos de gastos diarios
        viewModel.listenForExpensesChanges(receivedObject.getId());
        viewModel.getDailyExpensesLiveData().observe(this, budgets -> {
            mainRecyclerAdapter.setDataList(budgets);
            rebuildAmountAvailable();
        });

        // Configurar el botón de finalizar
        binding.imvFinalizar.setOnClickListener(v -> showFinalizeDialog());
    }

    private void setupListeners() {
        // Listener para actualizar el subtotal cuando cambien los valores
        binding.edtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.edtPrecio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Listener para guardar un gasto diario
        binding.btnGuardarDia.setOnClickListener(v -> saveDailyExpense());
    }

    private void saveDailyExpense() {
        try {
            double actualPrice = Double.parseDouble(binding.edtPrecio.getText().toString());
            int actualAmount = Integer.parseInt(binding.edtCantidad.getText().toString());

            GastoDia gastoDia = new GastoDia(
                    binding.edtArticulo.getText().toString(),
                    actualPrice,
                    actualAmount,
                    getSubtotal(actualPrice, actualAmount),
                    receivedObject.getId()
            );

            viewModel.addDailyExpense(gastoDia,
                    documentReference -> {
                        setDefaultValues();
                        rebuildAmountAvailable();
                        Toast.makeText(this, "Compra guardada correctamente", Toast.LENGTH_SHORT).show();
                    },
                    e -> Toast.makeText(this, "Error al guardar la compra", Toast.LENGTH_SHORT).show());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFinalizeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("¿Desea finalizar el presupuesto?")
                .setCancelable(false)
                .setPositiveButton("Confirmar", (dialog, id) -> viewModel.finalizeBudget(
                        receivedObject.getId(),
                        documentReference -> finish(),
                        e -> Toast.makeText(this, "Error al finalizar el presupuesto", Toast.LENGTH_SHORT).show()))
                .setNegativeButton("Cancelar", (dialog, id) -> {})
                .show();
    }

    private void setDefaultValues() {
        binding.txvPActual.setText("$" + receivedObject.getMonto());
        binding.txvTActual.setText("$0");

        binding.edtArticulo.setText(null);
        binding.edtArticulo.clearFocus();

        binding.edtPrecio.setText(null);
        binding.edtPrecio.clearFocus();

        binding.edtCantidad.setText(null);
        binding.edtCantidad.clearFocus();
    }

    private double getSubtotal(double price, int quantity) {
        return price * quantity;
    }

    private void updateTotal() {
        String quantityText = binding.edtCantidad.getText().toString();
        String priceText = binding.edtPrecio.getText().toString();

        if (!quantityText.isEmpty() && !priceText.isEmpty()) {
            double actualPrice = Double.parseDouble(priceText);
            int actualAmount = Integer.parseInt(quantityText);
            binding.txvTActual.setText("$" + getSubtotal(actualPrice, actualAmount));
        } else {
            binding.txvTActual.setText("$0");
        }
    }

    private void rebuildAmountAvailable() {
        double availableAmount = receivedObject.getMonto() - mainRecyclerAdapter.getActualSubTotalAll();
        binding.txvPActual.setText("$" + String.format("%.2f", availableAmount));
    }
}
