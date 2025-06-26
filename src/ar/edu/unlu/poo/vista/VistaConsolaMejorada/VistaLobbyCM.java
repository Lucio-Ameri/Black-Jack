package ar.edu.unlu.poo.vista.VistaConsolaMejorada;

import ar.edu.unlu.poo.controlador.Controlador;
import ar.edu.unlu.poo.interfaces.IVista;
import ar.edu.unlu.poo.modelo.enumerados.Mensajes;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.List;

public class VistaLobbyCM extends JFrame implements IVista {
    private Controlador controlador;

    public VistaLobbyCM(Controlador c){
        this.controlador = c;
    }


    @Override
    public void cambiarVisibilidad(Boolean visibilidad) {

    }

    @Override
    public void cambiarVisibilidad() {

    }

    @Override
    public void actualizarConectados(List<String> jugadoresConectados) {

    }

    @Override
    public void actualizarLabelNotificador(String s) {

    }

    @Override
    public void actualizarSaldoJugador() {

    }

    @Override
    public void actualizarTablero() throws RemoteException {

    }

    @Override
    public void ventanaResultados() {

    }

    @Override
    public void ventanaReInscripcion() {

    }

}
