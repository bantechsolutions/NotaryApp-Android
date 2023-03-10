package com.notaryapp.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Foreground implements Application.ActivityLifecycleCallbacks{

    public static final String TAG = Foreground.class.getName();
    public static final long CHECK_DELAY = 10000;

    public interface Listener {
        public void onBecameForeground();
        public void onBecameBackground();
    }

    public interface Binding {
        public void unbind();
    }

    private interface Callback {
        public void invoke(Listener listener);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // if we're changing configurations we aren't going background so
        // no need to schedule the check

           // //Log.i(TAG, "onActivityPaused :: " + activity);

            if (!activity.isChangingConfigurations()) {
                // don't prevent activity being gc'd
                final WeakReference<Activity> ref = new WeakReference<>(activity);
                handler.postDelayed(check = new Runnable() {
                    @Override
                    public void run() {
                        onActivityCeased(ref.get());
                    }
                }, CHECK_DELAY);
            }

    }

    private static Callback becameForeground = new Callback() {
        @Override
        public void invoke(Listener listener) {
            listener.onBecameForeground();
        }
    };

    private static Callback becameBackground = new Callback() {
        @Override
        public void invoke(Listener listener) {
            listener.onBecameBackground();
        }
    };

    private static Foreground instance;

    private boolean foreground;
    private Activity current;
    private Listeners listeners = new Listeners();
    private Handler handler = new Handler();
    private Runnable check;

    public static Foreground init(Application application){
        if (instance == null) {
            instance = new Foreground();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static Foreground get(Application application){
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static Foreground get(){
        if (instance == null) {
            throw new IllegalStateException(
                    "Foreground is not initialised - first invocation must use parameterised init/get");
        }
        return instance;
    }

    public boolean isForeground(){
        return foreground;
    }

    public boolean isBackground(){
        return !foreground;
    }

    public Binding addListener(Listener listener){
        return listeners.add(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityStarted(Activity activity) {
        current = activity;
        // remove any scheduled checks since we're starting another activity
        // we're definitely not going background
        if (check != null) {
            handler.removeCallbacks(check);
        }
        ////Log.i(TAG, "onActivityStarted :: "+current);
       // //Log.i(TAG, "onActivityStarted :: "+check);

        // check if we're becoming foreground and notify listeners
        if (!foreground && (activity != null && !activity.isChangingConfigurations())){
            foreground = true;
           // //Log.i(TAG, "became foreground");
            listeners.each(becameForeground);
        } else {
            ////Log.i(TAG, "still foreground");
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ////Log.i(TAG, "onActivityStopped :: "+activity);

        if (check != null) {
            handler.removeCallbacks(check);
        }
        onActivityCeased(activity);
    }

    private void onActivityCeased(Activity activity){

        ////Log.i(TAG, "onActivityCeased :: "+activity);

        if (foreground) {
            if ((activity == current) && (activity != null && !activity.isChangingConfigurations())){
                foreground = false;
               // //Log.i(TAG, "went background");
                listeners.each(becameBackground);
            } else {
               // //Log.i(TAG, "still foreground");
            }
        } else {
           // //Log.i(TAG, "still background");
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}

    private static class Listeners {
        private List<WeakReference<Listener>> listeners = new CopyOnWriteArrayList<>();

        public Binding add(Listener listener){
            final WeakReference<Listener> wr = new WeakReference<>(listener);
            listeners.add(wr);
            return new Binding(){
                public void unbind() {
                    listeners.remove(wr);
                }
            };
        }

        public void each(Callback callback){
            for (Iterator<WeakReference<Listener>> it = listeners.iterator(); it.hasNext();) {
                try {
                    WeakReference<Listener> wr = it.next();
                    Listener l = wr.get();
                    if (l != null)
                        callback.invoke(l);
                    else
                        it.remove();
                } catch (Exception exc) {
                   // //Log.e(TAG, "Listener threw exception!", exc);
                }
            }
        }
    }
}
