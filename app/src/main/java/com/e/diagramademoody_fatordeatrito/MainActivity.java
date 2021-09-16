package com.e.diagramademoody_fatordeatrito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.ornach.nobobutton.NoboButton;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private EditText reynoldsInput, roughnessInput, pipeDiameterInput;
    private NoboButton reynoldsCalculatorBtn, selectMaterialBtn;


        private Spinner pipeDiameterSpinner;
    private ArrayAdapter<CharSequence> diameterUnitAdapter;
    private String diameterUnit = "mm";

    private NoboButton calculateBtn;

    private Moody moody;
    Double frictionFactor = 0.0;

    private LinearLayout resultLayout;
    private TextView finalResultTextView;

    private AdView bottomAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        moody = new Moody();

        reynoldsInput = (EditText) findViewById(R.id.reynolds_number_input);
        reynoldsCalculatorBtn = (NoboButton) findViewById(R.id.reynolds_number_calculate_btn);
        roughnessInput = (EditText) findViewById(R.id.pipe_roughness_input);
        selectMaterialBtn = (NoboButton) findViewById(R.id.pipe_roughness_select_btn);
        pipeDiameterInput = (EditText) findViewById(R.id.pipe_diameter_input);

        pipeDiameterSpinner = (Spinner) findViewById(R.id.pipe_diameter_unit);
        diameterUnitAdapter = ArrayAdapter.createFromResource(this, R.array.pipe_dimensions, android.R.layout.simple_spinner_item);
        diameterUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pipeDiameterSpinner.setAdapter(diameterUnitAdapter);
        pipeDiameterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                diameterUnit = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        calculateBtn = (NoboButton) findViewById(R.id.calculate_btn);

        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        finalResultTextView = (TextView) findViewById(R.id.final_result);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        bottomAdView = findViewById(R.id.bottom_adview);
        AdRequest bottomAdRequest = new AdRequest.Builder().build();
        bottomAdView.loadAd(bottomAdRequest);


    }

    @Override
    protected void onStart() {
        super.onStart();

        calculateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CalculateFrictionFactorMETHOD();
            }
        });

        reynoldsCalculatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InflateReynoldsCalculatorMETHOD();
            }
        });

        selectMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InflateMaterialSelectMETHOD();
            }
        });
    }



    private void CalculateFrictionFactorMETHOD()
    {
        if(TextUtils.isEmpty(reynoldsInput.getText().toString()))
        {
            Toast.makeText(this, "Insira um número de Reynolds Válido!", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(roughnessInput.getText().toString()))
        {
            Toast.makeText(this, "Insira uma rugosidade de material válida!", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(pipeDiameterInput.getText().toString()))
        {
            Toast.makeText(this, "Insira um diâmetro válido!", Toast.LENGTH_LONG).show();
        }
        else
        {
            Double reynolds = Double.parseDouble(reynoldsInput.getText().toString()
                    .replace(",", "."));
            Double roughness = Double.parseDouble(roughnessInput.getText().toString()
                    .replace(",", "."));
            Double pipeDiameter = Double.parseDouble(pipeDiameterInput.getText().toString()
                    .replace(",", "."));

            if(reynolds <= 0)
            {
                Toast.makeText(this, "Insira um número de Reynolds Válido!", Toast.LENGTH_LONG).show();
            }
            else if(roughness <= 0)
            {
                Toast.makeText(this, "Insira uma rugosidade de material válida!", Toast.LENGTH_LONG).show();
            }
            else if(pipeDiameter <=0 )
            {
                Toast.makeText(this, "Insira um diâmetro válido!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Double RR = relativeRoughness(pipeDiameter, diameterUnit, roughness);
                frictionFactor = moody.ColebrookWhiteEquation(reynolds, RR);

                DecimalFormat df = new DecimalFormat("0.000000"); //result in 6 decimals places

                finalResultTextView.setText(df.format(frictionFactor));
                resultLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private Double relativeRoughness(Double diameter, String unit, Double roughness)
    {
        if(unit.equals("pol"))
        {
            diameter = diameter * 25.4;
        }
        Double RR = roughness/diameter;
        return RR;
    }


    private void InflateReynoldsCalculatorMETHOD()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.reynolds_calculator_layout, null, false);

        NoboButton cancelBtn = (NoboButton) view.findViewById(R.id.reynolds_calculator_cancel_btn);
        NoboButton confirmBtn = (NoboButton) view.findViewById(R.id.reynolds_calculator_confirm_btn);
        EditText densityInput = (EditText) view.findViewById(R.id.reynolds_calculator_density_input);
        EditText viscosityInput = (EditText) view.findViewById(R.id.reynolds_calculator_viscosity_input);
        EditText velocityInput = (EditText) view.findViewById(R.id.reynolds_calculator_velocity_input);
        EditText diameterInput = (EditText) view.findViewById(R.id.reynolds_calculator_diameter_input);


        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(TextUtils.isEmpty(densityInput.getText().toString()) ||
                        TextUtils.isEmpty(viscosityInput.getText().toString()) ||
                        TextUtils.isEmpty(velocityInput.getText().toString()) ||
                        TextUtils.isEmpty(diameterInput.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Preencha todos os campos corretamente!", Toast.LENGTH_LONG).show();
                }
                else
                    {
                        Double density = Double.valueOf(densityInput.getText().toString().replace(",","."));
                        Double viscosity = Double.valueOf(viscosityInput.getText().toString().replace(",","."));
                        Double velocity = Double.valueOf(velocityInput.getText().toString().replace(",","."));
                        Double diameter = Double.valueOf(diameterInput.getText().toString().replace(",","."));

                        if(density >=0 && viscosity >=0 && velocity >=0 && diameter >=0)
                        {

                            Double reynoldsNum = (density*velocity*diameter)/viscosity;
                            DecimalFormat df = new DecimalFormat("0.00");
                            reynoldsInput.setText(df.format(reynoldsNum));
                            dialog.dismiss();

                        }
                        else
                            {
                                Toast.makeText(MainActivity.this, "Preencha todos os campos corretamente!", Toast.LENGTH_LONG).show();
                            }


                    }
            }
        });
    }


    private String selectedRoughnessModal = "0.046";
    private void InflateMaterialSelectMETHOD()
    {
        String[] roughnessArray = getResources().getStringArray(R.array.pipe_material_roughness);
        selectedRoughnessModal = "0.046";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pipe_material_layout, null, false);

        NoboButton cancelBtn = (NoboButton) view.findViewById(R.id.select_material_cancel_btn);
        Spinner materialSpinner = (Spinner) view.findViewById(R.id.select_material_spinner);
        NoboButton confirmBtn = (NoboButton) view.findViewById(R.id.select_material_confirm_btn);
        TextView displayRoughnessText = (TextView) view.findViewById(R.id.select_material_roughness_text);
        ArrayAdapter<CharSequence> materialAdapter = ArrayAdapter.createFromResource(this, R.array.pipe_material, android.R.layout.simple_spinner_item);
        materialAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        materialSpinner.setAdapter(materialAdapter);
        materialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                selectedRoughnessModal = roughnessArray[i];
                displayRoughnessText.setText(selectedRoughnessModal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roughnessInput.setText(selectedRoughnessModal);
                dialog.dismiss();
            }
        });
    }



}