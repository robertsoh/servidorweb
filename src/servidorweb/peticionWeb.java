/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorweb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

/**
 *
 * @author LaboratorioFISI
 */
public class peticionWeb extends Thread {
    
    int contador = 0;

	final int ERROR = 0;
	final int WARNING = 1;
	final int DEBUG = 2;
        final static String CRLF = "\r\n";

	void depura(String mensaje)
	{
		depura(mensaje,DEBUG);
	}	

	void depura(String mensaje, int gravedad)
	{
		System.out.println(currentThread().toString() + " - " + mensaje);
	}	

	private Socket scliente 	= null;		// representa la petición de nuestro cliente
   	private PrintWriter out 	= null;		// representa el buffer donde escribimos la respuesta
        private DataOutputStream os = null;

   	peticionWeb(Socket ps)
   	{
		depura("El contador es " + contador);		
		contador ++;						
		scliente = ps;
		setPriority(NORM_PRIORITY - 1); // hacemos que la prioridad sea baja
   	}

	public void run() // emplementamos el metodo run
	{
		depura("Procesamos conexion");

		try
		{
			BufferedReader in = new BufferedReader (new InputStreamReader(scliente.getInputStream()));
  			out = new PrintWriter(new OutputStreamWriter(scliente.getOutputStream(),"8859_1"),true) ;


			String cadena = "";		// cadena donde almacenamos las lineas que leemos
			int i=0;				// lo usaremos para que cierto codigo solo se ejecute una vez
	
			do			
			{
                            cadena = in.readLine();

                            if (cadena != null )
                            {
                                    // sleep(500);
                                    depura("--" + cadena + "-");
                            }


                            if(i == 0) // la primera linea nos dice que fichero hay que descargar
                            {
                                i++;

                                StringTokenizer st = new StringTokenizer(cadena);

                                if ((st.countTokens() >= 2) && st.nextToken().equals("GET")) 
                                {
                                    retornaFichero(st.nextToken()) ;
                                }
                                else 
                                {
                                    out.println("400 Petición Incorrecta") ;
                                }
                            }
				
			}
			while (cadena != null && cadena.length() != 0);
		}
		catch(Exception e)
		{
			depura("Error en servidor\n" + e.toString());
		}			
		depura("Hemos terminado");
	}
	
	
	void retornaFichero(String sfichero)
	{
		depura("Recuperamos el fichero " + sfichero);
		
		// comprobamos si tiene una barra al principio
		if (sfichero.startsWith("/"))
		{
                    sfichero = sfichero.substring(1) ;
		}
        
                // si acaba en /, le retornamos el index.htm de ese directorio
                // si la cadena esta vacia, no retorna el index.htm principal
                if (sfichero.endsWith("/") || sfichero.equals(""))
                {
                        sfichero = sfichero + "index.htm" ;
                }
                
                try
                {
                    
                    
		    // Ahora leemos el fichero y lo retornamos
		    File mifichero = new File(sfichero) ;
                    
                    
		    if (mifichero.exists()) 
		    {
	      		out.println("HTTP/1.1 200 ok");
                            out.println("Server: Roberto Server/1.0");
                            out.println("Date: " + new Date());
                            out.println("Content-Type: text/html");
                            out.println("Content-Length: " + mifichero.length());
                            out.println("\n");

                            BufferedReader ficheroLocal = new BufferedReader(new FileReader(mifichero));


                            String linea = "";

                            do			
                            {
                                    linea = ficheroLocal.readLine();

                                    if (linea != null )
                                    {
                                            // sleep(500);
                                            out.println(linea);
                                    }
                            }
                            while (linea != null);

                            depura("fin envio fichero");

                            ficheroLocal.close();
                            out.close();
				
			}  // fin de si el fiechero existe 
			else
			{
                            depura("No encuentro el fichero " + mifichero.toString());	
                            out.println("HTTP/1.0 400 ok");
                            out.close();
			}
                        
                    /*
                    os = new DataOutputStream(scliente.getOutputStream());
                    String lineaDeEstado = "";
                    String lineaDeTipoContenido = null;
                    String cuerpoMensaje = null;
                    if (mifichero.exists()) 
		    {
                        BufferedReader ficheroLocal = new BufferedReader(new FileReader(mifichero));
                        lineaDeEstado = ficheroLocal.readLine();
                        lineaDeTipoContenido = "Content-type: " + contentType(mifichero.toString()) + CRLF;
                    }
                    else{
                        lineaDeEstado = "";
                        lineaDeTipoContenido = "";
                        cuerpoMensaje = "<HTML>" + 
                                "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
                                "<BODY><b>404</b> Not Found</BODY></HTML>";
                    }                   
			
                    // Envia la línea de estado.
                    os.writeBytes(lineaDeEstado);

                    // Envía el contenido de la línea content-type.
                    os.writeBytes(lineaDeTipoContenido);

                    // Envía una línea en blanco para indicar el final de las líneas de header.
//                    os.writeBytes(CRLF);
                    */
		}
		catch(Exception e)
		{
			depura("Error al retornar fichero");	
		}

	}
        
        private static String contentType(String nombreArchivo)
        {
            if(nombreArchivo.endsWith(".htm") || nombreArchivo.endsWith(".html")) {
                    return "text/html";
            }
            if(nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg")) {
                    return "image/jpeg";
            }
            if(nombreArchivo.endsWith(".gif")) {
                   return "image/gif";
            }
            if(nombreArchivo.endsWith(".png")) {
                   return "image/png";
            }
            return "application/octet-stream";
        }

    
}
