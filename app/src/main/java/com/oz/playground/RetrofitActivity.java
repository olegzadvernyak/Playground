package com.oz.playground;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.pm.PackageManager.DONT_KILL_APP;

public class RetrofitActivity extends AppCompatActivity {

    private RequestBlockingInterceptor requestBlockingInterceptor = new RequestBlockingInterceptor();
    private SomeRetrofitService someRetrofitService = getSomeRetrofitService();

    private SomeRetrofitService getSomeRetrofitService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(requestBlockingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .client(okHttpClient)
                .build();
        return retrofit.create(SomeRetrofitService.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        Intent intent = new Intent(this, SomeService.class);
        startService(intent);

        findViewById(R.id.disableSomeServiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPackageManager().setComponentEnabledSetting(
                        new ComponentName(RetrofitActivity.this, SomeService.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        DONT_KILL_APP);
            }
        });

        findViewById(R.id.executeCallButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                someRetrofitService.posts().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        Log.d("oleg", "received response " + response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("oleg", "request failed");
                    }
                });
            }
        });
        findViewById(R.id.enableBlockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBlockingInterceptor.setBlockEnabled(true);
            }
        });
        findViewById(R.id.disableBlockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBlockingInterceptor.setBlockEnabled(false);
            }
        });

    }

}
