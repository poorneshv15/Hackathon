package rvce.hackathon.hackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class CitySet extends AppCompatActivity {
    private AutoCompleteTextView view;
    String[] city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_set);
        view = (AutoCompleteTextView) findViewById(R.id.CitySearch);
        city=getResources().getStringArray(R.array.city);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,city);
        view.setAdapter(adapter);

    }
}
