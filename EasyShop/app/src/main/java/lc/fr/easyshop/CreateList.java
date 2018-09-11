package lc.fr.easyshop;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateList extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextAuthorList;
    private EditText editTextNameList;
    private ListView listViewCatalog;
    private Button buttonSaveList;
    private Button buttonSendList;
    private TextView textViewResult;
    private TextView textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_list);

        editTextAuthorList = findViewById(R.id.editTextAuthorList);
        editTextNameList = findViewById(R.id.editTextNameList);
        buttonSaveList = findViewById(R.id.buttonSaveList);
        buttonSendList = findViewById(R.id.buttonSendList);
        textViewResult = findViewById(R.id.textViewResult);
        listViewCatalog = findViewById(R.id.listViewCatalog);
        textViewTitle = findViewById(R.id.textViewTitle);

        buttonSaveList.setOnClickListener(this);
        buttonSendList.setOnClickListener(this);

        String CATALOG_CSV = "easyshop_catalog.csv";

        List<String> catalogSimple = readCatalogSimple(this, CATALOG_CSV);
        ArrayAdapter<String> List = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, catalogSimple);
        listViewCatalog.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewCatalog.setAdapter(List);

        String fileSelected = this.getIntent().getStringExtra("fileSelected");

        if(fileSelected != null){
            String editList = getString(R.string.edit_list);
            textViewTitle.setText(editList);
            List<String> listSelected = readList(this, fileSelected);

            String lineOne = listSelected.get(0);
            String[] theadValues = lineOne.split(";");
            editTextAuthorList.setText(theadValues[0]);
            editTextAuthorList.setEnabled(false);
            editTextNameList.setText(theadValues[1]);
            editTextNameList.setEnabled(false);

            for(int i = 0; i < listViewCatalog.getAdapter().getCount(); i++){
                String line = listViewCatalog.getItemAtPosition(i).toString();
                for(int k=0; k<listSelected.size(); k++){
                    if(line.equals(listSelected.get(k))){
                        listViewCatalog.setItemChecked(i, true);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == buttonSaveList) {

            String authorList = editTextAuthorList.getText().toString().replaceAll(" ", "");
            String nameList = editTextNameList.getText().toString().replaceAll(" ", "");
            String myList = authorList + "-" + nameList + "_easyshop.csv";

                StringBuilder lsb = new StringBuilder();
                String authorName = editTextAuthorList.getText().toString();
                String listName = editTextNameList.getText().toString();

                lsb.append(authorName);
                lsb.append(";");
                lsb.append(listName);
                lsb.append("\n");
                lsb.append("product_name\n");

                //récupérer les itemSelected
                SparseBooleanArray sba = listViewCatalog.getCheckedItemPositions();

                for (int i = 0; i < sba.size(); i++) {
                    if(sba.get(sba.keyAt(i))){
                        String lsProduct = listViewCatalog.getItemAtPosition(sba.keyAt(i)).toString();
                        lsb.append(lsProduct);
                        lsb.append("\n");
                    }
                }
                String lsContent = lsb.toString();

                createFile(this, myList, lsContent);
                Toast.makeText(this, getString(R.string.list_ok), Toast.LENGTH_LONG).show();
        } // savelist

        if(v == buttonSendList){

            String author = editTextAuthorList.getText().toString();
            String nameList = editTextNameList.getText().toString();

            StringBuilder lsb = new StringBuilder();

            SparseBooleanArray sba = listViewCatalog.getCheckedItemPositions();

            for (int i = 0; i < sba.size(); i++) {
                if(sba.get(sba.keyAt(i))){
                    String lsProduct = listViewCatalog.getItemAtPosition(sba.keyAt(i)).toString();
                    lsb.append(lsProduct);
                    lsb.append("\n");
                }
            }

            String lsContent = lsb.toString();

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject) + nameList);
            sendIntent.putExtra(Intent.EXTRA_TEXT, author + getString(R.string.mail_text)+"\n"+nameList  +": \n"+lsContent);
            sendIntent.setType("text/plain");
            startActivityForResult(Intent.createChooser(sendIntent, getString(R.string.mail_dialog)), 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: // Send list
                switch (resultCode) {
                    case RESULT_OK:
                        // --- Recuperation des donnees recues
                        Toast.makeText(this, getString(R.string.mail_success), Toast.LENGTH_LONG).show();;
                        return;
                    case RESULT_CANCELED:
                        Toast.makeText(this, getString(R.string.cancel), Toast.LENGTH_LONG).show();
                        return;
                } // / switch (resultCode)

        }
    }// / onActivityResult


        /**
         * @param contexte
         * @param psFichier
         * @param psContenu
         */
        private void createFile (Context contexte, String psFichier, String psContenu){

            FileOutputStream fos;
            OutputStreamWriter osw;

            BufferedWriter bw;
            try {

                fos = contexte.openFileOutput(psFichier, Context.MODE_PRIVATE);
                osw = new OutputStreamWriter(fos);
                bw = new BufferedWriter(osw);
                bw.write(psContenu);

                bw.close();
                osw.close();
                fos.close();
            } catch (Exception e) {
                textViewResult.setText(e.getMessage());
            }

        } // / ecrire

    private List<String> readCatalogSimple(Context contexte, String lsFile){

        String lsPath = contexte.getFilesDir().getAbsolutePath();
        String lsRoad = lsPath + "/" + lsFile;

        File f;
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        StringBuilder lsb = new StringBuilder();
        String lsLine;

        List<String> catalogSimple = new ArrayList();

        try {
            f = new File(lsRoad);
            if (f.exists()) {
                // openFileInput : spécifique android
                fis = contexte.openFileInput(lsFile);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                lsLine = br.readLine();
                while ((lsLine = br.readLine()) != null) {
                    catalogSimple.add(lsLine);
                }
                br.close();
                isr.close();
                fis.close();
            } else{
                textViewResult.setText(getString(R.string.no_file));
            }

        } catch(FileNotFoundException e){
            textViewResult.setText(e.getMessage());
        } catch(IOException e){
            textViewResult.setText(e.getMessage());
        }
        return catalogSimple;
    }

    private List<String> readList(Context contexte, String lsFile){

        String lsPath = contexte.getFilesDir().getAbsolutePath();
        String lsRoad = lsPath + "/" + lsFile;

        File f;
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        StringBuilder lsb = new StringBuilder();
        String lsLine;

        List<String> list = new ArrayList();

        try {
            f = new File(lsRoad);
            if (f.exists()) {
                // openFileInput : spécifique android
                fis = contexte.openFileInput(lsFile);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                while ((lsLine = br.readLine()) != null) {
                    list.add(lsLine);
                }
                br.close();
                isr.close();
                fis.close();
            } else{
                textViewResult.setText(getString(R.string.no_file));
            }

        } catch(FileNotFoundException e){
            textViewResult.setText(e.getMessage());
        } catch(IOException e){
            textViewResult.setText(e.getMessage());
        }
        return list;
    }


}


