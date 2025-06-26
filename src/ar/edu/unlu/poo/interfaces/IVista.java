package ar.edu.unlu.poo.interfaces;

import ar.edu.unlu.poo.modelo.Jugador;
import ar.edu.unlu.poo.modelo.enumerados.Mensajes;

import java.rmi.RemoteException;
import java.util.List;

public interface IVista {

    void cambiarVisibilidad(Boolean visibilidad);

    void cambiarVisibilidad();

    void actualizarConectados(List<String> jugadoresConectados);

    void actualizarLabelNotificador(String s);

    void actualizarSaldoJugador();

    void actualizarTablero() throws RemoteException;

    void ventanaResultados() throws RemoteException;

    void ventanaReInscripcion() throws RemoteException;
}
