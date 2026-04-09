package com.ticketing.util;

import java.util.UUID;

public final class CredencialesBuilder {

    private static final ThreadLocal<Credenciales> CONTEXTO = new ThreadLocal<>();
    private static final String PASSWORD_FIJO = "Test1234!";

    private CredencialesBuilder() {
    }

    public static Credenciales conEmailUUID() {
        String email = UUID.randomUUID().toString().replace("-", "") + "@test.com";
        return new Credenciales(email, PASSWORD_FIJO);
    }

    public static void guardar(Credenciales credenciales) {
        CONTEXTO.set(credenciales);
    }

    public static Credenciales obtener() {
        return CONTEXTO.get();
    }

    public static void limpiar() {
        CONTEXTO.remove();
    }
}
