package xyz.krniket.dailyshopping;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, pass;
    private Button btnreg;
    private TextView signin;

    //create object of firebase oath...
    private FirebaseAuth mAuth;

    //progress dialog
    private ProgressDialog mDialog;

    

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);


        email = findViewById(R.id.email_signup);
        pass = findViewById(R.id.password_signup);
        btnreg = findViewById(R.id.btn_signup);
        signin = findViewById(R.id.signin_txt);

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail=email.getText().toString().trim();
                String mpass=pass.getText().toString().trim();

                //Caution for reg not field..

                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Required...");
                    return;
                }
                if (TextUtils.isEmpty(mpass)){
                    pass.setError("Required?");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                //obj code for firebase auth
                mAuth.createUserWithEmailAndPassword(mEmail,mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //condition

                        if (task.isSuccessful()){

                            //after successfully completion registration goes to home activity
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Susseful",Toast.LENGTH_SHORT).show();

                            mDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                    }
                });

            }
        });


    //we call main activity by signin text in regact.
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}
