package com.kc.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.kc.R;
import com.kc.utilites.RemoteDatabaseConnecter;

import java.io.IOException;

import static com.kc.C.*;


public class ALogin extends MyActivity {

    boolean acceptPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);


        if (fileReadSuccess()) {

            Intent intent = new Intent(this, AHome.class);
            startActivity(intent);// AHome
            Toast.makeText(ALogin.this, "Welcome " + MY_NAME, Toast.LENGTH_SHORT).show();
            finish();

        }

    }

    @SuppressWarnings("ConstantConditions")
    public void onLoginClick(View view) {

        EditText input = (EditText) findViewById(R.id.alogin_login_box);
        final String input_id = input.getText().toString();

        if (input_id != null) {

            if (!acceptPassword) {
                new AsyncTask<Void, Void, Void>() {

                    String toastMessage;
                    ProgressDialog dialog;

                    @Override
                    protected void onPreExecute() {

                        dialog = new ProgressDialog(ALogin.this);
                        dialog.setTitle("Please wait");
                        dialog.setMessage("connecting to the database");
                        dialog.setCancelable(false);
                        dialog.show();
                        Log.d(TAG, "login thread started");
                    }

                    @Override
                    protected Void doInBackground(Void... params) {

                        try {

                            // first check student's validity
                            String result = new RemoteDatabaseConnecter("GET", STUDENT_VALIDTY + "student_id=" + input_id)
                                    .connect(null)
                                    .getRawData();

                            String validity_result = String.valueOf(result.charAt(0));
                            String student_email = result.substring(1, result.length());
                            Log.d(TAG, "validity result = " + validity_result);

                            switch (validity_result) {
                                case "0":
                                    MY_ID = input_id;
                                    acceptPassword = true;
                                    toastMessage = "Password sent to " + student_email + ".\nCheck your inbox.";
                                    break;
                                case "1":
                                    toastMessage = "No such student found.\nTry again.";
                                    break;
                                case "2":
                                    toastMessage = "Account already active.\nContact staff for help.";
                                    break;
                                case "3":
                                    toastMessage = "Failed to send mail, Contact staff for your password";
                                    break;
                                default:
                                    toastMessage = "Someting went wrong.\nRecheck your ID input or contact staff.   ";
                                    finish();
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {

                        Toast.makeText(ALogin.this, toastMessage, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        if (acceptPassword) {
                            final EditText password = (EditText) findViewById(R.id.alogin_login_box);
                            password.getText().clear();
                            password.setHint("Enter password");
                            Button b = (Button) findViewById(R.id.alogin_login_button);
                            b.setText("Login");

                        }
                        Log.d(TAG, "login thread finished");
                    }

                }.execute(null, null, null);
            } else {
                new PasswordChecker().execute(input_id, null, null);
            }

        } else {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }

    }

    boolean fileReadSuccess() {

        SharedPreferences sp = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        MY_ID = sp.getString("my_student_id", "notFound");
        MY_GCM_ID = sp.getString("my_gcm_id", "notFound");
        MY_SEM = sp.getInt("my_sem", -1);
        MY_ROLL = sp.getInt("my_roll", -1);
        MY_NAME = sp.getString("my_name", "notFound");
        MY_EMAIL = sp.getString("my_email", "-");
        Log.i(TAG, "fileRead details:\n" +
                "MY_ID = " + MY_ID + "\n" +
                "MT_NAME = " + MY_NAME + "\n" +
                "MY_GCM_ID = " + MY_GCM_ID + "\n" +
                "MY_SEM = " + MY_SEM + "\n");
        return !MY_ID.equals("notFound");
    }

    class PasswordChecker extends AsyncTask<String, Void, Void> {

        boolean gotoHomeScreen = false;
        String         toastMessage;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ALogin.this);
            dialog.setTitle("Please wait");
            dialog.setMessage("verifing password");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String password = params[0];

            try {
                RemoteDatabaseConnecter rdcPass = new RemoteDatabaseConnecter("GET", PASSWORD_CHECKER + "student_id=" + MY_ID + "&password=" + password)
                        .connect(null);
                String result = rdcPass.rawData;

                switch (result) {
                    case "0":
                        InstanceID iid = InstanceID.getInstance(ALogin.this);
                        MY_GCM_ID = iid.getToken(GOOGLE_PROJECT_NO, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                        short nooTries = 0;

                        while ((!MY_GCM_ID.equals("") || MY_GCM_ID != null) && nooTries < 4) {

                            new RemoteDatabaseConnecter("POST", UPDATE_GCM_ID)
                                    .connect("student_id=" + MY_ID + "&user_gcm_id=" + MY_GCM_ID);

                            String recheck_result = new RemoteDatabaseConnecter("GET", GCM_ID_RECHECK + "student_id=" + MY_ID + "&gcm_id=" + MY_GCM_ID)
                                    .connect(null)
                                    .getRawData();

                            if (recheck_result.equals("-1") || recheck_result == null) {
                                MY_GCM_ID = "null";
                                nooTries++;
                            } else {
                                nooTries = 63;
                                MY_NAME = recheck_result;
                            }

                        }

                        SharedPreferences sp = ALogin.this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
                        Editor editor = sp.edit();
                        editor.putString("my_student_id", MY_ID);
                        editor.putString("my_gcm_id", MY_GCM_ID);
                        editor.putString("my_name", MY_NAME);
                        editor.apply();
                        toastMessage = "Welcome " + MY_NAME;
                        gotoHomeScreen = true;
                        break;
                    case "1":
                        toastMessage = "Incorrect password";
                        break;
                    default:
                        toastMessage = "Somrthing went wrong";

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Toast.makeText(ALogin.this, toastMessage, Toast.LENGTH_SHORT).show();
            if (gotoHomeScreen) {
                Intent intent = new Intent(ALogin.this, AHome.class);
                startActivity(intent);
                finish();
            }
            dialog.dismiss();
        }
    }

}
