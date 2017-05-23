package com.goline.goline.view.activty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.goline.goline.R;
import com.goline.goline.util.AlertUtil;
import com.goline.goline.util.EncryptUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth firebaseAuth;

    private EditText editEmail;
    private EditText editPassword;
    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();

        // Método que ativa o botão Up Navigation
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        editEmail = (EditText) findViewById(R.id.emaileditTxt);
        editPassword = (EditText) findViewById(R.id.senhaeditTxt);

        progressDialog = AlertUtil.getProgressDialog(this, "Verificando credenciais",
                "Aguarde um momento");

    }

    @Override
    public void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        firebaseAuth.removeAuthStateListener(this);
    }


    public void logIn(View view) {

        if(editEmail.getText().toString().trim().equals("")){
            AlertUtil.toast(getBaseContext(), "Informe o e-mail de usuário");
        }else if(editPassword.getText().toString().trim().equals("")){
            AlertUtil.toast(getBaseContext(), "Informe a senha de usuário");
        }else {
            progressDialog.show();

            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            password = EncryptUtil.encryptPassword(password);

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();

                    if (!task.isSuccessful()) {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            AlertUtil.toast(getBaseContext(), "Não há usuário registrado com o e-mail informado");
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            AlertUtil.toast(getBaseContext(), "E-mail ou senha inválidos");
                        } else {
                            AlertUtil.toast(getBaseContext(), "Ocorreu um erro. Tente Novamente.");
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        user = firebaseAuth.getCurrentUser();

        if(user!=null){

            Toast.makeText(this, "Usuário logado... ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }else{
            //Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show();
        }
    }

    // Esse método da Activity é chamado sempre que um botão da ActionBar é clicado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Estou verificando pelo Id do botão clicado se corresponde ao botão Up Navigation
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
