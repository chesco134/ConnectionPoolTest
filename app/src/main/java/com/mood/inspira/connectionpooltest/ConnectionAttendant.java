package com.mood.inspira.connectionpooltest;

import com.mood.inspira.networking.IOHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by labmovil-10 on 3/06/16.
 */
public class ConnectionAttendant extends Thread {

    private ConnectionPoolService.AccionesServicio acciones;
    private Socket socket;
    private IOHandler ioHandler;
    private boolean VERDADERO;
    private byte[] bytes;
    private String anfitrionRemoto;
    private String nombreDeUsuario;
    private String clave;
    private String comando;
    private String nombreDeLista;

    public ConnectionAttendant(Socket socket, ConnectionPoolService.AccionesServicio acciones) throws IOException {
        this.socket = socket;
        this.acciones = acciones;
        this.ioHandler = new IOHandler(this.socket.getInputStream(), this.socket.getOutputStream());
        anfitrionRemoto = this.socket.getRemoteSocketAddress().toString();
        VERDADERO = true;
    }

    @Override
    public void run(){
        if(autenticacion())
        while(VERDADERO)
        try{
            bytes = ioHandler.recibirMensaje();
            comando = new String(bytes);
            // Do something with the command...
        }catch(IOException e){
            e.printStackTrace();
        }
        cerrarConexion();
    }

    public void terminar(){
        VERDADERO = false;
    }

    private void cerrarConexion(){
        try{
            socket.close();
            acciones.removerAttendant(this);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private boolean autenticacion(){
        try{
            ioHandler.escribirMensaje("¿Quién eres?".getBytes());
            bytes = ioHandler.recibirMensaje();
            nombreDeUsuario = new String(bytes);
            bytes = ioHandler.recibirMensaje();
            clave = new String(bytes);
            nombreDeLista = anfitrionRemoto.concat("-").concat(nombreDeUsuario);
            acciones.agregarAttendant(this);
        }catch(IOException e){
            e.printStackTrace();
        }
        return true;
    }

    public String obtenerNombreDeLista(){
        return nombreDeLista;
    }
}
