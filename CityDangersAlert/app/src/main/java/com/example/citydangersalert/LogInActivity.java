package com.example.citydangersalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LogInActivity extends AppCompatActivity {
    private TextView userName;
    private TextView password;
    private static class LogInTaskParameters
    {
        public String username;
        public String password;
        LogInTaskParameters(String username, String password)
        {
            this.username= username;
            this.password=password;
        }
    }
    private static class LogInReturnTaskParameters
    {
        public Boolean verif=false;
        public String error="";
    }
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
                new LogInVerification().execute(new LogInTaskParameters(String.valueOf(userName.getText()),String.valueOf(password.getText())));
            }
        });
    }

    class LogInVerification extends AsyncTask<LogInTaskParameters,Void,LogInReturnTaskParameters>
    {

        @Override
        protected LogInReturnTaskParameters doInBackground(LogInTaskParameters... logInTaskParameters) {
            try {
                URL url = new URL("https://citydangersapi.azurewebsites.net/users");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.e("dasfas","inainte de  a se stabili conexiunea");
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    Log.e("dasfas","Conexiunea stabilita");
                    Log.e("dasfas","s a stabilit conexiunea");
                    // Do normal input or output stream reading
                    StringBuilder content;
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        content = new StringBuilder();
                        line = in.readLine();
                        Log.e("dasfas",line);
                        if(line==null)
                        {
                            Log.e("asdf","line goala");
                            throw new MalformedURLException();
                        }
                        else{
                            JSONArray array = new JSONArray(line);
                            LogInReturnTaskParameters returnParams=new LogInReturnTaskParameters();
                            for(int i=0; i < array.length(); i++)
                            {
                                JSONObject object = array.getJSONObject(i);
                                Log.e("dsafd",object.getString("partitionKey"));
                                Log.e("dsafd",object.getString("rowKey"));

                                if(logInTaskParameters[0].username.compareTo(object.getString("partitionKey"))==0)
                                {
                                    if(logInTaskParameters[0].password.compareTo(object.getString("rowKey"))==0)
                                    {

                                        returnParams.verif=true;
                                        CurrentUser.currentUser=logInTaskParameters[0].username;
                                        return returnParams;
                                    }
                                    else {
                                        returnParams.verif=false;
                                        returnParams.error="incorrect password";
                                        return returnParams;
                                    }
                                }
                            }
                            returnParams.verif=false;
                            returnParams.error="non-existent user";
                            return returnParams;
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        connection.disconnect();
                    }
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(LogInReturnTaskParameters logInReturnTaskParameters) {
            super.onPostExecute(logInReturnTaskParameters);
            Log.e("saf",logInReturnTaskParameters.error);
            if(logInReturnTaskParameters.verif) {
                Intent intent = new Intent(LogInActivity.this, MapsActivity.class);
                startActivity(intent);
            }
            else
            if(logInReturnTaskParameters.error.compareTo("incorrect password")==0)
                password.setError("incorrect password");
            else
                userName.setError("non-existent user");

        }
    }
}