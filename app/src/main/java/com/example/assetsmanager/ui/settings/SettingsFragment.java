package com.example.assetsmanager.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.assetsmanager.R;
import com.example.assetsmanager.util.Constants;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SettingsFragment extends Fragment {
    private Spinner spinnerLanguages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        spinnerLanguages = root.findViewById(R.id.spinner_language);
        MaterialButton btnSave = root.findViewById(R.id.btn_save_language);

        List<String> languages = new ArrayList<>();
        languages.add(getString(R.string.serbian));
        languages.add(getString(R.string.english));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, languages);
        spinnerLanguages.setAdapter(adapter);

        SharedPreferences shPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String lang = shPreferences.getString(Constants.SELECTED_LANGUAGE, Locale.getDefault().getLanguage());

        if(lang.equals(Constants.langCodes[0]))
            spinnerLanguages.setSelection(0);
        else
            spinnerLanguages.setSelection(1);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerLanguages.getSelectedItem().equals(getString(R.string.serbian)))
                    setLocale("sr");
                else
                    setLocale("en");

                getActivity().recreate();
            }
        });

        return root;
    }

    private void setLocale(String language) {
        SharedPreferences shPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = shPreferences.edit();
        editor.putString(Constants.SELECTED_LANGUAGE, language);
        editor.apply();

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        requireActivity().getResources().updateConfiguration(config,
                requireActivity().getResources().getDisplayMetrics());
    }

}