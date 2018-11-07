/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finanzas;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;
import jdk.nashorn.internal.runtime.ECMAErrors;

/**
 *
 * @author loreanaisabel
 */
public class Finanzas {

    Double [][] x; 
    ArrayList<fondoMonetario> fondos = new ArrayList<>();
    static int cantCruzar=50;
    double fitnnesTotal=0;
    double[] proporcionRuleta;
    int[][] parejas;
    static int parada=0;
    
    Finanzas(){     
        /*iniciando datos fondos*/
        Double[] c=new Double[4];
        c[0]=0.00069766; //APIUX
        c[1]=0.00054303; //PRPFX
        c[2]=0.00051542; //MPERX
        c[3]=0.00069178; //AFFIX
        fondoMonetario APIIX= new fondoMonetario("APIIX", 0.00245, 0.00070, c); 
        fondos.add(APIIX);
        
           
        c[0]=0.00069766; //APIIX
        c[1]=0.00054091; //PRPFX
        c[2]=0.00051147; //MPERX
        c[3]=0.00069231; //AFFIX
        fondoMonetario APIUX= new fondoMonetario("APIUX", 0.02064, 0.00070, c);
        fondos.add(APIUX);
        
        
        c[0]=0.00054303; //APIIX
        c[1]=0.00054091; //APIUX
        c[2]=0.00127306; //MPERX
        c[3]=0.00053507; //AFFIX
        fondoMonetario PRPFX= new fondoMonetario("PRPFX", 0.00097, 0.00153, c);
        fondos.add(PRPFX);
        
        
        c[0]=0.00051542; //APIIX
        c[1]=0.00051147; //APIUX
        c[2]=0.00127306; //PRPFX
        c[3]=0.00050670; //AFFIX
        fondoMonetario MPERX= new fondoMonetario("MPERX", -0.00317, 0.00141, c);
        fondos.add(MPERX);
        
   
        c[0]=0.00069178; //APIIX
        c[1]=0.00069231; //APIUX
        c[2]=0.00053507; //PRPFX
        c[3]=0.00050670; //MPERX
        fondoMonetario AFFIX= new fondoMonetario("AFFIX", 0.00170, 0.00069, c);
        fondos.add(AFFIX);
        
        //100 individuos, 5 acciones + fitness
        x = new Double[100][6]; //filas*columnas
        proporcionRuleta= new double[cantCruzar];
        parejas= new int[cantCruzar/2][2];
    }
    
    public void poblacionInicial(){
        double rango; 
        for (int i = 0; i < 100; i++) {
            rango=1;
            for (int j = 0; j < fondos.size(); j++) {
                if (rango>0 && j!=(fondos.size()-1)) {
                   double numero =  numeroDecimales((Math.random() * rango),4);
                   x[i][j]=numero;
                   rango=rango-numero; 
                }else{
                    if (j==fondos.size()-1) {
                        x[i][j]=numeroDecimales(rango,4);
                    }else{
                        x[i][j]=(double) 0;
                    }
                    
                }   
            }
            x[i][fondos.size()]=numeroDecimales(fitness(i),4); //fitness   
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
        /*System.out.println("----------FIlaa "+fila);
        for (int i = 0; i < fondos.size(); i++) {
            for (int j = 0; j < fondos.size(); j++) {
                System.out.print(matriz[i][j]+"  ");
            }
            System.out.println();
        }
        System.out.println("-------------------------Total "+Varrp);*/
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
        //System.out.println(" --Porcentaje del individuo en la Ruleta-- ");
        for (int i = 0; i < filas; i++) {
            proporcionRuleta[i]=numeroDecimales((x[i][fondos.size()])/numeroDecimales(fitnnesTotal, 2), 3);
            //System.out.println(i+" "+proporcionRuleta[i]);
        }
              
    }
    
    public void ruleta(){
        double contador=0;
        int individuo=0;
        int vuelta=0;
        int numPareja=0;
        porcentajeRuleta(cantCruzar);
        //System.out.println(" --------% Ruleta--------- ");
        do {           
            contador=0;
            double numero =  numeroDecimales((Math.random() * 1),3);
           // System.out.println(numero);
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
        
        //System.out.println(" ---Parejas a cruzar--- ");
       /* for (int i = 0; i < (cantCruzar/2); i++) {
            System.out.println(i+" pareja "+parejas[i][0]+" / "+parejas[i][1]);
        }*/
    
    }
    
    public void cruze(){
        int vuelta=0;
        double alfa=numeroDecimales(Math.random()*1,2);
        System.out.print("-------------------------------Cruce------------------------------");
        for (int i = cantCruzar; i < 100; i++) {
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
                x[i][j]=numeroDecimales((x[parejas[vuelta][0]][j]*alfa)+(x[parejas[vuelta][1]][j]*(1-alfa)), 4);
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
        
        System.out.println("Viejo individuo");
        imprimirIndividuo(fila);
        int ceros=0;
        for (int i = 0; i < fondos.size(); i++) {
            suma=numeroDecimales(suma+x[fila][i],4);
            if (x[fila][i]==0) {
                ceros++;
            }
        }
        System.out.println("Fila: "+fila+" suma: "+suma);
        if (suma>1) {
            System.out.println(">suma: "+(suma-1)/(fondos.size()-ceros));
            /*imprimirIndividuo(fila);
            System.out.println("suma: "+suma);*/
            suma=numeroDecimales((suma-1)/(fondos.size()-ceros),8);
            //System.out.println("suma/5: "+suma);
            for (int i = 0; i < fondos.size(); i++) {
                x[fila][i]=numeroDecimales(x[fila][i]-suma,4);
                if (x[fila][i]<0) {
                    x[fila][i]=0.0;
                }
            }
        }else{
            if (suma<1) {
                suma=numeroDecimales((1-suma)/(fondos.size()-ceros),4);
                System.out.println("<suma: "+(1-suma)/(fondos.size()-ceros));
                for (int i = 0; i < fondos.size(); i++) {
                    x[fila][i]=numeroDecimales(x[fila][i]+suma,4);
                }
            }
        }
        
        x[fila][fondos.size()]=numeroDecimales(fitness(fila),4); //fitness
        System.out.println("Nuevo individuo");
        imprimirIndividuo(fila);
        
    }
    
    public void mutacion(){
        for (int j = 0; j < 3; j++) {
            int fila=(int) ((Math.random() * 99)+1);
            int columna= (int) ((Math.random() * 4));
            double valorTotal=0.0;
            double valorNuevo=0.0;
             
            valorNuevo=((Math.random() * (x[fila][columna])));
            x[fila][columna]=numeroDecimales(x[fila][columna]+valorNuevo,4);
            corregirHijo(fila);
            
            x[fila][fondos.size()]=numeroDecimales(fitness(fila),4);

            System.out.println("\n ---- Individuo Mutado ---- ");
            imprimirIndividuo(fila);
        }
    }
    
    public void imprimirPoblacion(String titulo, int filas){
        System.out.println(titulo);
        for (int i = 0; i < filas; i++) {
            System.out.print(i+") APIIX-> "+x[i][0]+" APIUX-> "+x[i][1]+" PRPFX-> "+x[i][2]+" MPERX-> "+x[i][3]+" AFFIX-> "+x[i][4]+" Fitness-> "+x[i][5]);
            double rendimiento=rendimientoPortafolio(i);
            double varianza= rendimiento/x[i][5];
            System.out.print(" Rendimiento-> "+numeroDecimales(rendimiento, 6));
            System.out.println(" VarianzaCalculada-> "+numeroDecimales(varianza, 8));
            
        }
    }
    
    public void imroimirValoresGrafica(String titulo, int filas){
        System.out.println(titulo);
        for (int i = 0; i < filas; i++) {
            double rendimiento=rendimientoPortafolio(i);
            double varianza= rendimiento/x[i][5];
            System.out.print(numeroDecimales(rendimiento, 6)); // rendimiento
            System.out.print(";");
            System.out.println(numeroDecimales(varianza, 10)); // varianza
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
    
    public void escribirSalida(int filas, String titulo){
        FileWriter fichero = null;
        PrintWriter pw = null;
        FileWriter fichero2 = null;
        PrintWriter pw2 = null;
        try
        {
            fichero2 = new FileWriter("Salida del algoritmo/"+titulo+"RendimientoVarianza.txt");
            pw2 = new PrintWriter(fichero2);
            
            fichero = new FileWriter("Salida del algoritmo/"+titulo+"PorcentajesActivos.csv");
            pw = new PrintWriter(fichero);
      
        for (int i = 0; i < filas; i++) {
            pw.print(" APIIX-> ;"+x[i][0]+"; APIUX-> ;"+x[i][1]+"; PRPFX-> ;"+x[i][2]+"; MPERX-> ;"+x[i][3]+"; AFFIX-> ;"+x[i][4]+"; Fitness-> ;"+x[i][5]);
            double rendimiento=rendimientoPortafolio(i);
            double varianza= rendimiento/x[i][5];
            pw.print(" Rendimiento-> "+numeroDecimales(rendimiento, 6));
            pw.println(" VarianzaCalculada-> "+numeroDecimales(varianza, 8));
            
            pw2.print(numeroDecimales(rendimiento, 6)); // rendimiento
            pw2.print(";");
            pw2.print(numeroDecimales(varianza, 10)); // varianza
            pw2.print(";");
            pw2.println(numeroDecimales(x[i][5], 10)); // fitness
        }
        
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
           try {
           if (null != fichero2)
              fichero2.close();
           } catch (Exception e3) {
              e3.printStackTrace();
           }
        }
    }
    
    public static void main(String[] args) {
        Finanzas programa=new Finanzas(); 
        programa.poblacionInicial();
        programa.imprimirPoblacion("Poblacion Inicial",100);
        programa.insercionDirecta(100);
        System.out.println(" ");
        programa.imprimirPoblacion("Ordenar por fitness",100);
        int var=0;
        do {            
            programa.ruleta();
            programa.cruze();
            
            //System.out.println(" ");
            //programa.imprimirPoblacion(" ------Poblacion despues del cruze--------",20);
            programa.insercionDirecta(100);
            System.out.println(" ");
            programa.imprimirPoblacion(" --------Ordenar por fitness------- ",50);
            programa.mutacion();
            programa.insercionDirecta(100);
            var++;
        } while (parada<700);
        
        programa.imprimirPoblacion(" --------Poblacion final------- ",10);
        //programa.imroimirValoresGrafica(" ----Rendimiento----Varianza--- ", 100);
        Scanner reader = new Scanner(System.in);
        int numero = 0;
        System.out.println("Introduce números de ejecucion del algoritmo");
        numero = reader.nextInt();
        programa.escribirSalida(10,"corrida"+numero);
    }
    
}
