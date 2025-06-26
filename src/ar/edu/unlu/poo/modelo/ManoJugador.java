package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.modelo.enumerados.EstadoDeLaMano;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ManoJugador extends Mano implements Serializable {
    @Serial
    private static final long serialVersionUID = 10L;

    private Apuesta envite;

    public ManoJugador(double monto){
        super();
        this.envite = new Apuesta(monto);
    }


    //metodo: devuelve el monto que fue apostado.
    public double getMontoApostado(){
        return envite.getApostado();
    }


    //metodo: devuelve el monto del seguro apostado.
    public double getMontoSeguro(){
        return envite.getSeguro();
    }


    //metodo: devuelve las ganancias de la mano.
    public double getGanancias(){
        return envite.getGanancias();
    }


    //metodo: devuelve el envite.
    public Apuesta getEnvite(){
        return envite;
    }


    //metodo: informa si se puede realizar la separacion.
    public boolean condicionSepararMano(){
        List<Carta> cartas = getCartasDeLaMano();
        return cartas.get(0).cartasSimilares(cartas.get(1));
    }


    //metodo: cambia el estado a SE_QUEDO.
    public void quedarme(){
        cambiarEstadoDeLaMano(EstadoDeLaMano.SE_QUEDO);
    }


    //metodo: cambia el estado a SE_RINDIO.
    public boolean rendirme(){
        if(manoTurnoInicial()) {
            cambiarEstadoDeLaMano(EstadoDeLaMano.SE_RINDIO);
            return true;
        }

        return false;
    }


    //metodo: si no se aseguro ya, se asegura.
    public boolean asegurarme(){
        if(!envite.asegurado()){
            envite.asegurarse();
            return true;
        }

        return false;
    }


    //metodo: dobla la mano.
    public void doblar(Carta c){
        envite.doblarApuesta();

        recibirCarta(c);
        if(getEstadoDeLaMano() == EstadoDeLaMano.EN_JUEGO){
            cambiarEstadoDeLaMano(EstadoDeLaMano.SE_QUEDO);
        }
    }


    //metodo: separa la mano, devolviendo la nueva mano.
    public ManoJugador separar(){
        List<Carta> cartas = getCartasDeLaMano();

        ManoJugador nM = new ManoJugador(envite.getApostado());
        nM.recibirCarta(cartas.remove(cartas.size() - 1));

        cambiarEstadoDeLaMano(EstadoDeLaMano.TURNO_INICIAL);
        calcularTotal();

        return nM;
    }


    //metodo: actualiza el estado segun el total de la mano.
    @Override
    protected void actualizarEstadoMano(int total) {
        if(total > 21){
            cambiarEstadoDeLaMano(EstadoDeLaMano.SE_PASO);
        }

        else if(total == 21){
            if(manoTurnoInicial()){
                cambiarEstadoDeLaMano(EstadoDeLaMano.BLACKJACK);
            }

            else{
                cambiarEstadoDeLaMano(EstadoDeLaMano.SE_QUEDO);
            }
        }

        else{
            cambiarEstadoDeLaMano(getEstadoDeLaMano());
        }
    }


    //metodo: recibe una carta, actualizando los valores y estados de la mano.
    @Override
    public void recibirCarta(Carta c) {
        List<Carta> cartas = getCartasDeLaMano();
        c.revelarCarta();

        cartas.add(c);
        if(cartas.size() == 3){
            cambiarEstadoDeLaMano(EstadoDeLaMano.EN_JUEGO);
        }

        calcularTotal();
    }
}
