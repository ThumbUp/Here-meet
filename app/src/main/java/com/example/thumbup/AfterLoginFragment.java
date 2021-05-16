package com.example.thumbup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AfterLoginFragment extends Fragment {
    public static AfterLoginFragment newInstance() {
        return new AfterLoginFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View afterLoginView = inflater.inflate(R.layout.activity_after_login, container, false);
        return afterLoginView;
    }
}