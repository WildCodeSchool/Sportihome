package fr.wildcodeschool.apprenti.sportihome.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import javax.net.ssl.HttpsURLConnection;
import fr.wildcodeschool.apprenti.sportihome.Model.LogInModel;
import fr.wildcodeschool.apprenti.sportihome.ParserJSON;
import fr.wildcodeschool.apprenti.sportihome.R;

/**
 * Created by chantome on 15/01/2017.
 */

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editEmail;
    private Button btnRestore;
    private String email;
    private HttpURLConnection client;

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editEmail = (EditText)findViewById(R.id.email);
        btnRestore = (Button)findViewById(R.id.restore);

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editEmail.getText().toString().isEmpty()){
                    //SEND POST
                    new SendPostRestore().execute();
                }else{
                    editEmail.setError("email vide !");
                }

            }
        });
    }

    public class SendPostRestore extends AsyncTask<String, Void, String[]> {

        protected void onPreExecute(){
            email = editEmail.getText().toString();
        }

        protected String[] doInBackground(String... arg0) {

            try {

                URL url = new URL("https://sportihome.com/api/users/forgotPassword");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", email);
                //Log.e("params",postDataParams.toString());

                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setDoOutput(true);
                client.setDoInput(true);

                OutputStream os = client.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=client.getResponseCode();
                String res[] = new String[2];
                InputStream input;

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    //RESPONSE OK
                    input = client.getInputStream();

                }
                else {
                    //RESPONSE ERROR
                    input = client.getErrorStream();

                }

                BufferedReader in=new BufferedReader(new InputStreamReader(input));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();

                res[0] = String.valueOf(responseCode);
                res[1] = sb.toString();

                return res;
            }
            catch(Exception e){
                Log.e("ERR : ",new String("Exception: " + e.getMessage()));
                return null;
            }

        }

        @Override
        protected void onPostExecute(String[] result) {
            //Log.i("RES : ", result);

            if(result != null){
                switch (result[0]){
                    case "200":

                        //final LogInModel monLog = ParserJSON.getLogIn(result[1]);

                        //Log.i("ACCESS : ", String.valueOf(monLog.isSuccess()));
                        //Log.i("TOKEN : ", monLog.getToken());

                        //Restore Ok - Go page Check your mail !
                        Intent intent = new Intent(ForgotPasswordActivity.this,CheckMailActivity.class);
                        startActivity(intent);
                        finish();

                        break;
                    case "401":
                        Toast.makeText(getApplicationContext(), result[1], Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}