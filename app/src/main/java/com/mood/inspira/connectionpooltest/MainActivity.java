package com.mood.inspira.connectionpooltest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by labmovil-10 on 3/06/16.
 */
public class MainActivity extends AppCompatActivity {

    private ConnectionPoolService mService;
    private boolean mBound = false;
    private ArrayAdapter<String> adapter;
    private ActivityActions acciones;

    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.hosts_panel);
        ListView listaHostsActivos = (ListView) findViewById(R.id.hosts_panel_lista_de_hosts);
        List<String> hostsActivos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hostsActivos);
        listaHostsActivos.setAdapter(adapter);
        listaHostsActivos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String texto = ((TextView)view).getText().toString();
                mService.removerAttendant(mService.obtenerAsistente(texto));
                return true;
            }
        });
        acciones = new ActivityActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean consumed = false;
        if(item.getItemId() == R.id.main_menu_clear){
            
            consumed = true;
        }
        return consumed;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        String[] items = new String[adapter.getCount()];
        for(int i=0; i<items.length; i++)
            items[i] = adapter.getItem(i);
        outState.putStringArray("items", items);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstnaceState){
        String[] items = savedInstnaceState.getStringArray("items");
        assert items != null;
        for(String item : items)
            adapter.add(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, ConnectionPoolService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ConnectionPoolService.LocalBinder binder = (ConnectionPoolService.LocalBinder) service;
            mService = binder.getService();
            mService.setActivityActions(acciones);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public class ActivityActions{

        public synchronized void agregarHost(final String host){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.add(host);
                }
            });
        }

        public synchronized void removerHost(final String host){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.remove(host);
                }
            });
        }
    }
}
