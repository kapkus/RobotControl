package com.pl.edu.prz.robotui.control.moves;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.edu.prz.robotui.R;

import java.util.ArrayList;
import java.util.List;

public class ShowMovesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyDatabaseHelper myDB;
    ArrayList<String> moves_id, moves_name, moves_pos, moves_count;
    com.pl.edu.prz.robotui.control.moves.DBRecyclerViewAdapter DBRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_moves);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);

        myDB = new MyDatabaseHelper(ShowMovesActivity.this);
        moves_id = new ArrayList<>();
        moves_name = new ArrayList<>();
        moves_pos = new ArrayList<>();
        moves_count = new ArrayList<>();

        storeDataInArrays();
        DBRecyclerViewAdapter = new DBRecyclerViewAdapter(ShowMovesActivity.this, moves_id, moves_name, moves_pos, moves_count, new DBRecyclerViewAdapter.getSequenceListener() {
            @Override
            public void onLoadButtonClick(int id) {
                String queryMoves = myDB.readMoves(id);
                Intent intent = new Intent();
                intent.putExtra("moves", queryMoves);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        recyclerView.setAdapter(DBRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowMovesActivity.this));

    }

    @Override
    protected void onDestroy() {
        myDB.close();
        super.onDestroy();
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String moves = "";
                moves = parseMoves(cursor.getString(3));
                moves_id.add(cursor.getString(0));
                moves_name.add(cursor.getString(1));
                moves_count.add(cursor.getString(2));
                moves_pos.add(moves);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String parseMoves(String moves) {
        String parsedSteps = "";
        List<String> step = new ArrayList<String>();
        String number = "";
        for (int i = 0; i < moves.length(); i++) {
            number = number + moves.charAt(i);
            if (moves.charAt(i + 1) == ',') {
                step.add(number);
                number = "";
                i++;
            } else if (moves.charAt(i + 1) == ';') {
                step.add(number);
                number = "";
                i++;
            }
        }

        for (int i = 0; i < step.size(); i = i + 3) {
            parsedSteps = parsedSteps + "x: " + step.get(i) + " y: " + step.get(i + 1) + " z: " + step.get(i + 2) + '\n';
        }
        return parsedSteps;
    }

}