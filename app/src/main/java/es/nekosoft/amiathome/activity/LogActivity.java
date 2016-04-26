package es.nekosoft.amiathome.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import es.nekosoft.amiathome.R;
import es.nekosoft.amiathome.dao.LogMHDAO;
import es.nekosoft.amiathome.model.LogMH;


public class LogActivity extends AppCompatActivity {


    List<LogMH> logList;
    RecyclerView list;
    RecyclerView.LayoutManager layoutManager;
    LogMHAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        setupToolbar();
        setupList();

    }

    private void setupToolbar(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    private void setupList() {

        list = (RecyclerView) findViewById(R.id.lista_log);

        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        getData();
        adapter = new LogMHAdapter(logList);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        else if(id == R.id.action_delete){
            showDialogDelete();
        }
        else if(id == R.id.action_reload){
            getData();
            reloadList();
            messageReload();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getData(){

        LogMHDAO dao = new LogMHDAO(this);
        logList = dao.readAll();
    }

    private void reloadList() {

        //Set info to list
        adapter.setAdapter(logList);
        adapter.notifyDataSetChanged();
    }

    private void messageReload(){

        //Show mesage if there is no data
        if(logList == null)
            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.toast_reload_empty), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.toast_reload_data), Toast.LENGTH_SHORT).show();
    }

    private void showDialogDelete() {

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_delete_title))
                .setMessage(getString(R.string.dialog_delete_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        LogMHDAO dao = new LogMHDAO(getBaseContext());
                        dao.deleteAll();
                        logList = null;
                        reloadList();
                        Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.toast_delete), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

}
