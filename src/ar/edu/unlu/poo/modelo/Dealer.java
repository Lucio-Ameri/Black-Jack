package ar.edu.unlu.poo.modelo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Dealer implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;

    private ManoDealer mano;
    private Baraja baraja;

    public Dealer(){
        this.mano = new ManoDealer();
        this.baraja = new Baraja();
    }


    //metodo: devuelve la mano del dealer.
    public ManoDealer getManoDealer(){
        return mano;
    }


    //metodo: informa si se cumple la condicion para asegurarse.
    public boolean condicionSeguro(){
        return mano.primeraEsAs();
    }


    //metodo: revela la mano del dealer por completo.
    public void revelar(){
        mano.revelarMano();
    }


    //metodo: reparte una carta de la baraja.
    public Carta repartirCarta(){
        if(baraja.barajaVacia()){
            baraja.generarBaraja();
        }

        return baraja.repartir();
    }


    //metodo: calcula los resultados de las manos de los jugadores con respecto a la suya.
    public void calcularResultados(List<Jugador> jugadores){
        for(Jugador j: jugadores){
            List<ManoJugador> manos = j.getManos();

            for(ManoJugador m: manos){

                Apuesta a = m.getEnvite();
                a.calcularGanancias(m.getEstadoDeLaMano(), mano.getEstadoDeLaMano(), mano.getTotalMano(), m.getTotalMano());
            }
        }
    }


    //metodo: reparte la ganancia a un jugador.
    public void repartirGanancia(Jugador j){
        List<ManoJugador> manos = j.getManos();
        Dinero saldoJ = j.getSaldo();
        for(ManoJugador m: manos){
            saldoJ.actualizarMonto(m.getGanancias());
        }
    }


    //metodo: elimina las manos del jugador x;
    public void eliminarManos(Jugador j){
        j.resetearManos();
    }

    public void devolverDineroManos(Jugador j){
        List<ManoJugador> manos = j.getManos();
        Dinero saldoJ = j.getSaldo();

        for(ManoJugador m: manos){
            saldoJ.actualizarMonto(m.getMontoApostado());
        }

        eliminarManos(j);
    }

    public void devolverDineroMano(Jugador j, ManoJugador m){
        Dinero saldoJ = j.getSaldo();

        saldoJ.actualizarMonto(m.getMontoApostado());
    }
}
