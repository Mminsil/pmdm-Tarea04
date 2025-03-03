package dam.pmdm.spyrothedragon;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;


import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideBinding;
import dam.pmdm.spyrothedragon.databinding.GuideEndBinding;
import dam.pmdm.spyrothedragon.ui.GuideStepFragment;
import dam.pmdm.spyrothedragon.ui.MusicService;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    NavController navController = null;
    private Boolean showGuide = true;
    private GuideBinding guideBinding;
    private GuideEndBinding guideEndBinding;
    private int currentStep = 1;

    //Preferencias para enseñar la guía o no
    private static final String PREFS_NAME = "MyAppPreferences";
    private static final String KEY_SHOW_GUIDE = "showGuide";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        guideBinding = binding.includeGuideLayout;
        guideEndBinding = binding.includeGuideEndLayout;
        setContentView(binding.getRoot());

        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_characters ||
                    destination.getId() == R.id.navigation_worlds ||
                    destination.getId() == R.id.navigation_collectibles) {
                // Para las pantallas de los tabs, no queremos que aparezca la flecha de atrás
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        // Iniciar música de fondo
        startService(new Intent(this, MusicService.class));

        //------Comprobar las preferencias para saber si enseñar la Guia
        // Obtener la instancia de SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Verificar si se debe mostrar la guía
        showGuide = sharedPreferences.getBoolean(KEY_SHOW_GUIDE, true);

        //showGuide = true;

        if (showGuide) {
            initializeGuide();
        } else {
            guideEndBinding.endLayout.setVisibility(View.GONE);
            guideBinding.guideLayout.setVisibility(View.GONE);
        }
    }

    private void initializeGuide() {
        guideBinding.skipGuide.setOnClickListener(this::onSkipGuide);
        guideBinding.guideLayout.setVisibility(View.VISIBLE);
        guideEndBinding.endLayout.setVisibility(View.GONE);
        getSupportActionBar().hide();

        applyGuideButtonStarAnimation(guideBinding.startGuideButton);

        guideBinding.startGuideButton.setOnClickListener(this::startGuide);

        //actualizar las preferencias para que no se vuelva a enseñar la guía
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SHOW_GUIDE, false);
        editor.apply();
    }

    private void onSkipGuide(View view) {
        playSkipSound(() -> {
            guideBinding.guideLayout.setVisibility(View.GONE);
        });
    }

    private void startGuide(View view) {
        playStartSound(() -> {
            guideBinding.guideLayout.setVisibility(View.GONE);
            getSupportActionBar().show();
            currentStep = 2;
            showGuideStep(currentStep);
        });

    }

    private void showGuideStep(int currentStep) {
        String message;
        switch (currentStep) {
            case 2:
                message = "Aquí podrás explorar a todos los personajes del mundo de Spyro.\n" +
                        "Estos son los diferentes personajes con habilidades únicas para tus aventuras.";
                break;
            case 3:
                message = "Aquí podrás explorar los mundos disponibles para tus aventuras.\n" +
                        "Cada mundo ofrece desafíos únicos y emocionantes.";
                break;
            case 4:
                message = "Estos son los coleccionables que puedes encontrar durante el juego.\n" +
                        "¡Encuéntralos todos para desbloquear contenido especial!";
                break;
            case 5:
                message = "Este icono proporciona información útil sobre la aplicación.\n" +
                        "¡Toca para descubrir más!";
                actionBarSimulateIconClickAnimarion(R.id.action_info);
                break;
            default:
                return;
        }
        //Abrimos el GuideStepFragment para los pasos de la guía
        GuideStepFragment guideFragment = GuideStepFragment.newInstance(message, currentStep);
        guideFragment.show(getSupportFragmentManager(), "GuideStepFragment");
    }

    public void nextGuideStep(int currentStep) {
        // Remover el fragmento actual de la guía
        getSupportFragmentManager().popBackStack();
        this.currentStep = currentStep + 1;
        if (this.currentStep == 6) {
            showEndGuide();
            return;
        }
        // Mostrar siguiente paso o finalizar guía
        if (this.currentStep <= 6) {
            showGuideStep(this.currentStep);
        }
    }

    public void previousGuideStep(int currentStep) {
        getSupportFragmentManager().popBackStack();
        if (currentStep > 1) {
            this.currentStep = currentStep - 1;
            showGuideStep(this.currentStep);
        }
    }

    public void showEndGuide() {
        guideEndBinding.endLayout.setVisibility(View.VISIBLE);
        guideBinding.guideLayout.setVisibility(View.GONE);

        applyGuideButtonStarAnimation(guideEndBinding.endGuideButton);

        // Ocultar la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        guideEndBinding.endGuideButton.setOnClickListener(this::closeEndGuide);
    }

    void closeEndGuide(View view) {
        playStartSound(() -> {
            guideBinding.guideLayout.setVisibility(View.GONE);
            guideEndBinding.getRoot().setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
        });
    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);

        if (navController != null) {
            int destinationId;

            if (menuItem.getItemId() == R.id.nav_characters) {
                destinationId = R.id.navigation_characters;
            } else if (menuItem.getItemId() == R.id.nav_worlds) {
                destinationId = R.id.navigation_worlds;
            } else {
                destinationId = R.id.navigation_collectibles;
            }

            // Configuramos las opciones de navegación con animaciones
            NavOptions navOptions = new NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build();

            navController.navigate(destinationId, null, navOptions);
        }
        return true;
    }

    public void selectBottomMenuItem(int itemId) {
        binding.navView.setSelectedItemId(itemId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }

    //--------- Animaciones y Efectos de sonidos-----------------
    private void applyGuideButtonStarAnimation(ImageView button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.5f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f);

        // Configurar la repetición
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);

        // Crear el AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, fadeIn);
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    private void actionBarSimulateIconClickAnimarion(int itemId) {
        final View decorView = getWindow().getDecorView();
        decorView.post(() -> {
            View actionBarIconView = decorView.findViewById(itemId);
            if (actionBarIconView != null) {
                applyIconAnimation(actionBarIconView);
            }
        });
    }

    private void applyIconAnimation(View view) {
        // Escalado con rebote
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f, 1f);

        // Rotación completa
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);

        // Efecto de brillo (alpha)
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.3f, 1f);

        // Rebote con interpolador
        scaleX.setInterpolator(new BounceInterpolator());
        scaleY.setInterpolator(new BounceInterpolator());

        // Todos juntos
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotation, fadeInOut);
        animatorSet.setDuration(1500);
        animatorSet.start();
    }

    private void playStartSound(Runnable action) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.start);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                action.run();
            });
        } else {
            action.run();
        }
    }

    private void playSkipSound(Runnable action) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.cerrar);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                action.run();
            });
        } else {
            action.run();
        }
    }
}