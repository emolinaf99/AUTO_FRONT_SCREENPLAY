package com.ticketify.util;

public final class Credenciales {

    private final String email;
    private final String password;

    public Credenciales(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String toJson() {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
    }
}
