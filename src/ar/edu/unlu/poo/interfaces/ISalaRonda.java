package ar.edu.unlu.poo.interfaces;

import ar.edu.unlu.poo.modelo.Carta;
import ar.edu.unlu.poo.modelo.Jugador;
import ar.edu.unlu.poo.modelo.ManoJugador;
import ar.edu.unlu.poo.modelo.enumerados.AccionesJugador;
import ar.edu.unlu.poo.modelo.enumerados.EstadoDeLaSalaRonda;
import ar.edu.unlu.poo.modelo.enumerados.Mensajes;
import ar.edu.unlu.poo.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.List;

public interface ISalaRonda extends IObservableRemoto {
    boolean hayLugaresDisponibles() throws RemoteException;

    void confirmarParticipacion(Jugador j) throws RemoteException;

    void controladorRepartirCartasIniciales(Jugador j) throws RemoteException;

    void controladorArrancarTurnoDealer(Jugador j) throws RemoteException;

    void pasarAReInscripciones(Jugador j) throws RemoteException;

    void confirmarNuevaParticipacion(Jugador j, double monto, boolean situacion) throws RemoteException;

    Mensajes inscribirJugador(Jugador j, double monto) throws RemoteException;

    Mensajes apostarOtraMano(Jugador j, double monto) throws RemoteException;

    Mensajes retirarUnaMano(Jugador j, ManoJugador mano) throws RemoteException;

    Mensajes retirarmeDeLaSalaRonda(Jugador j) throws RemoteException;

    Mensajes jugarTurno(AccionesJugador accion, Jugador j, ManoJugador m) throws RemoteException;

    List<Carta> getManoDealer() throws RemoteException;

    int getTotalDealer() throws RemoteException;

    EstadoDeLaSalaRonda getEstadoSalaRonda() throws RemoteException;

    String getJugadorTurnoActual() throws RemoteException;

    boolean jugadorInscripto(Jugador j) throws RemoteException;

    boolean confirmoElJugador(Jugador j) throws RemoteException;

    List<Jugador> aquellosQueJuegan() throws RemoteException;

    boolean esMiTurno(Jugador j) throws RemoteException;
}
