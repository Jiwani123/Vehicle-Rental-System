package org.example.rentalsytsem.service;

import org.springframework.stereotype.Service;

@Service
public class passwordEncoding {
    public String encode(String password) {
        return password;
    }

    public boolean matches(String password, String password1) {
        return password.equals(password1);
    }
}