/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorweb;

import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author LaboratorioFISI
 */
public class ServidorWeb {

    /**
     * @param args the command line arguments
     */
    int puerto = 90; //1989, 90
	 	 	 	
	final int ERROR = 0;
	final int WARNING = 1;
	final int DEBUG = 2;
	
		
	// funcion para centralizar los mensajes de depuracion

	void depura(String mensaje)
	{
		depura(mensaje,DEBUG);
	}	

	void depura(String mensaje, int gravedad)
	{
		System.out.println("Mensaje: " + mensaje);
	}	

    
    public static void main(String[] array) {
        // TODO code application logic here
        ServidorWeb instancia = new ServidorWeb(array);	
		instancia.arranca();

    }
    
    // constructor que interpreta los parameros pasados
	ServidorWeb(String[] param)
	{
		procesaParametros();	
	}
	
	// parsearemos el fichero de entrada y estableceremos las variables de clase
	boolean procesaParametros()
	{
		return true;	
	}
	
	boolean arranca()
	{
		depura("Arrancamos nuestro servidor",DEBUG);
		
		try
		{
		
			
			ServerSocket s = new ServerSocket(puerto);
                            
			depura("Quedamos a la espera de conexion");
			
			while(true)  // bucle infinito .... ya veremos como hacerlo de otro modo
			{
				Socket entrante = s.accept();
				peticionWeb pCliente = new peticionWeb(entrante);
				pCliente.start();
			}
			
		}
		catch(Exception e)
		{
			depura("Error en servidor\n" + e.toString());
		}
		
		return true;
	}
    
}
