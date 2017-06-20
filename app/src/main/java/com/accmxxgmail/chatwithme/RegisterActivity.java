package com.accmxxgmail.chatwithme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    EditText username, password, email, passwordConfirm;
    Button registerButton;
    String emailStr, usernameStr, passwordStr, passwordConfirmStr;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText) findViewById(R.id.editText_email);
        username = (EditText)findViewById(R.id.editText_username);
        password = (EditText)findViewById(R.id.editText_create_password);
        passwordConfirm = (EditText)findViewById(R.id.editText_reenter_password);
        registerButton = (Button)findViewById(R.id.button_create_account);
        login = (TextView)findViewById(R.id.link_login);

        Firebase.setAndroidContext(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailStr = email.getText().toString();
                usernameStr = username.getText().toString();
                passwordStr = password.getText().toString();
                passwordConfirmStr = passwordConfirm.getText().toString();

                if(emailStr.equals("") ||
                        !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()){
                    email.setError("Please enter a valid email");
                }
                else if(usernameStr.equals("")){
                    username.setError("can't be blank");
                }
                else if(passwordStr.equals("")){
                    password.setError("can't be blank");
                }
                else if(!usernameStr.matches("[A-Za-z0-9]+")){
                    username.setError("only alphabet or number allowed");
                }
                else if(usernameStr.length()<5){
                    username.setError("at least 5 characters long");
                }
                else if(passwordStr.length()<5){
                    password.setError("at least 5 characters long");
                }
                else if(!passwordConfirmStr.equals(passwordStr)){
                    passwordConfirm.setError("Passwords Must Match");
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://chatwithme-8e2fe.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://chatwithme-8e2fe.firebaseio.com/users");

                            if(s.equals("null")) {
                                reference.child(usernameStr).child("password").setValue(passwordStr);
                                Toast.makeText(RegisterActivity.this, "registration successful", Toast.LENGTH_LONG).show();
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(usernameStr)) {
                                        reference.child(usernameStr).child("password").setValue(passwordStr);
                                        Toast.makeText(RegisterActivity.this, "registration successful", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "username already exists", Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }

                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError );
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(RegisterActivity.this);
                    rQueue.add(request);
                }
            }
        });
    }
}