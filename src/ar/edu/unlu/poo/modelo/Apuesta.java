package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.modelo.enumerados.EstadoDeLaMano;
import ar.edu.unlu.poo.modelo.enumerados.SituacionApuesta;

import java.io.Serial;
import java.io.Serializable;

public class Apuesta implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Dinero apostado;
    private Dinero seguro;
    private Dinero ganancias;
    private SituacionApuesta estado;

    public Apuesta(double monto){
        this.apostado = new Dinero(monto);
        this.seguro = null;
        this.ganancias = null;
        this.estado = SituacionApuesta.CALCULANDO;
    }


    //metodo: devuelve el monto que fue apostado.
    public double getApostado(){
        return apostado.getMonto();
    }


    //metodo: devuelve el estado de la apuesta.
    public SituacionApuesta getEstado(){
        return estado;
    }


    //metodo: devuelve el seguro de la apuesta.
    public double getSeguro(){
        if(asegurado()){
            return seguro.getMonto();
        }

        return  0.0;
    }


    //metodo: devuelve el dinero neto que se obtuvo con esta mano.
    public double getGanancias(){
        if(estado == SituacionApuesta.CALCULANDO){
            return 0.0;
        }

        return ganancias.getMonto();
    }


    //metodo: informa si la mano se aseguro.
    public boolean asegurado(){
        return seguro != null;
    }


    //metodo: asegura la mano.
    public void asegurarse(){
        this.seguro = new Dinero(apostado.getMonto() / 2.0);
    }


    //metodo: dobla la apuesta de la mano.
    public void doblarApuesta(){
        apostado.actualizarMonto(apostado.getMonto());
    }


    //metodo: calcula el resultado de la mano con respecto al del dealer.
    public void calcularGanancias(EstadoDeLaMano estadoJ, EstadoDeLaMano estadoD, int totalDealer, int totalJugador){
        this.ganancias = new Dinero( 0.0);

        if(estadoJ != EstadoDeLaMano.SE_RINDIO){

            switch (estadoD){
                case BLACKJACK -> {
                    if(estadoJ == EstadoDeLaMano.BLACKJACK){
                        estado = SituacionApuesta.EMPATO;
                        ganancias.actualizarMonto(getApostado());
                    }

                    else{
                        estado = SituacionApuesta.PERDIO;
                    }

                    if(asegurado()){
                        ganancias.actualizarMonto(getSeguro() * 2.0);
                    }
                }

                case SE_PASO -> {
                    if(estadoJ != EstadoDeLaMano.SE_PASO){
                        estado = SituacionApuesta.GANO;

                        if(estadoJ == EstadoDeLaMano.BLACKJACK){
                            ganancias.actualizarMonto(getApostado() + (getApostado() * 1.5));
                        }

                        else{
                            ganancias.actualizarMonto(getApostado() * 2.0);
                        }
                    }

                    else{
                        estado = SituacionApuesta.PERDIO;
                    }
                }

                case SE_QUEDO -> {
                    if(estadoJ == EstadoDeLaMano.SE_QUEDO){
                        if(totalDealer > totalJugador){
                            estado = SituacionApuesta.PERDIO;
                        }

                        else if(totalJugador == totalDealer){
                            estado = SituacionApuesta.EMPATO;
                            ganancias.actualizarMonto(getApostado());
                        }

                        else{
                            estado = SituacionApuesta.GANO;
                            ganancias.actualizarMonto(getApostado() * 2.0);
                        }
                    }

                    else if(estadoJ == EstadoDeLaMano.BLACKJACK){
                        estado = SituacionApuesta.GANO;
                        ganancias.actualizarMonto(getApostado() + (getApostado() * 1.5));
                    }

                    else{
                        estado = SituacionApuesta.PERDIO;
                    }
                }
            }
        }

        else{
            estado = SituacionApuesta.RENDIDO;
            ganancias.actualizarMonto(getApostado() / 2.0);
        }
    }
}
