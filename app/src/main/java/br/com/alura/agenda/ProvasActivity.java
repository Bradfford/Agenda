package br.com.alura.agenda;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.alura.agenda.modelo.Prova;

public class ProvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provas);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frame_principal, new ListaProvasFragment());
        if(isModoPaisagem()){
            transaction.replace(R.id.frame_secundario, new DetalhesProvaFragment());
        }

        transaction.commit();
    }

    private boolean isModoPaisagem() {
        //foram criados dois arquivos "bools" de mesmo nome na pasta values, um com parametro true e outro false.
        // o device usará o arquivo conforme a orientação do mesmo.
        return getResources().getBoolean(R.bool.modoPaisagem);
    }

    public void selecionaProva(Prova prova) {
        FragmentManager manager = getSupportFragmentManager();
        if(!isModoPaisagem()){
            FragmentTransaction transaction = manager.beginTransaction();

            DetalhesProvaFragment detalhesFragment = new DetalhesProvaFragment();
            Bundle parametros = new Bundle();
            parametros.putSerializable("prova", prova);

            detalhesFragment.setArguments(parametros);

            transaction.replace(R.id.frame_principal, detalhesFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            DetalhesProvaFragment detalhesProvaFragment =
                    (DetalhesProvaFragment) manager.findFragmentById(R.id.frame_secundario);
            detalhesProvaFragment.popularCamposCom(prova);
        }
    }
}
