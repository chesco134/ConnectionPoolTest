package com.mood.inspira.connectionpooltest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by labmovil-10 on 3/06/16.
 */
public class ConnectionPoolService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final IBinder mBinder = new LocalBinder();
    private AccionesServicio accionesDeServicio;
    private MainActivity.ActivityActions acciones;
    private ConcurrentLinkedQueue<ConnectionAttendant> attendants;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        ConnectionPoolService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ConnectionPoolService.this;
        }
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        accionesDeServicio = new AccionesServicio();
        attendants = new ConcurrentLinkedQueue<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public void setActivityActions(MainActivity.ActivityActions acciones){
        this.acciones = acciones;
    }

    public ConnectionAttendant obtenerAsistente(String attendantString){
        return accionesDeServicio.obtenerAsistente(attendantString);
    }

    public void removerAttendant(ConnectionAttendant attendant){
        accionesDeServicio.removerAttendant(attendant);
    }

    public class AccionesServicio {

        public void agregarAttendant(ConnectionAttendant attendant) {
            attendants.add(attendant);
            acciones.agregarHost(attendant.obtenerNombreDeLista());
        }

        public void removerAttendant(ConnectionAttendant attendant) {
            attendant.terminar();
            attendants.remove(attendant);
            acciones.removerHost(attendant.obtenerNombreDeLista());
        }

        public ConnectionAttendant obtenerAsistente(String attendantString){
            ConnectionAttendant attendant = null;
            for(ConnectionAttendant att : attendants)
                if(att.obtenerNombreDeLista().equals(attendantString)) {
                    attendant = att;
                    break;
                }
            return attendant;
        }
    }

}
