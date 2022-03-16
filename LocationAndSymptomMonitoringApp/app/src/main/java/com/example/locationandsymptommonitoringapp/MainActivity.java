package com.example.locationandsymptommonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {

    EditText username, password, repassword;
    Button signin, signup;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase.loadLibs(this);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
       // repassword = (EditText) findViewById(R.id.repassword);

        signin = (Button) findViewById(R.id.buttonSignIn);
        //signup = (Button) findViewById(R.id.buttonSignUp);

        db = new DBHelper(this);

        /*signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();
                if( user.equals("") || pass.equals("") || repass.equals("")){
                    Toast.makeText(MainActivity.this,"Please enter all the required information", Toast.LENGTH_SHORT).show();
                }else{
                    if(pass.equals(repass)){
                        boolean userCheck = db.checkusername(user);
                        if(!userCheck){
                            boolean insert = db.insertData(user, pass);
                            if(insert){
                                Toast.makeText(MainActivity.this,"Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this,"Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                                Toast.makeText(MainActivity.this,"user already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this,"passwords are not matching", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });*/

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = username.getText().toString();
                String pass = password.getText().toString();
                if(user.equals("")|| pass.equals("")){
                    Toast.makeText(MainActivity.this,"Please enter all the fields", Toast.LENGTH_SHORT).show();
                }
                else if(!pass.equals(DBHelper.PASS_PHRASE)){
                    Toast.makeText(MainActivity.this,"Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
                else{

                    Toast.makeText(MainActivity.this,"SignIn successfull", Toast.LENGTH_SHORT).show();
                    //DBHelper.PASS_PHRASE = pass;
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);

                   /* boolean checkUserNameNdPass = db.checkusernamepassword(user, pass);
                    if(checkUserNameNdPass){
                        Toast.makeText(LoginActivity.this,"SignIn successfull", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }*/
                }

               /* Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);*/

            }
        });
    }
}