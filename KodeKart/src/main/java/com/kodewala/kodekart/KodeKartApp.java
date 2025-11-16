package com.kodewala.kodekart;

import com.kodewala.kodekart.service.IKodeKartService;
import com.kodewala.kodekart.service.KodeKartServiceImpl;

public class KodeKartApp {
    
    private IKodeKartService kodeKartService;

    public KodeKartApp() {
        this.kodeKartService = new KodeKartServiceImpl();
    }

    public static void main(String[] args) {
        KodeKartApp app = new KodeKartApp();
        app.start();
    }

    public void start() {
        System.out.println("====================================");
        System.out.println("      Welcome to KodeKart!");
        System.out.println("====================================");

        while (true) {
            if (!kodeKartService.isUserLoggedIn()) {
                kodeKartService.showHomeMenu();
            } else if (kodeKartService.isAdminUser()) {
                kodeKartService.showAdminMenu();
            } else {
                kodeKartService.showUserMenu();
            }
        }
    }
}