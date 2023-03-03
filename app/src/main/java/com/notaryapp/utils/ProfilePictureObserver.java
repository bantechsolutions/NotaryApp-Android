package com.notaryapp.utils;

import java.util.Observable;

public class ProfilePictureObserver extends Observable {

    private String name = "First time i have this Text";

    /**
     *@return the value
     */
    public String getValue() {
        return name;
    }

    /**
     *@param name
     * the value to set
     */
    public void setValue(String name) {
        this.name = name;
        setChanged();
        notifyObservers();
    }
}
