package com.pl.edu.prz.robotui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    SharedPreferences sp;
    Switch darkThemeSwitch;
    SeekBar joystickSensXY, joystickSensZ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.settings_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        darkThemeSwitch = view.findViewById(R.id.dark_theme_switch);
        joystickSensXY = view.findViewById(R.id.seekbar_joystick_XY);
        joystickSensZ = view.findViewById(R.id.seekbar_joystick_Z);

        darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    editor.putBoolean("darkTheme", true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    editor.putBoolean("darkTheme", false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                editor.apply();
            }
        });

        joystickSensXY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editor.putInt("sensXY", i);
                Log.e("XY", String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO: implement joystick XY sensitivity
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.apply();
            }
        });

        joystickSensZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editor.putInt("sensZ", i);

                Log.e("Z", String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO: implement joystick Z sensitivity
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.apply();
            }
        });
        loadSettings();
    }


    private void loadSettings() {
        boolean darkTheme = sp.getBoolean("darkTheme", false);
        darkThemeSwitch.setChecked(darkTheme);
        int sensXY = sp.getInt("sensXY", 0);
        int sensZ = sp.getInt("sensZ", 0);
        Log.e("loadSettings", sensXY + " " + sensZ);
        joystickSensXY.setProgress(sensXY);
        joystickSensZ.setProgress(sensZ);
    }

}

