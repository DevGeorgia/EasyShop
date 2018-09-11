package lc.fr.easyshop;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

public class OpenList extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinnerLists;
    private Button buttonOpenList;
    private Button buttonDelete;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_list);

        spinnerLists = findViewById(R.id.spinnerLists);
        buttonOpenList = findViewById(R.id.buttonOpenList);
        buttonDelete = findViewById(R.id.buttonDelete);

        textViewResult = findViewById(R.id.textViewResult);

        buttonOpenList.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        setSpinner();

    } // onCreate

    @Override
    public void onClick(View v) {

        if (v == buttonOpenList) {
            String itemSelect = spinnerLists.getSelectedItem().toString();
            String fileSelected = itemSelect+"_easyshop.csv";
            Intent intentionOpenList = new Intent(this, CreateList.class);
            // --- L'ajout des valeurs a renvoyer
            intentionOpenList.putExtra("fileSelected", fileSelected);
            startActivityForResult(intentionOpenList, 1);
            finish();
        } // buttonOpenList

        if(v == buttonDelete){

            String itemSelect = spinnerLists.getSelectedItem().toString();
            String fileSelected = itemSelect+"_easyshop.csv";

            String lsPath = this.getFilesDir().getAbsolutePath();
            String lsRoad = lsPath + "/" +fileSelected;
            File lsFile  = new File(lsRoad);

            try {
                lsFile.delete();
                setSpinner();
                Toast.makeText(this, getString(R.string.list_deleted), Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                textViewResult.setText(e.getMessage());
            }
        } // buttonDelete
    } // onClick(View v)

    private void setSpinner(){

        List<String> globalLists = new ArrayList();

        String[] lsDirectory = this.getFilesDir().list();

        if(lsDirectory.length > 0) {
            for (int i = 0; i < lsDirectory.length; i++) {
                if (lsDirectory[i].endsWith("_easyshop.csv")) {
                    String[] tDirValues = lsDirectory[i].split("_");
                    String lsList = tDirValues[0];
                    globalLists.add(lsList);
                }
            }
        }

        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, globalLists);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLists.setAdapter(aa);
    }

}
