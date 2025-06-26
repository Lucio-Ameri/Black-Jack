package ar.edu.unlu.poo.persistencia;

import ar.edu.unlu.poo.modelo.Jugador;

import java.io.*;
import java.util.ArrayList;

public class Serializador {
    private static final String ARCHIVO_JUGADORES_GUARDADOS = "jugadoresGuardados.dat";
    private static final String ARCHIVO_RECORDS_HISTORICOS = "recordsHistoricos.dat";
    private static final String ARCHIVO_NOMBRES_USADOS = "nombresYaUsados.dat";




    public static void guardarJugador(ArrayList<Jugador> j) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_JUGADORES_GUARDADOS))) {
            salida.writeObject(j);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void eliminarJugadorGuardado(Jugador j){
        ArrayList<Jugador> jugadores = cargarJugadoresGuardados();

        for(Jugador jug: jugadores){
            if(j.getNombre().equals(jug.getNombre())){

                jugadores.remove(j);
            }
        }

        guardarJugador(jugadores);
    }

    public static ArrayList<Jugador> cargarJugadoresGuardados() {
        File archivo = new File(ARCHIVO_JUGADORES_GUARDADOS);
        if (archivo.exists()) {
            try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ARCHIVO_JUGADORES_GUARDADOS))) {
                return (ArrayList<Jugador>) entrada.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }





    public static void guardarRecordsHistoricos(RecordsHistoricos record) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_RECORDS_HISTORICOS))) {
            salida.writeObject(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RecordsHistoricos cargarRecordsHistoricos() {
        File archivo = new File(ARCHIVO_RECORDS_HISTORICOS);
        if (archivo.exists()) {
            try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ARCHIVO_RECORDS_HISTORICOS))) {
                return (RecordsHistoricos) entrada.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new RecordsHistoricos();
    }








    public static void guardarListaNombresUsados(ArrayList<String> nombres) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_NOMBRES_USADOS))) {
            salida.writeObject(nombres);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> cargarListaNombresUsados() {
        File archivo = new File(ARCHIVO_NOMBRES_USADOS);
        if (archivo.exists()) {
            try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ARCHIVO_NOMBRES_USADOS))) {
                return (ArrayList<String>) entrada.readObject();

            }

            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}
