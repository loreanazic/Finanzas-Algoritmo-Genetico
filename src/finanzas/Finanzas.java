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
    ArrayList<fondoMonetario> fondos = new ArrayList<>();
    static int cantCruzar=10;
    double fitnnesTotal=0;
    double[] proporcionRuleta;
    int[][] parejas;
    static int parada=0;
    
    Finanzas(){     
        /*iniciando datos fondos*/
        Double[] c=new Double[4];
        c[0]=0.00041332; //APIUX
        c[1]=0.00031971; //PRPFX
        c[2]=-0.00000410; //MPERX
        c[3]=0.00039653; //AFFIX
        fondoMonetario APIIX= new fondoMonetario("APIIX", 0.00328, 0.00042, c); 
        fondos.add(APIIX);
        
        c[0]=0.00041332; //APIIX
        c[1]=0.00031760; //PRPFX
        c[2]=0.00000201; //MPERX
        c[3]=0.00039417; //AFFIX
        fondoMonetario APIUX= new fondoMonetario("APIUX", 0.01490, 0.00039, c);
        fondos.add(APIUX);
        
        c[0]=0.00031971; //APIIX
        c[1]=0.00031760; //APIUX
        c[2]=0.00021441; //MPERX
        c[3]=0.00030892; //AFFIX
        fondoMonetario PRPFX= new fondoMonetario("PRPFX", 0.00233, 0.00119, c);
        fondos.add(PRPFX);
        
        c[0]=-0.00000410; //APIIX
        c[1]=0.00000201; //APIUX
        c[2]=0.00021441; //PRPFX
        c[3]=-0.00001569; //AFFIX
        fondoMonetario MPERX= new fondoMonetario("MPERX", -0.00317, 0.00141, c);
        fondos.add(MPERX);
        
        c[0]=0.00039653; //APIIX
        c[1]=0.00039417; //APIUX
        c[2]=0.00030892; //PRPFX
        c[3]=-0.00001569; //MPERX
        fondoMonetario AFFIX= new fondoMonetario("AFFIX", 0.00251, 0.00040, c);
        fondos.add(AFFIX);
        
        x = new Double[20][6]; //filas*columnas
        proporcionRuleta= new double[cantCruzar];
        parejas= new int[cantCruzar/2][2];
    }
    
    public void poblacionInicial(){
        double rango; 
        for (int i = 0; i < 20; i++) {
            rango=1;
            for (int j = 0; j < fondos.size(); j++) {
                if (rango>0 && j!=(fondos.size()-1)) {
                   double numero =  numeroDecimales((Math.random() * rango),2);
                   x[i][j]=numero;
                   rango=rango-numero; 
                }else{
                    if (j==fondos.size()-1) {
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
        parada++;
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
    
    public void porcentajeRuleta(int filas){
        fitnnesTotal=0;
        for (int i = 0; i < filas; i++) {
            fitnnesTotal=fitnnesTotal+x[i][fondos.size()];
        }
        System.out.println(" --Porcentaje del individuo en la Ruleta-- ");
        for (int i = 0; i < filas; i++) {
            proporcionRuleta[i]=numeroDecimales((x[i][fondos.size()])/numeroDecimales(fitnnesTotal, 2), 3);
            System.out.println(i+" "+proporcionRuleta[i]);
        }
              
    }
    
    public void ruleta(){
        double contador=0;
        int individuo=0;
        int vuelta=0;
        int numPareja=0;
        porcentajeRuleta(cantCruzar);
        System.out.println(" --------% Ruleta--------- ");
        do {           
            contador=0;
            double numero =  numeroDecimales((Math.random() * 1),3);
            System.out.println(numero);
            for (int i = 0; i < proporcionRuleta.length; i++) {
                contador=contador+proporcionRuleta[i];
                if (contador>=numero) {
                    individuo=i;
                    break;
                }
            }
            if (numPareja==0) {
               parejas[vuelta][numPareja]=individuo;
               numPareja=1;
            }else{
                if (parejas[vuelta][numPareja-1]!=individuo) {
                    parejas[vuelta][numPareja]=individuo;
                    numPareja=0;
                    vuelta++;
                }
            }
            
        } while (vuelta<(cantCruzar/2));
        
        System.out.println(" ---Parejas a cruzar--- ");
        for (int i = 0; i < (cantCruzar/2); i++) {
            System.out.println(i+" pareja "+parejas[i][0]+" / "+parejas[i][1]);
        }
    
    }
    
    public void cruze(){
        int vuelta=0;
        double alfa=numeroDecimales(Math.random()*1,2);
        
        for (int i = cantCruzar; i < 20; i++) {
            if ((i % 2)==0) {
                alfa=numeroDecimales(Math.random()*1,2);
                //System.out.println("1. alfa "+alfa);
            }else{
               // System.out.println("2. alfa "+alfa);
            }
            for (int j = 0; j < fondos.size(); j++) {
               // System.out.println("x1 "+x[parejas[vuelta][0]][j]+" x2 "+x[parejas[vuelta][1]][j]);
                double resul=(x[parejas[vuelta][0]][j]*alfa)+(x[parejas[vuelta][1]][j]*(1-alfa));
               // System.out.println("resultado---> "+ resul);
                x[i][j]=numeroDecimales((x[parejas[vuelta][0]][j]*alfa)+(x[parejas[vuelta][1]][j]*(1-alfa)), 3);
            }
            corregirHijo(i); 
            alfa=numeroDecimales(1-alfa, 2);
            if ((i % 2)!=0) {
                vuelta++; 
               // System.out.println("-vuelta "+vuelta);
            }
        }
    
    }
    
    public void corregirHijo(int fila){
        double suma=0;
        
        for (int i = 0; i < fondos.size(); i++) {
            suma=numeroDecimales(suma+x[fila][i],2);
        }
        
        if (suma>1) {
            /*imprimirIndividuo(fila);
            System.out.println("suma: "+suma);*/
            suma=numeroDecimales((suma-1)/fondos.size(),4);
            //System.out.println("suma/5: "+suma);
            for (int i = 0; i < fondos.size(); i++) {
                x[fila][i]=numeroDecimales(x[fila][i]-suma,3);
                if (x[fila][i]<0) {
                    x[fila][i]=0.0;
                }
            }
        }
        
        x[fila][fondos.size()]=numeroDecimales(fitness(fila),2); //fitness
        imprimirIndividuo(fila);
        
    }
    
    public void mutacion(){
        for (int j = 0; j < 3; j++) {
            int fila=(int) ((Math.random() * 19)+1);
            int columna= (int) ((Math.random() * 4));
            double valorTotal=0.0;
            double valorNuevo=0.0;
            for (int i = 0; i < fondos.size(); i++) {
                valorTotal=valorTotal+x[fila][i];
            }

            if (valorTotal<1) {
                valorNuevo=((Math.random() * (1-valorTotal)));
                x[fila][columna]=numeroDecimales(valorNuevo, 2);
            }else{
                valorNuevo=((Math.random() * (x[fila][columna])));
                x[fila][columna]=numeroDecimales(x[fila][columna]-valorNuevo,3);
            }
            x[fila][fondos.size()]=numeroDecimales(fitness(fila),2);

            System.out.println("\n ---- Individuo Mutado ---- ");
            imprimirIndividuo(fila);
        }
    }
    
    public void imprimirPoblacion(String titulo, int filas){
        System.out.println(titulo);
        for (int i = 0; i < filas; i++) {
            System.out.print(i+") APIIX-> "+x[i][0]+" APIUX-> "+x[i][1]+" PRPFX-> "+x[i][2]+" MPERX-> "+x[i][3]+" AFFIX-> "+x[i][4]+" Fitness-> "+x[i][5]);
            System.out.println(" Rendimiento-> "+numeroDecimales(rendimientoPortafolio(i), 4));
        }
    }
    
    public void imprimirIndividuo(int i){
        System.out.println(i+") APIIX-> "+x[i][0]+" APIUX-> "+x[i][1]+" PRPFX-> "+x[i][2]+" MPERX-> "+x[i][3]+" AFFIX-> "+x[i][4]+" Fitness-> "+x[i][5]);
    }
    
    
    
    public double numeroDecimales(double numero, int decimal){
        BigDecimal bd = new BigDecimal(numero);
        bd = bd.setScale(decimal, RoundingMode.HALF_UP);     
        return bd.doubleValue();
    }
    
    public static void main(String[] args) {
        Finanzas programa=new Finanzas(); 
        programa.poblacionInicial();
        programa.imprimirPoblacion("Poblacion Inicial",20);
        programa.insercionDirecta(20);
        System.out.println(" ");
        programa.imprimirPoblacion("Ordenar por fitness",20);
        int var=0;
        do {            
            programa.ruleta();
            programa.cruze();
            //System.out.println(" ");
            //programa.imprimirPoblacion(" ------Poblacion despues del cruze--------",20);
            programa.insercionDirecta(20);
            System.out.println(" ");
            programa.imprimirPoblacion(" --------Ordenar por fitness------- ",20);
            programa.mutacion();
            programa.insercionDirecta(20);
            var++;
        } while (var<10);
        
        programa.imprimirPoblacion(" --------Poblacion final------- ",20);
    }
    
}
