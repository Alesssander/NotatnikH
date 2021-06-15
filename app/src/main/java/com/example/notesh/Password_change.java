package com.example.notesh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Password_change extends AppCompatActivity {


    EditText newPassword;
    EditText confirmPassword;
    Button savePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        //pobranie kontrolek z widoku
        newPassword = findViewById(R.id.editTextTextPassword);
        confirmPassword = findViewById(R.id.editTextTextPassword2);
        savePassword = findViewById(R.id.button);

        //ustawienie akcji na naduszenie przycisku zapisu akcji
        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pobranie obu haseł z okien do wpisania
                String pass = newPassword.getText().toString();
                String confirm = confirmPassword.getText().toString();

                //pętla sprawdza czy oba hasła się zgadzają oraz czy nie są to pola puste
                 if(pass.equals(confirm) && !pass.isEmpty() && !confirm.isEmpty()){

                     //pobranie obiektu shared preferences
                     SharedPreferences preferences = getApplicationContext().getSharedPreferences("example", Context.MODE_PRIVATE);
                     SharedPreferences.Editor editor = preferences.edit();

                     //hashowanie hasła
                     String hashPass = EncryptionHelper.getSecureHash(pass,"mojsalt");

                     // zapisanie hasła do shared preferences
                     editor.putString("pass", hashPass);
                     editor.commit();

                     //zamknięcie okna
                     finish();
                 }
                 else {

                     Toast toast = Toast.makeText(getApplicationContext(),"Hasło nie pasuje", Toast.LENGTH_LONG);
                     toast.show();

                 }



            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}



