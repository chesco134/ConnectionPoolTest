package com.mood.inspira.networking;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by labmovil-10 on 3/06/16.
 */
public class IOHandler {

    private DataInputStream entrada;
    private DataOutputStream salida;
    private ByteArrayOutputStream baos;
    private byte[] bytes;
    private int tallaDelBloque;
    private int bytesLeidos;

    public IOHandler(InputStream entrada, OutputStream salida) throws IOException{
        entrada = new DataInputStream(entrada);
        salida = new DataOutputStream(salida);
        tallaDelBloque = 64;
        bytes = new byte[tallaDelBloque];
    }

    public byte[] recibirMensaje() throws IOException {
        int tallaDelMensaje;
        int numBloques;
        int bytesSobrantes;
        baos = new ByteArrayOutputStream();
        while((tallaDelMensaje = leerEntero()) != 0) {
            numBloques = tallaDelMensaje / tallaDelBloque;
            for (int i = 0; i < numBloques; i++) {
                bytesLeidos = entrada.read(bytes);
                baos.write(bytes, 0, bytesLeidos);
            }
            bytesSobrantes = tallaDelMensaje - tallaDelBloque * numBloques;
            if (bytesSobrantes > 0) {
                bytesLeidos = entrada.read(bytes);
                baos.write(bytes, 0, bytesLeidos);
            }
            escribirEntero(0);
        }
        bytes = baos.toByteArray();
        baos.reset();
        return bytes;
    }

    public void escribirMensaje(byte[] mensaje) throws IOException {
        int tallaDelMensaje = mensaje.length;
        int numBloques = tallaDelMensaje/tallaDelBloque;
        int bytesSobrantes;
        for(int i=0; i<numBloques; i++){
            escribirEntero(tallaDelBloque);
            salida.write(mensaje, i*tallaDelBloque, tallaDelBloque);
            leerEntero();
        }
        bytesSobrantes = tallaDelMensaje - tallaDelBloque*numBloques;
        if(bytesSobrantes > 0){
            escribirEntero(bytesSobrantes);
            salida.write(mensaje, numBloques*tallaDelBloque, bytesSobrantes);
            leerEntero();
        }
    }

    public void ajustarTallaDelBloque(int talla){
        tallaDelBloque = talla;
        bytes = new byte[tallaDelBloque];
    }

    public int leerEntero() throws IOException {
        return entrada.readInt();
    }

    public long leerEnteroLargo() throws IOException {
        return entrada.readLong();
    }

    public void escribirEntero(int valor) throws IOException {
        salida.writeInt(valor);
    }

    public void escribirEnteroLargo(long valor) throws IOException{
        salida.writeLong(valor);
    }
}
