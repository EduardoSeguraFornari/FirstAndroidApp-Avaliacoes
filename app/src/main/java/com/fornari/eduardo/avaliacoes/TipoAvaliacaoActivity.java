package com.fornari.eduardo.avaliacoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.fornari.eduardo.avaliacoes.dao.TipoAvaliacaoDAO;
import com.fornari.eduardo.avaliacoes.model.TipoAvaliacao;

public class TipoAvaliacaoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText editTextNome;
    private Switch switchNotificar;
    private LinearLayout linearLayoutAntecedenciaNotificacao;
    private Spinner spinnerAntecedenciaNotificacao;
    private ArrayAdapter<Integer> arrayAdapterAntecedenciaNotificacao;
    private EditText editTextDescricao;

    private TipoAvaliacao tipoAvaliacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_avaliacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        editTextNome = (EditText) findViewById(R.id.editTextNome);

        switchNotificar = (Switch) findViewById(R.id.switchNotificar);

        linearLayoutAntecedenciaNotificacao = (LinearLayout) findViewById(R.id.linearLayoutAntecedenciaNotificacao);
        linearLayoutAntecedenciaNotificacao.setVisibility(View.GONE);

        spinnerAntecedenciaNotificacao = (Spinner) findViewById(R.id.spinnerAntecedenciaNotificacao);
        preencheArrayAdapterAntecedenciaNotificacao();
        spinnerAntecedenciaNotificacao.setAdapter(arrayAdapterAntecedenciaNotificacao);

        editTextDescricao = (EditText) findViewById(R.id.editTextDescricao);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("TIPO_AVALIACAO_ID")) {
            int tipo_avaliacao_id = (int) bundle.getSerializable("TIPO_AVALIACAO_ID");

            TipoAvaliacaoDAO tipoAvaliacaoDAO = new TipoAvaliacaoDAO(this);

            tipoAvaliacao = tipoAvaliacaoDAO.buscaTipoAvaliacaoId(tipo_avaliacao_id);

            setTipoAvaliacao();
        }

        switchNotificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchNotificar.isChecked() == true) {
                    linearLayoutAntecedenciaNotificacao.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutAntecedenciaNotificacao.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setTipoAvaliacao() {
        editTextNome.setText(tipoAvaliacao.getNome());
        switchNotificar.setChecked(tipoAvaliacao.isNotificar());
        if (tipoAvaliacao.isNotificar()) {
            linearLayoutAntecedenciaNotificacao.setVisibility(View.VISIBLE);
        } else {
            linearLayoutAntecedenciaNotificacao.setVisibility(View.GONE);
        }
        spinnerAntecedenciaNotificacao.setSelection(tipoAvaliacao.getAntecedenciaNotificacao() - 1);
        editTextDescricao.setText(tipoAvaliacao.getDescricao());editTextNome.setText(tipoAvaliacao.getNome());
        switchNotificar.setChecked(tipoAvaliacao.isNotificar());
        if (tipoAvaliacao.isNotificar()) {
            linearLayoutAntecedenciaNotificacao.setVisibility(View.VISIBLE);
        } else {
            linearLayoutAntecedenciaNotificacao.setVisibility(View.GONE);
        }
        spinnerAntecedenciaNotificacao.setSelection(tipoAvaliacao.getAntecedenciaNotificacao() - 1);
        editTextDescricao.setText(tipoAvaliacao.getDescricao());
    }

    public void preencheArrayAdapterAntecedenciaNotificacao() {
        int layoutAdapter = android.R.layout.simple_list_item_1;
        arrayAdapterAntecedenciaNotificacao = new ArrayAdapter<Integer>(TipoAvaliacaoActivity.this, layoutAdapter);
        for (int i = 1; i <= 50; i++) {
            arrayAdapterAntecedenciaNotificacao.add(i);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tipo_avaliacao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_salvar_tipo_avaliacao) {
            String nome = editTextNome.getText().toString();
            if (nome.isEmpty()) {
                Toast.makeText(TipoAvaliacaoActivity.this, "O tipo de avaliação não pode ficar em branco!", Toast.LENGTH_LONG).show();
            } else {
                TipoAvaliacaoDAO tipoAvaliacaoDAO = new TipoAvaliacaoDAO(this);
                if ((tipoAvaliacao == null || !nome.equals(tipoAvaliacao.getNome())) && tipoAvaliacaoDAO.buscaTipoAvaliacaoPorNome(nome) != null) {
                    Toast.makeText(TipoAvaliacaoActivity.this, "Já existe um tipo de avaliação com este nome!", Toast.LENGTH_LONG).show();
                } else {
                    boolean notificarTipoAvaliacao = switchNotificar.isChecked();
                    int antecedenciaNotificacaoTipoAvaliacao = (int) spinnerAntecedenciaNotificacao.getSelectedItem();
                    String descricaoTipoAvaliacao = editTextDescricao.getText().toString();
                    if (tipoAvaliacao == null) {
                        TipoAvaliacao tipoAvaliacaoAUX = new TipoAvaliacao(nome, notificarTipoAvaliacao, antecedenciaNotificacaoTipoAvaliacao, descricaoTipoAvaliacao);
                        tipoAvaliacaoDAO.inserir(tipoAvaliacaoAUX);
                    } else {
                        TipoAvaliacao tipoAvaliacaoAUX = new TipoAvaliacao(tipoAvaliacao.getId(), nome, notificarTipoAvaliacao, antecedenciaNotificacaoTipoAvaliacao, descricaoTipoAvaliacao);
                        if (!tipoAvaliacaoAUX.equals(tipoAvaliacao)) {
                            tipoAvaliacaoDAO.atualizaTipoAvaliacao(tipoAvaliacaoAUX);
                            Toast.makeText(TipoAvaliacaoActivity.this, "São diferentes", Toast.LENGTH_LONG).show();
                        }
                    }
                    Intent intent = new Intent(TipoAvaliacaoActivity.this, TiposDeAvaliacaoActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}