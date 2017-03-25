package rvce.hackathon.hackathon;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    ProgressDialog progressDailog;
    @BindView(R.id.passwordEt)EditText passwordEt;
    @BindView(R.id.emailEt)EditText emailEt;
    @BindView(R.id.nameEt)EditText nameEt;
    @BindView(R.id.mobileEt)EditText mobileEt;
    String TAG="SIGN UP ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        progressDailog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @OnClick(R.id.signUpBtn)
    public void onClick(View view) {
        progressDailog.setMessage("Creating Account...");
        progressDailog.show();
        final String email=emailEt.getText().toString();
        String password=passwordEt.getText().toString();
        final String name=nameEt.getText().toString();
        final String mobile=mobileEt.getText().toString();
        if(valid(email,password,name,mobile)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Failed : "+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                FirebaseUser user = task.getResult().getUser();
                                UserProfileChangeRequest request=new UserProfileChangeRequest
                                        .Builder()
                                        .setDisplayName(nameEt.getText().toString())
                                        .build();
                                user.updateProfile(request)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User Name updated.");
                                                }
                                            }
                                        });
                                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                                User newUser=new User(email,name,mobile);
                                myRef.child(user.getUid()).setValue(newUser);
                            }
                            progressDailog.dismiss();

                            // ...
                        }
                    });
        }
        progressDailog.dismiss();
    }

    private boolean valid(String email, String password, String name, String mobile) {
        if(name.length()<3){
            nameEt.requestFocus();
            nameEt.setError("Minimum 3 Characters");
            return false;
        }
        if(mobile.isEmpty()){
            mobileEt.requestFocus();
            mobileEt.setError("10 digits Mobile Number");
            return false;
        }
        if(email.isEmpty()){
            emailEt.requestFocus();
            emailEt.setError("Please enter Email");
            return false;
        }
        if(password.length()<6){
            passwordEt.requestFocus();
            passwordEt.setError("Minimum 6 characters");
            return false;
        }


        return true;
    }
}
