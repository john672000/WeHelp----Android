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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrphanageProfile extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    TextView errorCheck;

    private ListView listView;
    MyAdapter adapter;
    int sizeIndex = 1;

    String[] storeItemName = new String[50];
    String[] storeItemQuantity = new String[50];
    String[] storeAddress = new String[50];
    String[] storePhoneNo = new String[50];
    String[] storeUserName = new String[50];
    int indexToStore = 0;

    String[] savePreviousItemName = new String[55];
    String[] savePreviousItemQuantity = new String[55];
    String[] savePreviousAddress = new String[55];
    String[] savePreviousPhoneNo = new String[55];
    String[] savePreviousUserName = new String[55];

    private ImageView signout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orphanage);

        listView = (ListView) findViewById(R.id.listView);

        //display username
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
                Toast.makeText(OrphanageProfile.this, "Error displaying name!", Toast.LENGTH_SHORT).show();
            }
        });

        //end


        signout = (ImageView) findViewById(R.id.logoutButton);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrphanageProfile.this, "Signed Out", Toast.LENGTH_SHORT).show();

                //firebase method to sign out the current user
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OrphanageProfile.this, MainActivity.class));

                //prevent back button
                finish();
            }
        });


        //show items from DB
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showData(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }  // end of onCreate



    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] ritemName;
        String[] ritemQuantity;
        String[] ritemAddress;
        String[] ritemPhone;
        String[] ritemOwnerName;

        MyAdapter (Context c, String[] itemName, String[] itemQuantity, String[] address, String[] phone, String[] name, int abc){
            super(c, R.layout.row2, R.id.itemNameLayout, itemName);
            this.context = c;
            this.ritemName = itemName;
            this.ritemQuantity = itemQuantity;
            this.ritemAddress = address;
            this.ritemPhone = phone;
            this.ritemOwnerName = name;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row2 = layoutInflater.inflate(R.layout.row2, parent, false);

            TextView myItemName = row2.findViewById(R.id.itemNameLayout);
            TextView myItemQuantity = row2.findViewById(R.id.itemQuantityLayout);
            TextView myItemAddress = row2.findViewById(R.id.itemOwnerAddress);
            TextView myItemPhone = row2.findViewById(R.id.itemOwnerPhone);
            TextView myItemOwnerName = row2.findViewById(R.id.itemOwnerName);


            //set our resources on views : acc to youtube video
            myItemName.setText(ritemName[position]);
            myItemQuantity.setText(ritemQuantity[position]);
            myItemAddress.setText(ritemAddress[position]);
            myItemPhone.setText(ritemPhone[position]);
            myItemOwnerName.setText(ritemOwnerName[position]);

            return row2;
        }
    }




    private void showData(DataSnapshot snapshot) {

        for (DataSnapshot snp : snapshot.getChildren()){

            FirebaseDatabase.getInstance().getReference("Users").child(snp.getKey()).child("ItemDetails").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                       storeItemName[indexToStore] = ds.child("itemName").getValue().toString();
                       storeItemQuantity[indexToStore] = ds.child("itemQuantity").getValue().toString();
                       storeAddress[indexToStore] = "Address: "+ds.child("address").getValue().toString();
                       storePhoneNo[indexToStore] = "Phone: "+ds.child("phone").getValue().toString();
                       storeUserName[indexToStore] = "Owner: "+ds.child("fullName").getValue().toString();

                        String[] itemName = new String[sizeIndex];
                        String[] itemQuantity = new String[sizeIndex];
                        String[] address = new String[sizeIndex];
                        String[] phone = new String[sizeIndex];
                        String[] name = new String[sizeIndex];

                        //To put previous data to the String Arrays
                        for (int i = 0; i < sizeIndex - 1; i++){
                            itemName[i] = savePreviousItemName[i];
                            itemQuantity[i] = savePreviousItemQuantity[i];
                            address[i] = savePreviousAddress[i];
                            phone[i] = savePreviousPhoneNo[i];
                            name[i] = savePreviousUserName[i];
                        }

                        //set current values
                        itemName[sizeIndex-1] = storeItemName[indexToStore];
                        itemQuantity[sizeIndex-1] = storeItemQuantity[indexToStore];
                        address[sizeIndex-1] = storeAddress[indexToStore];
                        phone[sizeIndex-1] = storePhoneNo[indexToStore];
                        name[sizeIndex-1] = storeUserName[indexToStore];

                        //store current values
                        savePreviousItemName[sizeIndex-1] = storeItemName[indexToStore];
                        savePreviousItemQuantity[sizeIndex-1] = storeItemQuantity[indexToStore];
                        savePreviousAddress[sizeIndex-1] = storeAddress[indexToStore];
                        savePreviousPhoneNo[sizeIndex-1] = storePhoneNo[indexToStore];
                        savePreviousUserName[sizeIndex-1] = storeUserName[indexToStore];

                        sizeIndex++;

                        int value = 0;
                        adapter = new MyAdapter(OrphanageProfile.this, itemName, itemQuantity, address, phone, name, value);
                        listView.setAdapter(adapter);

                        indexToStore++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }



    }  // end of showData method


}  // End of main class