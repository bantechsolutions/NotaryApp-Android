package com.notaryapp.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.notaryapp.R;

public class FragmentViewUtil {

    public static void replaceFragment(FragmentActivity activity,
                                       int contentId, Fragment fragment, boolean shouldAddToStack) {
        try {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(contentId, fragment, fragment.getClass().getSimpleName());
            if (shouldAddToStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }catch (Exception e){

        }
    }

    public static void poptoRoot(FragmentActivity activity){

        FragmentManager fm = activity.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public static void loadFragment(FragmentActivity activity, int contentId, Fragment fragment) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.add(contentId, fragment, fragment.getClass().getSimpleName());
        transaction.commit();
    }

    public static void removeFragment(FragmentActivity activity, Fragment fragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();
    }


    public static void showFragment(FragmentActivity activity, Fragment fragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .show(fragment)
                .commit();
    }
}
