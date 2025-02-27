package dam.pmdm.spyrothedragon.ui;

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

        binding.videoView.setVideoPath("android.resource://" +getPackageName()+"/" + R.raw.spyro);
        binding.videoView.start();

        // 🎯 🔄 Cerrar activity al terminar el vídeo
        binding.videoView.setOnCompletionListener(mp -> finish());
    }
}
