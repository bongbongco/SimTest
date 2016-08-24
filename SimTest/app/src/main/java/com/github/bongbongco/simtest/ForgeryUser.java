package com.github.bongbongco.simtest;

/**
 * Created by seungyonglee on 2016. 8. 2..
 */
public class ForgeryUser implements UnauthorizeUser {

    public boolean Access() {
        return true;
    }
}
