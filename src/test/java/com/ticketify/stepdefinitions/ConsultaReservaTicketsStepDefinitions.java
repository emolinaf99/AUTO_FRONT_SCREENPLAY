package com.ticketify.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ConsultaReservaTicketsStepDefinitions {

    @Given("el comprador accede a la pagina principal del sistema de ticketing")
    public void elCompradorAccedeALaPaginaPrincipalDelSistemaDeTicketing() {
    }

    @When("el sistema carga el listado de eventos disponibles")
    public void elSistemaLargaElListadoDeEventosDisponibles() {
    }

    @Then("el comprador puede ver al menos un evento con su nombre y fecha de realizacion")
    public void elCompradorPuedeVerAlMenosUnEventoConSuNombreYFechaDeRealizacion() {
    }

    @And("cada evento muestra la cantidad de tickets en estado {string}")
    public void cadaEventoMuestraLaCantidadDeTicketsEnEstado(String estadoTickets) {
    }

    @Given("el comprador se encuentra en la pagina de detalle de un evento con tickets disponibles")
    public void elCompradorSeEncuentraEnLaPaginaDeDetalleDeUnEventoConTicketsDisponibles() {
    }

    @When("selecciona un ticket disponible para reservar")
    public void seleccionaUnTicketDisponibleParaReservar() {
    }

    @And("intenta confirmar la reserva sin ingresar {string}")
    public void intentaConfirmarLaReservaSinIngresar(String campoRequerido) {
    }

    @Then("el sistema no procesa la reserva")
    public void elSistemaNoProcesaLaReserva() {
    }

    @But("muestra un mensaje indicando que {string} es obligatorio para continuar")
    public void muestraUnMensajeIndicandoQueEsObligatorioParaContinuar(String campoRequerido) {
    }
}
