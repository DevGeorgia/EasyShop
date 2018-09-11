package lc.fr.easyshop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;

public class Catalog extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private EditText editTextProductName;
    private Button buttonAddProduct;
    private ListView listViewCatalog;
    private TextView textViewResult;
    private List<String> catalogSimple;
    private ArrayAdapter<String> List;

    private final String CATALOG_CSV = "easyshop_catalog.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog);

        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        editTextProductName = findViewById(R.id.editTextProductName);
        listViewCatalog = findViewById(R.id.listViewCatalog);
        textViewResult = findViewById(R.id.textViewResult);

        buttonAddProduct.setOnClickListener(this);

        catalogSimple = readCatalogSimple(this, CATALOG_CSV);
        List = new ArrayAdapter<String>(this, R.layout.line_catalog, R.id.productName, catalogSimple);
        listViewCatalog.setAdapter(List);
        listViewCatalog.setOnItemLongClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == buttonAddProduct) {

            String lsPath = this.getFilesDir().getAbsolutePath();
            String lsRoad = lsPath + "/" + CATALOG_CSV;
            File lsFileExist  = new File(lsRoad); // OK
            int lsFind = 0;

            // --- LECTURE
            if (lsFileExist.exists()) {
                //Si catalog existe déjà alors add product à la fin du fichier
                StringBuilder lsb = new StringBuilder();
                lsb.append(editTextProductName.getText());
                lsb.append("\n");
                String lsContent = lsb.toString();

                catalogSimple = readCatalogSimple(this, CATALOG_CSV);
                for(int i=0; i<catalogSimple.size(); i++){
                      if(catalogSimple.get(i).equals(editTextProductName.getText().toString())){
                        lsFind = 1;
                        Toast.makeText(this, getString(R.string.error_add), Toast.LENGTH_LONG).show();
                    }
                }

                if(lsFind == 0){
                    editFile(this, CATALOG_CSV, lsContent);
                    Toast.makeText(this, getString(R.string.success_add), Toast.LENGTH_LONG).show();
                }

                catalogSimple = readCatalogSimple(this, CATALOG_CSV);
                List = new ArrayAdapter<String>(this, R.layout.line_catalog, R.id.productName, catalogSimple);
                listViewCatalog.setAdapter(List);


            } else {
                //Si catalog n'existe pas alors le créer
                StringBuilder lsb = new StringBuilder();
                lsb.append("product_name\n");
                lsb.append(editTextProductName.getText());
                lsb.append("\n");
                String lsContent = lsb.toString();
                createFile(this, CATALOG_CSV, lsContent);

                catalogSimple = readCatalogSimple(this, CATALOG_CSV);
                List = new ArrayAdapter<String>(this, R.layout.line_catalog, R.id.productName, catalogSimple);
                listViewCatalog.setAdapter(List);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {

        String lsTitle = getString(R.string.warning);
        String lsMessage = getString(R.string.warning_msg);

        DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int response) {

                if (response == Dialog.BUTTON_POSITIVE) {
                    catalogSimple.remove(position);
                    List = new ArrayAdapter<String>(getBaseContext(), R.layout.line_catalog, R.id.productName, catalogSimple);
                    List.notifyDataSetChanged();

                    StringBuilder lsb = new StringBuilder();
                    lsb.append("product_name\n");
                    for(int i = 0; i<catalogSimple.size(); i++){
                        lsb.append(catalogSimple.get(i));
                        lsb.append("\n");
                    }
                    String lsContent = lsb.toString();
                    createFile(getBaseContext(), CATALOG_CSV, lsContent);
                    Toast.makeText(getBaseContext(), getString(R.string.product_delete), Toast.LENGTH_LONG).show();

                    catalogSimple = readCatalogSimple(getBaseContext(), CATALOG_CSV);
                    List = new ArrayAdapter<String>(getBaseContext(), R.layout.line_catalog, R.id.productName, catalogSimple);
                    listViewCatalog.setAdapter(List);

                }

                if (response == Dialog.BUTTON_NEGATIVE) {
                    Toast.makeText(getBaseContext(), getString(R.string.cancel), Toast.LENGTH_LONG).show();
                }
            }
        };

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(lsTitle);
        ad.setMessage(lsMessage);
        ad.setNegativeButton(getString(R.string.no), click);
        ad.setPositiveButton(getString(R.string.yes), click);
        ad.show();
        return true;
    }

    /**
     * @param contexte
     * @param psFichier
     * @param psContenu
     */
    private void createFile(Context contexte, String psFichier, String psContenu) {

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

    /**
     * @param contexte
     * @param psFichier
     * @param psContenu
     */
    private void editFile(Context contexte, String psFichier, String psContenu) {

        FileOutputStream fos;
        OutputStreamWriter osw;
        BufferedWriter bw;

        try {

            fos = contexte.openFileOutput(psFichier, Context.MODE_APPEND);
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


}

