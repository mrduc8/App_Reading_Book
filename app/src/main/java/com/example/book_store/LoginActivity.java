package com.example.book_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.book_store.admin.AdminMenuActivity;
import com.example.book_store.model.User;
import com.example.book_store.sharedpreferences.Constants;
import com.example.book_store.sharedpreferences.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    EditText txtPhone, txtPass;
    Button btnLogin, btnRegister;
    private User user;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        txtPhone = (EditText) findViewById(R.id.login_txtPhone);
        txtPass = (EditText) findViewById(R.id.login_txtPass);
        btnLogin = findViewById(R.id.login_btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<User> list = new ArrayList<>();
                String phone = txtPhone.getText().toString().trim();
                String pass = txtPass.getText().toString().trim();
                if (phone.isEmpty() || pass.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Trường không được để trống", Toast.LENGTH_SHORT).show();
                } else {
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                User user1 = ds.getValue(User.class);
                                Log.e("TAG", "onDataChange: " + ds);
                                list.add(user1);
                            }
                            int dem = 0;
                            for (User user2 : list) {
                                if (user2.getPhone().equals(phone) && user2.getPassword().equals(pass)) {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                    writeToSharedPreferences(user2);
                                    //is admin
                                    if (user2.getIsAdmin() == 1) {
                                        Intent adminMenu = new Intent(getApplicationContext(), AdminMenuActivity.class);
                                        //adminMenu.putExtra(USER_KEY,user);
                                        startActivity(adminMenu);
                                    } else {
                                        //Main menu Activity
                                        //Gui user
                                        Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
                                        //menu.putExtra(USER_KEY,user);
                                        startActivity(menu);
                                    }
                                    break;
                                } else {
                                    dem++;
                                }
                            }

                            if (list.size() == dem) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!\nVui lòng thử lại", Toast.LENGTH_LONG).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
        btnRegister = (Button)

                findViewById(R.id.login_btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToRegisterActivity();
            }
        });

    }

    private void writeToSharedPreferences(User user) {
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
//        sharedPreferences = getSharedPreferences("book_store", Context.MODE_PRIVATE);
//        SharedPreferences.Editor  editor = sharedPreferences.edit();
        String phone = user.getPhone();
        int isAdmin = user.getIsAdmin();
        preferenceManager.putString(Constants.LOGIN_PHONE, phone);
        preferenceManager.putInt(Constants.LOGIN_IS_ADMIN, isAdmin);
//        editor.putString("phone",phone);
//        editor.putInt("isAdmin",isAdmin);
//        editor.commit();
    }

    private void changeToRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }

}