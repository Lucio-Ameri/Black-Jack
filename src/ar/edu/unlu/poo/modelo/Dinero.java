package ar.edu.unlu.poo.modelo;

import java.io.Serial;
import java.io.Serializable;

public class Dinero implements Serializable {

    @Serial
    private static final long serialVersionUID = 5L;

    private double monto;

    public Dinero(double cantidad){
        this.monto = cantidad;
    }


    //metodo: devuelve el monto que posee.
    public double getMonto(){
        return monto;
    }


    //metodo: informa si la transferencia es realizable.
    public boolean transferenciaRealizable(double cantidad){
        return monto >= cantidad;
    }


    //metodo: actualiza el monto de la clase.
    public void actualizarMonto(double cantidad){
        monto += cantidad;
    }


    //metodo: informa si posee dinero.
    public boolean tengoDinero(){
        return monto >= 1.0;
    }
}
