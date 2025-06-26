package ar.edu.unlu.poo.servidor;

import ar.edu.unlu.poo.modelo.Lobby;
import ar.edu.unlu.poo.persistencia.Serializador;
import ar.edu.unlu.poo.rmimvc.RMIMVCException;
import ar.edu.unlu.poo.rmimvc.Util;
import ar.edu.unlu.poo.rmimvc.servidor.Servidor;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServidorApp {
    public ServidorApp() throws RemoteException {
        /*String port = "8888";
        String ip = "127.0.0.1";*/
        ArrayList<String> listaIps = Util.getIpDisponibles();
        String ip = (String) JOptionPane.showInputDialog(
                null,
                "Seleccionar la IP del servidor",
                "IP del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                listaIps.toArray(),
                null
        );
        String port = (String) JOptionPane.showInputDialog(
                null,
                "Seleccionar el puerto del servidor",
                "Puerto del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "8888"
        );

        Lobby modelo = new Lobby();
        modelo.getSalaRonda().agregarObservador(modelo);

        Servidor servidor = new Servidor(ip, Integer.parseInt(port));

        try {
            servidor.iniciar(modelo);

        } catch (RemoteException | RMIMVCException e) {
            e.printStackTrace();
        }
    }
}
