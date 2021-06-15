package com.example.notesh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //pobranie kontrolek przycisku i pola tekstowego
    Button button1;
    EditText passwordText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sprawdzamy czy mamy już klucz w KeyStore - jeżeli nie to generujemy go automatycznie
        SecretKeyUtils.CheckAndGeneratePassword();

        //sprawdzamy czy jest ustawione haslo do logowania, jak nie odsylamy do ustawienia nowego hasla
        //sprawdzam czy mam zapisane hasło w shared preferences, na początku inicjuję obiekt do pobierania danych
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("example", Context.MODE_PRIVATE);

        //próbuje pobrać string z shared preferences z hasłem do logowania
        String pass = preferences.getString("pass", "");

        if(pass==""){

            //nie ma zapisanego hasła więc przechodzę do activity gdzie mogę wygenerwować nowe hasło
            Intent passIntent = new Intent(MainActivity.this, Password_change.class);
            startActivity(passIntent);
        }


        setContentView(R.layout.activity_main);

        //kontrolki
        button1 = findViewById(R.id.button1);
        passwordText1 = findViewById(R.id.editTextTextPassword1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pobieranie wpisanego hasla użytkownika
                String password = passwordText1.getText().toString();
                //użycie na nim funkcji hashującej - dodaję do niego dodatkowo salt
                String hashPass = EncryptionHelper.getSecureHash(password, "mojsalt");
                // pobieranie zahashowanego hasła z shared
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("example", Context.MODE_PRIVATE);
                String pass = preferences.getString("pass", "");

                if (hashPass.equals(pass)){

                    //przełączenie na widok Notatka gdy hasło zostanie przyjęte
                    Intent noteIntent = new Intent(MainActivity.this, Note.class);
                    startActivity(noteIntent);
                    finish();
                }
                else{
                    //info o złym haśle
                    Toast toast = Toast.makeText(getApplicationContext(), "Hasło niepoprawne", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });


    }

    //chowanie klawiatury jak sie kliknie poza obszar edytowania tekstu
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}