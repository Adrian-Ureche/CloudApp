package com.example.citydangersalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LogInActivity extends AppCompatActivity {
    private TextView userName;
    private TextView password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button logInRealButton = findViewById(R.id.logInRealButton);
        userName=findViewById(R.id.userNameLogIn);
        password=findViewById(R.id.passwordLogIn);
        logInRealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyLogIn(String.valueOf(userName.getText()),String.valueOf(password.getText()))) {
                    Intent intent = new Intent(LogInActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    Boolean verifyLogIn(String userNameString, String passwordString)
    {
        //get la baza de date pe tabela user
        if(userNameString.compareTo("admin")==0) {
            if(passwordString.compareTo("123")==0)
                return true;
            else
                password.setError("invalid password");
        }
        else{
            userName.setError("invalid username");
        }
        return false;
    }
}