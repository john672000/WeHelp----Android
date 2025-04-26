package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class UserProfile extends AppCompatActivity implements AddDialog.AddDialogListener {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ImageView add;

    private ImageView signout;

    private ListView listView;
    MyAdapter adapter;
    int sizeIndex = 1;
    String[] storeItemName = new String[50];
    String[] storeItemQuantity = new String[50];

    int nodeNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        listView = (ListView) findViewById(R.id.listView);

        TextView welcomeUser = (TextView) findViewById(R.id.welcomeText);


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null) {
                    String fullName = userProfile.fullName;
                    welcomeUser.setText("Welcome "+fullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "Error displaying name!", Toast.LENGTH_SHORT).show();
            }
        });



        signout = (ImageView) findViewById(R.id.logoutButton);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserProfile.this, "Signed Out", Toast.LENGTH_SHORT).show();

                //firebase method to sign out the current user
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserProfile.this, MainActivity.class));

                //to prevent back button
                finish();
            }
        });

        // To make the add btn
        add = (ImageView) findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make input dialog box
                AddDialog adddialog = new AddDialog();
                adddialog.show(getSupportFragmentManager(),"New Item");
            }
        });


    }


    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String ritemName[];
        String ritemQuantity[];

        MyAdapter (Context c, String itemName[], String itemQuantity[], int abc){
            super(c, R.layout.row, R.id.itemNameLayout, itemName);
            this.context = c;
            this.ritemName = itemName;
            this.ritemQuantity = itemQuantity;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);

            TextView myItemName = row.findViewById(R.id.itemNameLayout);
            TextView myItemQuantity = row.findViewById(R.id.itemQuantityLayout);

            //set our resources on views : acc to youtube video
            myItemName.setText(ritemName[position]);
            myItemQuantity.setText(ritemQuantity[position]);

            return row;
        }
    }



    @Override
    public void applyTexts(String itemNameReceived, String itemQuantityReceived) {
        nodeNumber++;
        // if clicked ok without anything entered
        if (itemNameReceived.trim().isEmpty() || itemQuantityReceived.trim().isEmpty() ){
            Toast.makeText(UserProfile.this, "Fill all details!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] itemName = new String[sizeIndex];
        String[] itemQuantity = new String[sizeIndex];

        //Trim extra spaces
        String itemNameTrim = itemNameReceived.trim();
        String itemQuantityTrim = "Quantity: "+itemQuantityReceived.trim();

        //To put previous data to the String Arrays
        for (int i = 0; i < sizeIndex - 1; i++){
                itemName[i] = storeItemName[i];
                itemQuantity[i] = storeItemQuantity[i];
        }

        itemName[sizeIndex-1] = itemNameTrim;
        itemQuantity[sizeIndex-1] = itemQuantityTrim;

        //store new values
        storeItemName[sizeIndex-1] = itemNameTrim;
        storeItemQuantity[sizeIndex-1] = itemQuantityTrim;

        sizeIndex++;

        int value = 0;
        adapter = new MyAdapter(this, itemName, itemQuantity, value);
        listView.setAdapter(adapter);


        // Store this to Firebase DB
        String stringnodenumber = String.valueOf(nodeNumber);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //for address pulling
                        String address = snapshot.child("address").getValue().toString();
                        String fullName = snapshot.child("fullName").getValue().toString();
                        String phoneNo = snapshot.child("phone").getValue().toString();
                        //Toast.makeText(UserProfile.this, fullName+" "+phoneNo, Toast.LENGTH_SHORT).show();
                        ItemDetails itemdetails = new ItemDetails(itemNameTrim, itemQuantityTrim, address, fullName, phoneNo);

                        // To save it
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ItemDetails").child(stringnodenumber).setValue(itemdetails)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // if data inserted to DataBase
                                        if(task.isSuccessful()) {

                                        }
                                        else {
                                            Toast.makeText(UserProfile.this,"Fail", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserProfile.this, "Error pulling address DB", Toast.LENGTH_SHORT).show();
                    }
                });



    }



}