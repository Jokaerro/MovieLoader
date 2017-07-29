package tesla.andrew.movieloader.presentation.screen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tesla.andrew.movieloader.R;
import tesla.andrew.movieloader.presentation.application.App;

public class MainActivity extends AppCompatActivity implements MainView {
    private static final int MY_PERMISSIONS_REQUEST = 43;

    private ProgressDialog mProgressDialog;

    @Inject
    MainPresenter mPresenter;

    @BindView(R.id.start_download) Button startButton;

    @OnClick(R.id.start_download)
    void onStartDownloadClick() {
        startButton.setEnabled(false);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
            startButton.setEnabled(true);
        } else {
            mPresenter.startDownloadFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.startDownloadFile();
                } else {
                    makeMessage("Please add permission to write external storage");
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        App.getAppComponent().injectMainActivity(this);

        mPresenter.setView(this);
    }

    @Override
    public void makeMessage(String message) {
        startButton.setEnabled(true);
        hideDownloadDialog();
        Snackbar.make(startButton, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    public void updateProgressState(int state) {
        if(mProgressDialog != null) {
            mProgressDialog.setProgress(state);
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Loading");
            mProgressDialog.setMessage("Loading file ...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMax(100);
            mProgressDialog.show();
            mProgressDialog.setProgress(state);
        }
    }

    @Override
    public void hideDownloadDialog() {
        if(mProgressDialog != null) {
            mProgressDialog.hide();
        }
    }
}