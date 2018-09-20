package io.iqube.pomogite;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import io.iqube.pomogite.Models.User;
import io.iqube.pomogite.Network.APIClient;
import io.iqube.pomogite.Network.ServiceGenerator;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mUsernameTextView;
    private EditText mPasswordView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        User user = PomogiteApplication.GetUser(this);
        if (user.getUsername() != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        mUsernameTextView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        TextView mEmailSignInButton = findViewById(R.id.login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        TextView mSignup = findViewById(R.id.sign_up);
        mSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

    }

    private void attemptLogin() {

        // Reset errors.
        mUsernameTextView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameTextView.getText().toString();
        final String password = mPasswordView.getText().toString();
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        APIClient client = ServiceGenerator.createService(APIClient.class, "", "");
        client.login(username, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                } else {
                    PrefManager prefManager = new PrefManager(LoginActivity.this);
                    prefManager.setFirstTimeLaunch(false);
                    User user = response.body();
                    PomogiteApplication.StoreUser(user,password,LoginActivity.this);
                    finish();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });


    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}