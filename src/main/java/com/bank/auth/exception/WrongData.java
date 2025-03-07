package com.bank.auth.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class WrongData extends UsernameNotFoundException {

    public WrongData(String message) {
        super(message);
    }
}
