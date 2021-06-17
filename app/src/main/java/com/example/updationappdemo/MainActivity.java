package com.example.updationappdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(
                                result,
                                AppUpdateType.FLEXIBLE,
                                MainActivity.this,
                                RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mAppUpdateManager.registerListener(installStateUpdatedListener);
    }

    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteUpdate();
            }
        }
    };

    @Override
    protected void onStop() {
        if (mAppUpdateManager != null)
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    private void showCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New App Is Ready!!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_APP_UPDATE) {
            Toast.makeText(this, "Downloading Start", Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK){
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}