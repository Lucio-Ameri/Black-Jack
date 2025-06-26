package ar.edu.unlu.poo.interfaces;

import ar.edu.unlu.poo.modelo.Jugador;
import ar.edu.unlu.poo.modelo.SalaRonda;
import ar.edu.unlu.poo.modelo.enumerados.Mensajes;
import ar.edu.unlu.poo.rmimvc.observer.IObservableRemoto;
import ar.edu.unlu.poo.rmimvc.observer.IObservadorRemoto;

import java.rmi.RemoteException;
import java.util.List;

public interface ILobby extends IObservadorRemoto, IObservableRemoto {
    List<Jugador> getJugadoresConectados() throws RemoteException;

    SalaRonda getSalaRonda() throws RemoteException;

    int getLonguitudListaDeEspera() throws RemoteException;

    int cualEsMiPosicionEnLaListaDeEspera(Jugador j) throws RemoteException;

    boolean tengoSolicitudEnEspera(Jugador j) throws RemoteException;

    Mensajes unirmeLobby(Jugador j) throws RemoteException;

    Mensajes irmeLobby(Jugador j) throws RemoteException;

    Mensajes unirmeListaDeEspera(Jugador j, double monto) throws RemoteException;

    Mensajes salirListaDeEspera(Jugador j) throws RemoteException;

    Mensajes unirmeSalaRonda(Jugador j, double monto) throws RemoteException;
}
