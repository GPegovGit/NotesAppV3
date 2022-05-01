package com.mirea.artemov.notesappv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.mirea.artemov.notesappv2.Models.User;

import java.security.AccessController;
import java.util.Collection;

public class LoginActivity extends AppCompatActivity {

    EditText editText_username, editText_password;
    TextView textView_login;
    Button button_login;
    Button button_reg;
    public User user = new User();
    public DatabaseReference DB;
    public String USER_KEY = "User";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_username = findViewById(R.id.editText_username);
        editText_password = findViewById(R.id.editText_password);
        textView_login = findViewById(R.id.textView_login);
        button_login = findViewById(R.id.button_login);
        button_reg = findViewById(R.id.button_registration);
        DB = FirebaseDatabase.getInstance().getReference(USER_KEY);


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editText_username.getText().toString();
                String password = editText_password.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username and Password can't be null!", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkUser(username, password);

                user.setUsername(username);
                user.setPassword(password);



                Intent intent =new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("user", editText_username.getText().toString()); // передается только сериализуемый объект
                setResult(Activity.RESULT_OK, intent);
                LoginActivity.this.startActivity(intent);
            }
        });


        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editText_username.getText().toString();
                String password = editText_password.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username and Password can't be null!", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkLogin(username, password);

                user.setUsername(username);
                user.setPassword(password);

                Intent intent =new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("user", editText_username.getText().toString()); // передается только сериализуемый объект
                setResult(Activity.RESULT_OK, intent);
                LoginActivity.this.startActivity(intent);
            }
        });
    }




    public void checkLogin(String username, String password){
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User da = ds.getValue(User.class);
                    if (da.username.equals(username)) {
                        Toast.makeText(LoginActivity.this, "This login is already exists", Toast.LENGTH_SHORT).show();
                        return; }
                }
                DB.push().setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        DB.addValueEventListener(vListener);
    }

    public void checkUser(String username, String password){
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        User da = ds.getValue(User.class);
                        if (da.username.equals(username) && da.password.equals(password)) {
                            Toast.makeText(LoginActivity.this, "You have successfully logged in", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Login or password is wrong, your data will not save on cloud", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        DB.addValueEventListener(vListener);
    }
}