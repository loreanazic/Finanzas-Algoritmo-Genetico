/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finanzas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 *
 * @author loreanaisabel
 */
public class Finanzas {

    Double [][] x; 
    ArrayList<fondoMonetario> fondos = new ArrayList<fondoMonetario>();
    
    Finanzas(){     
        /*iniciando datos fondos*/
        Double[] c=new Double[2];
        c[0]=0.00041332; //APIUX
        c[1]=0.00031971; //PRPFX
        fondoMonetario APIIX= new fondoMonetario("APIIX", 0.00328, 0.00042, c); 
        fondos.add(APIIX);
        
        c[0]=0.00041332; //APIIX
        c[1]=0.00031760; //PRPFX
        fondoMonetario APIUX= new fondoMonetario("APIUX", 0.01490, 0.00041, c);
        fondos.add(APIUX);
        
        c[0]=0.00031971; //APIIX
        c[1]=0.00031760; //APIUX
        fondoMonetario PRPFX= new fondoMonetario("PRPFX", 0.00233, 0.00091, c);
        fondos.add(PRPFX);
        
        x = new Double[20][4]; //filas*columnas
    }
    
    public void poblacionInicial(){
        double rango; 
        for (int i = 0; i < 20; i++) {
            rango=1;
            for (int j = 0; j < 3; j++) {
                if (rango>0 && j!=2) {
                   double numero =  numeroDecimales((Math.random() * rango),2);
                   x[i][j]=numero;
                   rango=rango-numero; 
                }else{
                    if (j==2) {
                        x[i][j]=numeroDecimales(rango,2);
                    }else{
                        x[i][j]=(double) 0;
                    }
                    
                }   
            }
            x[i][fondos.size()]=numeroDecimales(fitness(i),2); //fitness   
        }   
    }
    
    public double fitness(int fila){
        
        double Er=rendimientoPortafolio(fila);
        double Varrp=matrizVarianzaRendimiento(fila);
        
        double fitness=Er/Varrp;
        
        return fitness;
    }
    
    public double rendimientoPortafolio(int fila){
        double Er=0;
        
        for (int i = 0; i < fondos.size(); i++) {
            Er=Er+x[fila][i]*fondos.get(i).rendimiento;
        }
        return Er;
    }
    
    public double matrizVarianzaRendimiento(int fila){
        Double[][] matriz=new Double[fondos.size()][fondos.size()];
        double Varrp=0;
        
        for (int i = 0; i < fondos.size(); i++) {
            int cov=0;
            for (int j = 0; j < fondos.size(); j++) {
                if (i==j) {
                    matriz[i][j]=(x[fila][j]*x[fila][j])*fondos.get(i).varianza;
                    Varrp=Varrp+matriz[i][j];
                }else{
                    matriz[i][j]=(x[fila][i]*x[fila][j])*fondos.get(i).covarianza[cov];
                    Varrp=Varrp+matriz[i][j];
                    cov++;
                }
            }
        }
        return Varrp;
    }
    
    public void insercionDirecta(int filas){
        int p, j;
        Double[] aux= new Double[fondos.size()+1];
        
        for (p = 1; p < filas; p++){ // desde el segundo elemento hasta
            for (int i = 0; i < fondos.size()+1; i++) {
                aux[i] = x[p][i]; // el final, guardamos el elemento y
            } 
            j = p - 1; // empezamos a comprobar con el anterior
            while ((j >= 0) && (aux[fondos.size()] > x[j][fondos.size()])){ 
                for (int i = 0; i < fondos.size()+1; i++) {
                    x[j+1][i]=x[j][i];
                }     
                j--;                   
            }
            for (int i = 0; i < fondos.size()+1; i++) {
                x[j+1][i]=aux[i];
            }
        }
    }
    
    public void imprimirPoblacion(String titulo){
        System.out.println(titulo);
        for (int i = 0; i < 20; i++) {
            System.out.print(i+") APIIX-> "+x[i][0]+" APIUX-> "+x[i][1]+" PRPFX-> "+x[i][2]+" Fitness-> "+x[i][3]);
            System.out.println(" Rendimiento-> "+numeroDecimales(rendimientoPortafolio(i), 4));
        }
    }
    
    public double numeroDecimales(double numero, int decimal){
        BigDecimal bd = new BigDecimal(numero);
        bd = bd.setScale(decimal, RoundingMode.HALF_UP);     
        return bd.doubleValue();
    }
    
    public static void main(String[] args) {
        Finanzas programa=new Finanzas(); 
        programa.poblacionInicial();
        programa.imprimirPoblacion("Poblacion Inicial");
        programa.insercionDirecta(20);
        programa.imprimirPoblacion("Ordenar por fitness");
    }
    
}
