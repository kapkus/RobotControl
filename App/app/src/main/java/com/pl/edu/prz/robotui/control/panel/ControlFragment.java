package com.pl.edu.prz.robotui.control.panel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.edu.prz.robotui.MainActivity;
import com.pl.edu.prz.robotui.R;
import com.pl.edu.prz.robotui.control.moves.ListRecyclerViewAdapter;
import com.pl.edu.prz.robotui.control.moves.MyDatabaseHelper;
import com.pl.edu.prz.robotui.control.moves.ShowMovesActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

public class ControlFragment extends Fragment implements ThumbstickView.ThumbstickListener, VerticalThumbstickView.VerticalThumbstickListener {

    public boolean doneSendingXY = true;
    public boolean doneSendingZ = true;
    public boolean doneSendingSeq = true;
    Coords coords = new Coords();
    Button addButton, manageButton, saveButton, homeButton, playStepsButton, exitButton;
    RecyclerView recyclerView;
    ArrayList<String> step_id;
    ArrayList<ArrayList<String>> coords_str;
    ListRecyclerViewAdapter listRecyclerViewAdapter;
    BtProtocol btProtocol;
    OutputStream outputStream;
    InputStream inputStream;
    SharedPreferences sp;
    int sensXY, sensZ;
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                String id = data.getStringExtra("moves");
                recyclerView.setAdapter(listRecyclerViewAdapter);
                listRecyclerViewAdapter.overwriteMoves(id);

                System.out.println(listRecyclerViewAdapter.getItemCount());
                listRecyclerViewAdapter.notifyDataSetChanged();

            }
        }
    });
    private TextView coordsX, coordsY, coordsZ;
    private TextView emptyView;
    private String alertDialogText = "";
    private int selectedPos;

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.control_panel, container, false);
        coordsX = v.findViewById(R.id.coordsX);
        coordsY = v.findViewById(R.id.coordsY);
        coordsZ = v.findViewById(R.id.coordsZ);
        coordsX.setText((round(coords.getPosX(), 2)).toString());
        coordsY.setText((round(coords.getPosY(), 2)).toString());
        coordsZ.setText((round(coords.getPosZ(), 2)).toString());

        addButton = v.findViewById(R.id.addButton);
        manageButton = v.findViewById(R.id.manageSequences);
        saveButton = v.findViewById(R.id.saveSequence);
        homeButton = v.findViewById(R.id.homeButton);
        playStepsButton = v.findViewById(R.id.playSteps);
        emptyView = v.findViewById(R.id.empty_view);
        exitButton = v.findViewById(R.id.exitButton);

        ThumbstickView thumbstickView = v.findViewById(R.id.mainThumbstick);
        VerticalThumbstickView verticalThumbstickView = v.findViewById(R.id.vertThumbstick);

        thumbstickView.setListener(this);
        verticalThumbstickView.setListener(this);

        recyclerView = v.findViewById(R.id.coordsList);

        if (step_id == null) {
            step_id = new ArrayList<String>();
            coords_str = new ArrayList<ArrayList<String>>();
            emptyView.setVisibility(View.VISIBLE);
        }

        listRecyclerViewAdapter = new ListRecyclerViewAdapter(getContext(), step_id, coords_str);
        recyclerView.setAdapter(listRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        outputStream = ((MainActivity) getActivity()).getOutputStream();
        inputStream = ((MainActivity) getActivity()).getInputStream();
        if (outputStream != null) {
            btProtocol = new BtProtocol(outputStream, inputStream);
        }

        sp = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        sensXY = sp.getInt("sensXY", 0) + 1;
        sensZ = sp.getInt("sensZ", 0) + 1;
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (listRecyclerViewAdapter.getItemCount() > 0) {
                    emptyView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
                super.onChanged();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> coordsStr = new ArrayList<String>();
                coordsStr.add((round(coords.getPosX(), 2)).toString());
                coordsStr.add((round(coords.getPosY(), 2)).toString());
                coordsStr.add((round(coords.getPosZ(), 2)).toString());
                listRecyclerViewAdapter.addItem(coordsStr);

                recyclerView.scrollToPosition(listRecyclerViewAdapter.getItemCount() - 1);
                System.out.println(coords_str);
            }
        });


        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowMovesActivity.class);
                launchSomeActivity.launch(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Zapisz sekwencję jako:");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String coordsParsed = "";
                        alertDialogText = input.getText().toString();
                        MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());
                        coordsParsed = parseMoves(coords_str);
                        myDB.addMoves(alertDialogText, coords_str.size() - 1, coordsParsed);
                    }
                });
                builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coords.setPosX(0);
                coords.setPosY(0);
                coords.setPosZ(0);
                coordsX.setText((round(coords.getPosX(), 2)).toString());
                coordsY.setText((round(coords.getPosY(), 2)).toString());
                coordsZ.setText((round(coords.getPosZ(), 2)).toString());
            }
        });

        playStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btProtocol == null) {
                    Toast.makeText(getContext(), "Najpierw połącz się z bluetooth", Toast.LENGTH_SHORT).show();
                    return;
                }
                btProtocol.setSequence(coords_str);
                CommunicationThread thread = new CommunicationThread();
                thread.start();
                playStepsButton.setText("Stop");
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                System.exit(0);
            }
        });
    }

    @Override
    public void onThumbstickMoved(float xPos, float yPos, int id) {
        if (outputStream == null) {
            return;
        }
        if (!doneSendingXY) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BigDecimal xRound, yRound;
                coords.increaseX(xPos * sensXY);
                coords.increaseY(yPos * sensXY);

                xRound = round(coords.getPosX(), 2);
                yRound = round(coords.getPosY(), 2);

                coordsX.setText((round(coords.getPosX(), 2)).toString());
                coordsY.setText((round(coords.getPosY(), 2)).toString());

                CommunicationThread thread = new CommunicationThread(xRound.toString(), yRound.toString());
                thread.start();

            }
        });
    }

    @Override
    public void onVerticalThumbstickMoved(float zPos, int id) {
        if (outputStream == null) {
            return;
        }
        if (!doneSendingZ) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                BigDecimal zRound;

                coords.increaseZ(zPos * sensZ);

                zRound = round(coords.getPosZ(), 2);
                coordsZ.setText((round(coords.getPosZ(), 2)).toString());

                CommunicationThread thread = new CommunicationThread(zRound.toString());
                thread.start();

            }
        });
    }

    private String parseMoves(ArrayList<ArrayList<String>> moves) {
        String newMoves = "";
        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).size() == 1) {
                newMoves = newMoves + moves.get(i).get(0) + ";";
            } else {
                newMoves = newMoves + moves.get(i).get(0) + "," + moves.get(i).get(1) + "," + moves.get(i).get(2) + ";";
            }
        }
        return newMoves;
    }

    class CommunicationThread extends Thread {
        public boolean runXY, runZ, runSeq;
        String x, y, z;

        CommunicationThread(String x, String y) {
            this.x = x;
            this.y = y;
            this.runXY = true;
        }

        CommunicationThread(String z) {
            this.z = z;
            this.runZ = true;
        }

        CommunicationThread() {
            this.runSeq = true;
        }

        @Override
        public void run() {
            String confirmation = "";
            if (this.runXY) {
                doneSendingXY = false;
                btProtocol.passXY(this.x, this.y);
                while (!Objects.equals(confirmation, "h")) {
                    confirmation = btProtocol.receiveData();
                    System.out.println("confirmation:   " + confirmation);
                }
                doneSendingXY = true;
                return;
            }

            if (this.runZ) {
                doneSendingZ = false;
                btProtocol.passZ(this.z);
                while (!Objects.equals(confirmation, "p")) {
                    confirmation = btProtocol.receiveData();
                    System.out.println(confirmation);
                }
                doneSendingZ = true;
                return;
            }

            if (this.runSeq) {
                doneSendingSeq = false;
                for (selectedPos = 0; selectedPos < btProtocol.getSequenceSize(); selectedPos++) {
                    btProtocol.passSequence(selectedPos);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listRecyclerViewAdapter.selectedPos = selectedPos;
                            listRecyclerViewAdapter.notifyDataSetChanged();
                            recyclerView.getLayoutManager().scrollToPosition(selectedPos);
                        }
                    });

                    while (!Objects.equals(confirmation, "3")) {
                        confirmation = btProtocol.receiveData();
                        System.out.println(confirmation);
                    }
                    confirmation = "";
                }

                btProtocol.clearSequence();
                doneSendingSeq = true;
            }

        }
    }
}