package edu.fsu.cs.mobile.teammatefinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class signUp extends Fragment {


    private static final String TAG = "users" ;

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mUsernameField;
    private EditText mAgeField;
    private RadioGroup gender;
    private int buttonId;
    RadioButton rb;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseFirestore database;
    private FirebaseUser userI;

    private View vi;
    private OnFragmentInteractionListener mListener;

    public signUp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       vi = inflater.inflate(R.layout.fragment_sign_up, container, false);

       mEmailField = vi.findViewById(R.id.email);
       mPasswordField = vi.findViewById(R.id.password);
       mAgeField= vi.findViewById(R.id.age);
       mUsernameField= vi.findViewById(R.id.username);

       gender = (RadioGroup) vi.findViewById(R.id.radioGroup);



       Button button= (Button) vi.findViewById(R.id.signUp);
       button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });


       return vi;
    }

    public void signup() {

        buttonId = gender.getCheckedRadioButtonId();
        rb =(RadioButton) vi.findViewById(buttonId);

        if (!validateForm()) {
            return;
        }
        createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());

    }
    private void writeNewUser( String name, String email,String password,String age,String gender) {

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);
        user.put("age", age);
        user.put("gender", gender);


        database.collection("users").document(userI.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void createAccount(String email, String password) {

        mAuth = FirebaseAuth.getInstance();
        Log.i("yes","hafefehas");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            database = FirebaseFirestore.getInstance();
                            userI = FirebaseAuth.getInstance().getCurrentUser();

                            writeNewUser(mUsernameField .getText().toString(),mEmailField.getText().toString(),mPasswordField.getText().toString(),mAgeField.getText().toString(),rb.getText().toString());
                            Log.i("yes","hahas");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Log.i("yes","hfghfghgfh");
                            //updateUI(null);
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        Log.i("yes","dope");
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        Log.i("yes","hahaha");
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        Log.i("yes","kkk");

        String username = mUsernameField .getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUsernameField.setError("Required.");
            valid = false;
        } else {
            mUsernameField.setError(null);
        }

        Log.i("yes","mid");

        String age = mAgeField.getText().toString();
        if (TextUtils.isEmpty(age)) {
            mAgeField.setError("Required.");
            valid = false;
        } else {
            mAgeField.setError(null);
        }


        if (gender.getCheckedRadioButtonId() == -1) {
            valid = false;
            Toast.makeText(getActivity(), "Please select Gender", Toast.LENGTH_SHORT).show();
        }
        Log.i("yes","final");
        return valid;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
