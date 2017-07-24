package br.com.alura.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Browser;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.alura.agenda.adapter.AlunosAdapter;
import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.dao.AlunoDao;
import br.com.alura.agenda.modelo.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);
        listaAlunos = (ListView) findViewById(R.id.alunos);

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                Intent intentFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intentFormulario.putExtra("Aluno", aluno);
                startActivity(intentFormulario);
            }
        });

        Button novoAluno = (Button) findViewById(R.id.novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentFormulario);
            }
        });

        if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, 124);
        }
        registerForContextMenu(listaAlunos);
    }

    private void carregarListaAlunos() {
        AlunoDao alunoDao = new AlunoDao(this);
        List<Aluno> alunos = alunoDao.listarAlunos();
        alunoDao.close();

        AlunosAdapter alunosAdapter = new AlunosAdapter(alunos, this);
        listaAlunos.setAdapter(alunosAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        carregarListaAlunos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_enviar_notas:
                new EnviaAlunosTask(this).execute();
                break;
            case R.id.baixar_provas:
                Intent irParaProvas = new Intent(this, ProvasActivity.class);
                startActivity(irParaProvas);
                break;
            case R.id.menu_mapa:
                Intent irParaMapa = new Intent(this, MapsActivity.class);
                startActivity(irParaMapa);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        MenuItem fazerLigacao = menu.add("Ligar");
        fazerLigacao.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });

        MenuItem enviarSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone()));
        enviarSMS.setIntent(intentSMS);

        MenuItem localizarAluno = menu.add("Vizualizar Mapa");
        Intent intentLocalizarMapa = new Intent(Intent.ACTION_VIEW);
        intentLocalizarMapa.setData(Uri.parse("geo:0.0?q=" + aluno.getEndereco()));
        localizarAluno.setIntent(intentLocalizarMapa);

        MenuItem visitarSite = menu.add("Visitar Site");
        Intent intentVisitarSite = new Intent(Intent.ACTION_VIEW);
        String siteAluno = aluno.getSite();
        if(!siteAluno.startsWith("http://")){
            siteAluno = "http://" + siteAluno;
        }
        intentVisitarSite.setData(Uri.parse(siteAluno));
        visitarSite.setIntent(intentVisitarSite);

        MenuItem excluir = menu.add("Excluir Aluno");
        excluir.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {


                AlunoDao alunoDao= new AlunoDao(ListaAlunosActivity.this);
                alunoDao.excluirAluno(aluno);
                alunoDao.close();
                carregarListaAlunos();
                Toast.makeText(ListaAlunosActivity.this, "O aluno " + aluno.getNome() + " foi excluído!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

}
