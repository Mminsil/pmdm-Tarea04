package dam.pmdm.spyrothedragon.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.ContextThemeWrapper;
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
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 💡 Fondo transparente
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String message = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : "";
        int step = getArguments() != null ? getArguments().getInt(ARG_STEP) : 0;

        // Asignar el texto al bocadillo
        binding.guideMessage.setText(message);

        if (step == 3) {
            ((MainActivity) requireActivity()).selectBottomMenuItem(R.id.navigation_worlds);
        } else if (step == 4) {
            ((MainActivity) requireActivity()).selectBottomMenuItem(R.id.navigation_collectibles);
        } else if (step == 5) {
            ((MainActivity) requireActivity()).selectMenuItem(R.id.action_info);
        }

        ImageView flecha = new ImageView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 250);
        params.gravity = Gravity.CENTER;
        flecha.setLayoutParams(params);
        flecha.setImageResource(R.drawable.arrow1);

        if (step == 2) addArrowBelow(flecha, 55);
        else if (step == 3) addArrowBelow(flecha, 0);
        else if (step == 4) addArrowBelow(flecha, 315);
        else if (step == 5) addArrowAbove(flecha, 230);

        Animation fadeInBubble = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        binding.bubbleContainer.startAnimation(fadeInBubble);

        applyBlinkingAnimation(binding.guideNext);

        if (step == 5) binding.guideNext.setText("Finalizar");

        binding.guideNext.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).nextGuideStep(step);
            dismiss(); // Cierra el diálogo al avanzar
        });

        binding.guidePrevius.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).previousGuideStep(step);
            dismiss(); // Cierra el diálogo al retroceder
        });

        binding.guidePrevius.setVisibility(step == 2 ? View.GONE : View.VISIBLE);
    }

    private void applyBlinkingAnimation(Button nextButton) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(nextButton, "alpha", 1f, 0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(nextButton, "alpha", 0f, 1f);
        fadeOut.setDuration(300);
        fadeIn.setDuration(300);
        fadeOut.setRepeatCount(2);
        fadeIn.setRepeatCount(2);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeOut, fadeIn);
        animatorSet.start();
    }

    private void addArrowBelow(ImageView flecha, int rotationAngle) {
        flecha.setRotation(rotationAngle);

        // Obtener la posición del bubbleContainer para colocar la flecha justo debajo
        binding.bubbleContainer.post(() -> {
            int[] location = new int[2];
            binding.bubbleContainer.getLocationOnScreen(location);
            int bubbleBottom = location[1] + binding.bubbleContainer.getHeight();

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    250, 250); // Ajusta el tamaño si es necesario
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.topMargin = bubbleBottom - 5; // 💡 Ajuste fino para que quede pegado

            flecha.setLayoutParams(params);
            ((FrameLayout) binding.getRoot()).addView(flecha);
            animateArrow(flecha, rotationAngle, 50f); // Animación más sutil
        });
    }



    private void addArrowAbove(ImageView flecha, int rotationAngle) {
        flecha.setRotation(rotationAngle);

        binding.bubbleContainer.post(() -> {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    250, 250); // Ajusta el tamaño si es necesario
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

            // 💡 Coloca la flecha justo encima del bubbleContainer
            params.topMargin = binding.bubbleContainer.getTop() - flecha.getLayoutParams().height + 10; // Ajuste fino

            flecha.setLayoutParams(params);
            ((FrameLayout) binding.getRoot()).addView(flecha, 0);
            animateArrow(flecha, rotationAngle, -50f);
        });
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 💧 Evitar fugas de memoria
    }
}

