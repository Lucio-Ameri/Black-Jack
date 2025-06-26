package ar.edu.unlu.poo.servidor;

import ar.edu.unlu.poo.controlador.Controlador;
import ar.edu.unlu.poo.rmimvc.RMIMVCException;
import ar.edu.unlu.poo.rmimvc.Util;
import ar.edu.unlu.poo.rmimvc.cliente.Cliente;
import ar.edu.unlu.poo.vista.MenuLogginJugador;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ClienteApp {
    public ClienteApp() {

        ArrayList<String> ips = Util.getIpDisponibles();
        String ip = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione la IP en la que escuchará peticiones el cliente", "IP del cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ips.toArray(),
                null
        );
        String port = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que escuchará peticiones el cliente", "Puerto del cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                9999
        );
        String ipServidor = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione la IP en la corre el servidor", "IP del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null
        );
        String portServidor = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que corre el servidor", "Puerto del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                8888
        );

        ArrayList<String> opciones = new ArrayList<>();
        opciones.add("Vista Grafica");
        opciones.add("Vista Consola Mejorada");


        String interfaz = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione como quiere visualizar el juego", "Selector de Vista",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones.toArray(),
                null
        );
        Controlador controlador = new Controlador();

        try {
            Cliente cliente = new Cliente(ip, Integer.parseInt(port), ipServidor, Integer.parseInt(portServidor));
            cliente.iniciar(controlador);

            if (interfaz.equals("Vista Consola Mejorada")) {
                new MenuLogginJugador(controlador, interfaz);
            } else {
                new MenuLogginJugador(controlador, interfaz);
            }

        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null,
                    "Error de conexión remota: " + e.getMessage() +
                            "\nVerifique que el servidor esté en ejecución y los puertos sean correctos",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);

        } catch (RMIMVCException e) {
            JOptionPane.showMessageDialog(null,
                    "Error en la configuración RMI: " + e.getMessage(),
                    "Error RMI",
                    JOptionPane.ERROR_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Formato de puerto inválido: " + e.getMessage(),
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);


        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,
                    "Parámetro inválido: " + e.getMessage(),
                    "Error de Configuración",
                    JOptionPane.ERROR_MESSAGE);


        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error inesperado: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
