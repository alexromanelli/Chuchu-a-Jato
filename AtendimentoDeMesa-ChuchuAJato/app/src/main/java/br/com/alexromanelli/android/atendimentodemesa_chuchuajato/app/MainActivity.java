package br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_atendimento_mesa:
                iniciarAtividade_AtenderMesa();
                return true;
            case R.id.action_sair:
                finish();
                return true;
        }
        return false;
    }

    private void iniciarAtividade_AtenderMesa() {
        Intent i = new Intent(MainActivity.this,
                AtividadeAtendimentoMesa.class);
        startActivity(i);
    }

}
