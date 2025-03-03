package dam.pmdm.spyrothedragon.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.databinding.ActivityEasterEggBinding;

public class EasterEggActivity extends AppCompatActivity {
    ActivityEasterEggBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEasterEggBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        changeMusicVolume(0.1f);
        binding.videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.spyro);
        binding.videoView.start();

        //Cerrar activity al terminar el vídeo
        binding.videoView.setOnCompletionListener(mp -> finish());
    }

    private void changeMusicVolume(float v) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("VOLUME", v);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        changeMusicVolume(0.5f);
    }
}
