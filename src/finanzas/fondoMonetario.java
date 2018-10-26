/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finanzas;

/**
 *
 * @author loreanaisabel
 */
public class fondoMonetario {
    String nombre;
    /*Float x; incognita del problema, cuanto invertir?*/
    Double rendimiento;
    Double varianza;
    Double[] covarianza;

    public fondoMonetario(String nombre, Double rendimiento, Double varianza, Double[] covarianza) {
        this.nombre=nombre;
        this.rendimiento=rendimiento;
        this.varianza=varianza;
        this.covarianza=new Double[covarianza.length];
        
        for (int i = 0; i < covarianza.length; i++) {
            this.covarianza[i]=covarianza[i];
        }
        
    }
    
    
    
}
