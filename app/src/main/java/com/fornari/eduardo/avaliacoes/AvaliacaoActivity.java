package com.fornari.eduardo.avaliacoes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.fornari.eduardo.avaliacoes.bo.AvaliacaoBO;
import com.fornari.eduardo.avaliacoes.bo.TipoAvaliacaoBO;
import com.fornari.eduardo.avaliacoes.model.Avaliacao;
import com.fornari.eduardo.avaliacoes.model.TipoAvaliacao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AvaliacaoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText editTextObservacao;
    private TextView textViewDataAvaliacao;
    private Spinner spinnerTiposAvaliacao;
    private ArrayAdapter<TipoAvaliacao> arrayAdapterTiposAvaliacao;

    private Integer disciplinaId = null;
    private Avaliacao avaliacao;

    private Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerTiposAvaliacao = (Spinner) findViewById(R.id.spinnerTiposAvaliacao);
        editTextObservacao = (EditText) findViewById(R.id.editTextObservacao);
        textViewDataAvaliacao = (TextView) findViewById(R.id.textViewDataAvaliacao);

        //Busca e mostra na tela os tipos de avaliações cadastrados no banco de dados em ordem alfabetica
        carregaTiposAvaliacao();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("DISCIPLINA_ID")) {
                disciplinaId = (int) bundle.getSerializable("DISCIPLINA_ID");
            }

            if (bundle.containsKey("AVALIACAO_ID")) {
                int avaliacaoId = (int) bundle.getSerializable("AVALIACAO_ID");
                AvaliacaoBO avaliacaoBO = new AvaliacaoBO(this);
                avaliacao = avaliacaoBO.buscaAvaliacaoID(avaliacaoId);
                setAvaliacao(avaliacao);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ExibeDataListener exibeDataListener = new ExibeDataListener();
        textViewDataAvaliacao.setOnClickListener(exibeDataListener);
        textViewDataAvaliacao.setOnFocusChangeListener(exibeDataListener);

        spinnerTiposAvaliacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                validaOpcaoSalvar();
                                                            }

                                                            @Override
                                                            public void onNothingSelected(AdapterView<?> parent) {
                                                            }
                                                        }
        );

        editTextObservacao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validaOpcaoSalvar();
            }
        });
    }

    private void carregaTiposAvaliacao() {
        preencheAdapterTiposAvaliacao(buscaTiposAvaliacao());
        spinnerTiposAvaliacao.setAdapter(arrayAdapterTiposAvaliacao);
    }

    private void validaOpcaoSalvar() {
        TipoAvaliacao tipoAvaliacao = arrayAdapterTiposAvaliacao.getItem(spinnerTiposAvaliacao.getSelectedItemPosition());

        String data = textViewDataAvaliacao.getText().toString();

        String observacao = editTextObservacao.getText().toString();

        if (tipoAvaliacao.getNome().equals("Selecionar") || data.equals("__ /__ /__")) {
            myMenu.findItem(R.id.action_salvar_avaliacao).setVisible(false);
        } else if (avaliacao != null) {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
            String dataAvaliacao = dateFormat.format(avaliacao.getData().getTime());

            if (tipoAvaliacao.getId() != avaliacao.getTipoAvaliacao().getId() || !data.equals(dataAvaliacao) || !observacao.equals(avaliacao.getObservacao())) {
                myMenu.findItem(R.id.action_salvar_avaliacao).setVisible(true);
            } else {
                myMenu.findItem(R.id.action_salvar_avaliacao).setVisible(false);
            }
        } else {
            myMenu.findItem(R.id.action_salvar_avaliacao).setVisible(true);
        }
    }

    private void setAvaliacao(Avaliacao avaliacao) {
        for (int i = 0; i < arrayAdapterTiposAvaliacao.getCount(); i++) {
            TipoAvaliacao tipoAvaliacao = arrayAdapterTiposAvaliacao.getItem(i);
            if (tipoAvaliacao.getId() == avaliacao.getTipoAvaliacao().getId()) {
                spinnerTiposAvaliacao.setSelection(i);
                break;
            }
        }

        Date dataAvaliacao = avaliacao.getData();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        String format = dateFormat.format(dataAvaliacao);
        textViewDataAvaliacao.setText(format);

        editTextObservacao.setText(avaliacao.getObservacao());
    }

    private void salvar() {
        int tipoAvaliacaoId = arrayAdapterTiposAvaliacao.getItem(spinnerTiposAvaliacao.getSelectedItemPosition()).getId();

        Date data = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        try {
            data = df.parse(textViewDataAvaliacao.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String observacao = editTextObservacao.getText().toString();

        Avaliacao avaliacaoAUX = new Avaliacao(tipoAvaliacaoId, data, observacao, disciplinaId);
        AvaliacaoBO avaliacaoBO = new AvaliacaoBO(this);

        if (avaliacao != null) {
            avaliacaoBO.atualizaAvaliacao(avaliacao.getId(), avaliacaoAUX);
        } else {
            avaliacaoBO.inserir(avaliacaoAUX);
        }

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void mostraDialogDeletarAvaliacao() {
        final Dialog dialog = new Dialog(AvaliacaoActivity.this);
        dialog.setContentView(R.layout.deletar);

        dialog.setTitle("DELETAR AVALIAÇÃO");

        TextView textViewDeletar = (TextView) dialog.findViewById(R.id.textViewDeletarDialog);
        textViewDeletar.setText("Deletar esta avaliação?");

        ImageButton imageButtonCancelDeletarDialog = (ImageButton) dialog.findViewById(R.id.imageButtonCancelDeletarDialog);
        imageButtonCancelDeletarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        ImageButton imageButtonDoneDeletarDialog = (ImageButton) dialog.findViewById(R.id.imageButtonDoneDeletarDialog);
        imageButtonDoneDeletarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AvaliacaoBO avaliacaoBO = new AvaliacaoBO(AvaliacaoActivity.this);
                avaliacaoBO.deletarAvaliacaoId(avaliacao.getId());

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        dialog.show();
    }

    private class SelecionaDataListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            Date date = calendar.getTime();
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
            String format = dateFormat.format(date);
            if (date.compareTo(new Date()) >= 0) {
                textViewDataAvaliacao.setText(format);
            } else textViewDataAvaliacao.setText("__ /__ /__");
            validaOpcaoSalvar();
        }
    }

    public class ExibeDataListener implements View.OnClickListener, View.OnFocusChangeListener {
        @Override
        public void onClick(View v) {
            exibeData();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) exibeData();
        }
    }

    private void exibeData() {
        int dia, mes, ano;
        Calendar calendar = Calendar.getInstance();
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        ano = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new SelecionaDataListener(), ano, mes, dia);
        datePickerDialog.show();
    }

    private void sortArrayAdapterTiposAvaliacao(ArrayAdapter<TipoAvaliacao> arrayAdapterTiposAvaliacao) {
        Comparator<TipoAvaliacao> porNome = new Comparator<TipoAvaliacao>() {
            @Override
            public int compare(TipoAvaliacao avaliacao1, TipoAvaliacao avaliacao2) {
                if (avaliacao1.getNome().equalsIgnoreCase("Selecionar")) return -1;
                if (avaliacao2.getNome().equalsIgnoreCase("Selecionar")) return 1;
                return avaliacao1.getNome().compareTo(avaliacao2.getNome());
            }
        };
        arrayAdapterTiposAvaliacao.sort(porNome);
    }

    public void preencheAdapterTiposAvaliacao(List<TipoAvaliacao> tiposAvaliacao) {
        int layoutAdapter = android.R.layout.simple_list_item_1;
        arrayAdapterTiposAvaliacao = new ArrayAdapter<TipoAvaliacao>(AvaliacaoActivity.this, layoutAdapter);
        for (TipoAvaliacao tipoAvaliacao : tiposAvaliacao) {
            arrayAdapterTiposAvaliacao.add(tipoAvaliacao);
        }
        TipoAvaliacao tipoAvaliacaoAUX = new TipoAvaliacao("Selecionar");
        tipoAvaliacaoAUX.setId(-1);
        arrayAdapterTiposAvaliacao.add(tipoAvaliacaoAUX);
        sortArrayAdapterTiposAvaliacao(arrayAdapterTiposAvaliacao);
    }

    public List<TipoAvaliacao> buscaTiposAvaliacao() {
        TipoAvaliacaoBO tipoAvaliacaoBO = new TipoAvaliacaoBO(this);
        List<TipoAvaliacao> tiposAvaliacao = tipoAvaliacaoBO.buscaTiposAvaliacao();
        return tiposAvaliacao;
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
        getMenuInflater().inflate(R.menu.avaliacao, menu);
        if (avaliacao != null) {
            menu.findItem(R.id.action_deletar_avaliacao).setVisible(true);
        }
        myMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_salvar_avaliacao) {
            salvar();
            return true;
        } else if (id == R.id.action_deletar_avaliacao) {
            mostraDialogDeletarAvaliacao();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent;

        if (id == R.id.nav_avaliacoes) {
            intent = new Intent(AvaliacaoActivity.this, AvaliacoesActivity.class);
            finish();
            startActivityForResult(intent, 0);
        } else if (id == R.id.nav_disciplinas) {
            intent = new Intent(AvaliacaoActivity.this, DisciplinasActivity.class);
            finish();
            startActivityForResult(intent, 0);
        } else if (id == R.id.nav_tipos_avaliacao) {
            intent = new Intent(AvaliacaoActivity.this, TiposAvaliacaoActivity.class);
            finish();
            startActivityForResult(intent, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}