package dam.pmdm.spyrothedragon.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.databinding.GuideStepBinding;

public class GuideStepFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_STEP = "step";
    private GuideStepBinding binding;
    private MediaPlayer mediaPlayer;

    public static GuideStepFragment newInstance(String message, int step) {
        GuideStepFragment fragment = new GuideStepFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putInt(ARG_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = GuideStepBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Obtenemos los datos pasados por los argumentos
        String message = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : "";
        int step = getArguments() != null ? getArguments().getInt(ARG_STEP) : 0;

        // Asignamos el texto al bocadillo
        binding.guideMessage.setText(message);

        MainActivity activity = (MainActivity) requireActivity();
        if (activity != null) {
            switch (step) {
                case 2:
                    activity.selectBottomMenuItem(R.id.nav_characters);
                    break;
                case 3:
                    activity.selectBottomMenuItem(R.id.nav_worlds);
                    break;
                case 4:
                    activity.selectBottomMenuItem(R.id.nav_collectibles);
                    break;
                case 5:
                    //Volvemos a la pantalla principal de la app
                    activity.selectBottomMenuItem(R.id.nav_characters);
                    break;
            }
        }

        ImageView flechaImagen = new ImageView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 250);
        params.gravity = Gravity.CENTER;
        flechaImagen.setLayoutParams(params);
        flechaImagen.setImageResource(R.drawable.arrow1);

        if (step == 2) addArrowBelow(flechaImagen, 55);
        else if (step == 3) addArrowBelow(flechaImagen, 0);
        else if (step == 4) addArrowBelow(flechaImagen, 315);
        else if (step == 5) addArrowAbove(flechaImagen, 230);

        Animation fadeInBubble = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        binding.bubbleContainer.startAnimation(fadeInBubble);

        applyPulsingAnimation(binding.guideNext);

        if (step == 5) binding.guideNext.setText("Finalizar");

        binding.guideNext.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).nextGuideStep(step);
            // Cierra el diálogo al avanzar
            dismiss();
        });

        binding.guidePrevius.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).previousGuideStep(step);
            // Cierra el diálogo al retroceder
            dismiss();
        });

        binding.guidePrevius.setVisibility(step == 2 ? View.GONE : View.VISIBLE);

        binding.skipGuide.setOnClickListener(this::closeGuideStep);
    }

    private void closeGuideStep(View view) {
        playSkipSound(() -> {
            //Accedo a la actividad contenedora, MainActivity, para tener a sus métodos para enseñar
            //la actionBar y navegar a la pestaña Personajes
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.getSupportActionBar().show();
                mainActivity.selectBottomMenuItem(R.id.navigation_characters);
            }
            //Cierro la guideStep
            dismiss();
        });
    }

    private void applyPulsingAnimation(Button nextButton) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(nextButton, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(nextButton, "scaleY", 1f, 1.2f, 1f);

        // Repetir solo una vez
        scaleX.setRepeatCount(0);
        // Duración de la animación
        scaleX.setDuration(1000);

        scaleY.setRepeatCount(0);
        scaleY.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
    }


    private void addArrowBelow(ImageView flecha, int rotationAngle) {
        flecha.setRotation(rotationAngle);
        // Reproducir sonido
        playArrowSound();

        // Obtener la posición del bubbleContainer para colocar la flecha debajo
        binding.bubbleContainer.post(() -> {
            int[] location = new int[2];
            binding.bubbleContainer.getLocationOnScreen(location);
            int bubbleBottom = location[1] + binding.bubbleContainer.getHeight();

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    250, 250);
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            //Ajuste para que quede pegado
            params.topMargin = bubbleBottom - 5;

            flecha.setLayoutParams(params);
            ((FrameLayout) binding.getRoot()).addView(flecha);
            animateArrow(flecha, rotationAngle, 50f);
        });
    }

    private void addArrowAbove(ImageView flecha, int rotationAngle) {
        flecha.setRotation(rotationAngle);

        // Reproducir sonido
        playArrowSound();

        binding.bubbleContainer.post(() -> {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    250, 250);
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

            // Coloca la flecha justo encima del bubbleContainer
            params.topMargin = binding.bubbleContainer.getTop() - flecha.getLayoutParams().height + 10;

            flecha.setLayoutParams(params);
            ((FrameLayout) binding.getRoot()).addView(flecha, 0);
            animateArrow(flecha, rotationAngle, -50f);
        });
    }

    private void playArrowSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.flecha);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                // Liberar memoria una vez termine
                mp.release();
            });
        }
    }

    private void animateArrow(ImageView flecha, int rotationAngle, float startY) {
        flecha.setRotation(rotationAngle);

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(flecha, "rotation", rotationAngle - 45f, rotationAngle);
        rotationAnim.setDuration(400);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(flecha, "alpha", 0f, 1f);
        fadeIn.setDuration(400);

        ObjectAnimator slideAnim = ObjectAnimator.ofFloat(flecha, "translationY", startY, 0f);
        slideAnim.setDuration(400);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotationAnim, fadeIn, slideAnim);
        animatorSet.start();
    }


    private void playSkipSound(Runnable action) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.cerrar);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

