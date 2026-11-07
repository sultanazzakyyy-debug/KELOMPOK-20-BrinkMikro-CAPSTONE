/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.config;

/**
 *
 * @author ASUS
 */
import com.mycompany.brinkmikro.app.model.Pengguna;

public class UserSession {
    private static Pengguna currentUser;

    public static void setLoginUser(Pengguna user) {
        currentUser = user;
    }

    public static Pengguna getLoginUser() {
        return currentUser;
    }
    
    public static void logout() {
        currentUser = null;
    }
}
