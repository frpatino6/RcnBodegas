package com.rcnbodegas.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.UserViewModel;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private View mLoginFormView;
    private View mProgressView;

    private EditText input_email;
    private EditText input_password;
    private Button btn_login;
    private ProgressDialog dialogo;
    private GlobalClass globalVariable;
    private UserViewModel data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((AppCompatActivity) this).getSupportActionBar().setTitle(getString(R.string.action_sign_in));

        InitializaControls();
        InitializaEvents();
    }

    private void InitializaControls(){
        input_email=findViewById(R.id.input_email);
        input_password=findViewById(R.id.input_password);
        btn_login=findViewById(R.id.btn_login);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        globalVariable = (GlobalClass) getApplicationContext();

    }

    private void InitializaEvents()
    {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempLogin();
            }
        });
    }

    private void attempLogin(){
        input_email.setError(null);
        input_password.setError(null);

        String email = input_email.getText().toString();
        String password = input_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            input_password.setError(getString(R.string.error_password_empty));
            focusView = input_password;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            input_email.setError(getString(R.string.error_user_empty));
            focusView = input_email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //showProgress(true);
            //TODO SE DEJA EN COMENTARIO ESTA LINEA PARA EFECTOS DE PRUEBAS
            //asyncLogin();

            //TODO, SOLO PARA PRUEBAS SIN CONEXION A BASE DE DATOS
            globalVariable.setUserName("PRUEBAS");
            globalVariable.setUserRole("ADMIN");
            Intent intent = null;
            intent = new Intent(LoginActivity.this,  SelectParametersActivity.class);
            startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showLoginError(String res) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this);

        dlgAlert.setMessage(res);
        dlgAlert.setTitle(getString(R.string.app_name));
        //dlgAlert.setPositiveButton(getString(R.string.Texto_Boton_Ok), null);
        dlgAlert.setPositiveButton(R.string.Texto_Boton_Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                // current activity

            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void asyncLogin() {
        // Create URL
        final String username = input_email.getText().toString();
        String pws = input_password.getText().toString();
        String urlCustomers = globalVariable.getUrlServices() + "User/LoginActiveDirectory/" + username + "/" + pws ;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(urlCustomers, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<UserViewModel> token = new TypeToken<UserViewModel>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            data = gson.fromJson(res, token.getType());

                            globalVariable.setUserName(input_email.getText().toString());
                            globalVariable.setUserRole(data.roleName);
                            Intent intent = null;
                            intent = new Intent(LoginActivity.this,  SelectParametersActivity.class);
                            startActivity(intent);

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        showProgress(false);

                    }
                }
        );
    }

}