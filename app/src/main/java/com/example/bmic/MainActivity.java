package com.example.bmic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Script;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmic.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView textViewUserName , dt ;
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<Model> list,ls;
    String userId;
    Button BtBMI,btLogout;

    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btLogout = findViewById(R.id.btLogOut);
        BtBMI = findViewById(R.id.gtMBI);
        userId = getIntent().getStringExtra("userId");
        textViewUserName = findViewById(R.id.textViewUserName);
        dt = findViewById(R.id.textView2);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy");
        dt.setText(sdf.format(new Date()));

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),BMI.class));
            }
        });

        BtBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),BMI.class);
                i.putExtra("userId",userId);
                startActivity(i);
            }
        });
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot snapshot : task.getResult()){
                    if(snapshot.getId().equals(userId)){
                        textViewUserName.setText("Hi, " +(snapshot.getString("Full name")));
                    }
                }
            }
        });
        list = new ArrayList<>();
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);
        showData();
    }

    private void showData() {
        firebaseFirestore.collection("Documents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                for(DocumentSnapshot snapshot : task.getResult()){
                    Model model = new Model(snapshot.getString("id"),
                            snapshot.getString("result"),
                            "Status: " + snapshot.getString("comment"),
                            snapshot.getString("userId"),
                            "Date: " + snapshot.getString("date"));
                    if(model.getUserId().equals(userId)){
                        list.add(model);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Some thing went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

}