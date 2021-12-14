package com.example.bmic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmic.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kevalpatel2106.rulerpicker.RulerValuePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class  BMI extends AppCompatActivity {
    RulerValuePicker rulerValuePickerHeight, rulerValuePickerWeight;
    Button btnCal, btnSave, btnReset;
    TextView textViewResult, textViewComment;
    FirebaseFirestore firebaseFirestore;
    String userId = null;

    private static final String TAG = "BMI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_m_i);
        userId = getIntent().getStringExtra("userId");
        rulerValuePickerHeight = findViewById(R.id.ruler_picker_height);
        rulerValuePickerWeight = findViewById(R.id.ruler_picker_weight);
        btnCal = findViewById(R.id.btnCal);
        btnSave = findViewById(R.id.btnSave);
        btnReset = findViewById(R.id.btnReset);
        textViewResult = findViewById(R.id.textViewResult);
        textViewComment = findViewById(R.id.textViewComment);

        rulerValuePickerWeight.selectValue(50);
        rulerValuePickerHeight.selectValue(150);

        firebaseFirestore = FirebaseFirestore.getInstance();

        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strweight = String.valueOf(rulerValuePickerWeight.getCurrentValue());
                String strheight = String.valueOf(rulerValuePickerHeight.getCurrentValue());

                if(strweight.equals("")){
                    Toast.makeText(BMI.this,"Please choice weight", Toast.LENGTH_SHORT).show();
                    rulerValuePickerWeight.requestFocus();
                    return;
                }
                if(strheight.equals("")){
                    Toast.makeText(BMI.this,"Please choice weight", Toast.LENGTH_SHORT).show();
                    rulerValuePickerHeight.requestFocus();
                    return;
                }

                float weight = Float.parseFloat(strweight);
                float height = Float.parseFloat(strheight)/100;
                float bmiVlaue = BMICaculate(weight, height);

                textViewComment.setText(interpreteBMI(bmiVlaue));
                textViewResult.setText("BMI: " + String.format("%.2f", bmiVlaue));


            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                if(isLogin()){
                    String result = textViewResult.getText().toString();
                    String comment = textViewComment.getText().toString();
                    String id = UUID.randomUUID().toString();

                    saveToFireStore(id, result, comment);
                    i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("userId",userId);

                }else{
                    Toast.makeText(BMI.this, "You need to login to save result", Toast.LENGTH_SHORT).show();
                    i = new Intent(getApplicationContext(),  LoginActivity.class);
                }
                startActivity(i);

            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rulerValuePickerWeight.selectValue(50);
                rulerValuePickerHeight.selectValue(150);
                textViewResult.setText("");
                textViewComment.setText("");
            }
        });

    }
    public Boolean isLogin(){
        if(userId == null){
            return false;
        }
        return true;
    }

    private void saveToFireStore(String id, String result, String comment){
        if(!result.isEmpty() && !comment.isEmpty()){
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("result", result);
            map.put("comment", comment);
            map.put("userId",userId);
            map.put("date",new SimpleDateFormat("E, dd-MMM-yyyy HH:mm:ss").format(new Date()));
            firebaseFirestore.collection("Documents").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(BMI.this, "Data Saved!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BMI.this, "Failed !!", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(this, "Empty Fields not Allowed", Toast.LENGTH_SHORT).show();
        }
    }

    public float BMICaculate(float rulerValuePickerWeight, float rulerValuePickerHeight ){
        return rulerValuePickerWeight/(rulerValuePickerHeight * rulerValuePickerHeight);
    }
    public String interpreteBMI(float bmiValue){
        if (bmiValue <16){
            return "Servely Underweight";
        }
        else if(bmiValue < 18.5){
            return "Underweight";
        }
        else if(bmiValue <25){
            return "Normal";
        }
        else if(bmiValue < 30){
            return "OverWeight";
        }
        else return "Obese";
    }
}