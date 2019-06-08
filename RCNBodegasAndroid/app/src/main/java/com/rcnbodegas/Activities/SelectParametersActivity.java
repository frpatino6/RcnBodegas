package com.rcnbodegas.Activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.R;

public class SelectParametersActivity extends AppCompatActivity {

    private static final int REQUEST_COMPANY = 1;
    private static final int REQUEST_WAREHOUSE = 2;
    private EditText txtCompany;
    private EditText txtWarehouse;
    private EditText txtResponsable;
    private EditText txtPerfil;
    private Button btnContinue;

    private GlobalClass globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_parameters);
        ((AppCompatActivity) this).getSupportActionBar().setTitle(getString(R.string.title_parameters));
        InitializeControls();
        InitializaEvents();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COMPANY) {
            if (resultCode == RESULT_OK) {
                String result=data.getStringExtra("companyName");
                this.txtCompany.setText(result);
                globalVariable.setIdSelectedCompany(Integer.valueOf(data.getStringExtra("companyId")));

                if(!this.txtCompany.getText().toString().equals("")){
                    txtWarehouse.setEnabled(true);
                }
                else{
                    txtWarehouse.setEnabled(false);
                }
            }
        }
        if (requestCode == REQUEST_WAREHOUSE) {
            if (resultCode == RESULT_OK) {
                String result=data.getStringExtra("wareHouseName");
                this.txtWarehouse.setText(result);
                globalVariable.setIdSelectedWareHouse(Integer.valueOf(data.getStringExtra("wareHouseId")));
            }
        }

    }

    private void InitializeControls() {
        txtCompany= findViewById(R.id.txtCompany);
        txtWarehouse= findViewById(R.id.txtWarehouse);
        txtWarehouse.setEnabled(false);
        txtResponsable= findViewById(R.id.txtResponsable);
        txtPerfil= findViewById(R.id.txtPerfil);
        globalVariable = (GlobalClass) getApplicationContext();
        btnContinue=findViewById(R.id.btnContinue);
        txtResponsable.setText(globalVariable.getUserName());
        txtPerfil.setText(globalVariable.getUserRole());
    }

    private void InitializaEvents(){
        txtCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenListCompany();
            }
        });

        txtWarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenListWareHouse();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(SelectParametersActivity.this,  MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void OpenListWareHouse() {
        Intent intent = new Intent(SelectParametersActivity.this, WareHouseListActivity.class);
        startActivityForResult(intent, REQUEST_WAREHOUSE);
    }

    private void OpenListCompany() {
        Intent intent = new Intent(SelectParametersActivity.this, CompanyListActivity.class);
        startActivityForResult(intent, REQUEST_COMPANY);
    }
}
