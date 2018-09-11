package lc.fr.easyshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends AppCompatActivity implements View.OnClickListener {

    private Button buttonCreateList;
    private Button buttonViewList;
    private Button buttonOpenCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        buttonCreateList = findViewById(R.id.buttonCreateList);
        buttonViewList = findViewById(R.id.buttonViewList);
        buttonOpenCatalog = findViewById(R.id.buttonOpenCatalog);

        buttonCreateList.setOnClickListener(this);
        buttonViewList.setOnClickListener(this);
        buttonOpenCatalog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == buttonCreateList){
            Intent intentionCreateList = new Intent();
            intentionCreateList.setClass(this, CreateList.class);
            startActivity(intentionCreateList);
        }

        if (v == buttonViewList){
            Intent intentionViewList = new Intent();
            intentionViewList.setClass(this, OpenList.class);
            startActivity(intentionViewList);
        }

        if (v == buttonOpenCatalog){
            Intent intentionOpenCatalog = new Intent();
            intentionOpenCatalog.setClass(this, Catalog.class);
            startActivity(intentionOpenCatalog);
        }

    }
}
