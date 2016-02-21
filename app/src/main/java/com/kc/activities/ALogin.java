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
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.kc.R;
import com.kc.utilites.RemoteDatabaseConnecter;

import static com.kc.C.FILE_NAME;
import static com.kc.C.GCM_ID_RECHECK;
import static com.kc.C.GOOGLE_PROJECT_NO;
import static com.kc.C.MY_GCM_ID;
import static com.kc.C.MY_ID;
import static com.kc.C.MY_NAME;
import static com.kc.C.MY_ROLL;
import static com.kc.C.MY_SEM;
import static com.kc.C.STUDENT_VALIDTY;
import static com.kc.C.UPDATE_GCM_ID;


public class ALogin extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);


        if(fileReadSuccess()){

            // check for necessary updates from server and store locally (if internet connection is avaiable)


            Intent intent = new Intent(this, AHome.class);
            startActivity(intent);
            Toast.makeText(ALogin.this, "Welcome " + MY_NAME, Toast.LENGTH_SHORT).show();
            finish();

        }

    }

    @SuppressWarnings("ConstantConditions")
    public void onLoginClick(View view){

        EditText input = (EditText) findViewById(R.id.alogin_login_box);
        final String input_id = input.getText().toString();

        if(input_id != null){

            new AsyncTask<Void, Void, Void>() {

                String toastMessage;
                ProgressDialog dialog;
                boolean gotoHomeScreen = false;

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

                    try{

                        // first check student's validity
                        String validity_result = new RemoteDatabaseConnecter("GET", STUDENT_VALIDTY + "student_id=" +input_id)
                                .connect(null)
                                .getRawData();

                        Log.d(TAG, "validity result = " + validity_result);

                        switch (validity_result){
                            case "0":

                                MY_ID = input_id;
                                InstanceID iid = InstanceID.getInstance(ALogin.this);
                                MY_GCM_ID = iid.getToken(GOOGLE_PROJECT_NO, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                                short nooTries = 0;

                                while((!MY_GCM_ID.equals("") || MY_GCM_ID != null) && nooTries < 4){

                                    new RemoteDatabaseConnecter("POST",UPDATE_GCM_ID)
                                            .connect("student_id=" + input_id + "&user_gcm_id=" + MY_GCM_ID);

                                    String recheck_result = new RemoteDatabaseConnecter("GET", GCM_ID_RECHECK +"student_id="+input_id+"&gcm_id="+MY_GCM_ID)
                                            .connect(null)
                                            .getRawData();

                                    if(recheck_result.equals("-1") || recheck_result == null){
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

                                toastMessage = "Account activated.";
                                gotoHomeScreen = true;
                                break;
                            case "1":
                                toastMessage = "No such student found.\nTry again.";
                                break;
                            case "2":
                                toastMessage = "Account already active.\nContact staff for help.";
                                break;
                            default:
                                toastMessage = "Someting went wrong :(";
                                finish();
                                break;
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                    Toast.makeText(ALogin.this, toastMessage, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    if(gotoHomeScreen){
                        Intent intent = new Intent(ALogin.this, AHome.class);
                        startActivity(intent);
                        finish();
                    }
                    Log.d(TAG, "login thread finished");
                }

            }.execute(null, null, null);

        } else {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }

    }

    boolean fileReadSuccess(){

        SharedPreferences sp = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        MY_ID = sp.getString("my_student_id", "notFound");
        MY_GCM_ID = sp.getString("my_gcm_id", "notFound");
        MY_SEM = sp.getInt("my_sem", -1);
        MY_ROLL = sp.getInt("my_roll", -1);
        MY_NAME = sp.getString("my_name", "notFound");
        Log.i(TAG, "fileRead details:\nMY_ID = " + MY_ID + "\nMT_NAME = " + MY_NAME + "\nMY_GCM_ID = " + MY_GCM_ID);
        return !MY_ID.equals("notFound");
    }

}
