package com.pl.edu.prz.robotui.control.moves;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.edu.prz.robotui.R;

import java.util.ArrayList;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<ArrayList<String>> coords_str;
    public int selectedPos = RecyclerView.NO_POSITION;
    public int step_counter;
    private ArrayList step_id;
    private AdapterView.OnItemClickListener mItemClickListener;

    public ListRecyclerViewAdapter(Context context, ArrayList step_id, ArrayList coords_str) {
        this.context = context;
        this.step_id = step_id;
        this.coords_str = coords_str;
        step_counter = step_id.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.step_id_txt.setText(String.valueOf(step_id.get(position)));
        holder.coords_str_txt.setText(parseStep(coords_str.get(position)));
        holder.deleteIcon.setEnabled(true);
        holder.itemView.setSelected(selectedPos == position);
    }


    @Override
    public int getItemCount() {
        return coords_str.size();
    }

    public void removeAt(int position, View v) {
        step_id.remove(position);
        coords_str.remove(position);
        for (int i = position; i < step_id.size(); i++) {
            step_id.set(i, "Krok " + i);
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, step_id.size());
        notifyItemRangeChanged(position, coords_str.size());
        notifyDataSetChanged();
        step_counter--;
    }

    public void addItem(ArrayList<String> item) {
        step_id.add("Krok " + step_counter);
        coords_str.add(item);
        notifyItemInserted(step_id.size() - 1);
        notifyDataSetChanged();
        step_counter++;
    }

    public void overwriteMoves(String moves) {
        ArrayList<ArrayList<String>> newMoves = new ArrayList<>();
        ArrayList<String> newSteps = new ArrayList<String>();
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < moves.length(); i++) {
            if (moves.charAt(i) == ',' || moves.charAt(i) == ';') {
                newSteps.add(number.toString());
                if (newSteps.size() == 3) {
                    ArrayList<String> cloneSteps = new ArrayList<String>(newSteps);
                    newMoves.add(cloneSteps);
                    newSteps.clear();
                }
                number = new StringBuilder();
                continue;
            }

            number.append(moves.charAt(i));

        }
        this.step_id = createStepsId(newMoves.size());
        this.coords_str.clear();
        this.coords_str.addAll(newMoves);
        System.out.println(this.coords_str);
    }

    public String parseStep(ArrayList step) {
        String stepString = "x: " + step.get(0).toString() + " y: " + step.get(1).toString() + " z: " + step.get(2).toString();
        return stepString;
    }

    private ArrayList<String> createStepsId(int size) {
        ArrayList<String> stepsId = new ArrayList<String>();
        int i = 0;
        for (; i < size; i++) {
            stepsId.add("Krok " + i);
        }
        this.step_counter = i;
        return stepsId;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View mCardView;
        TextView step_id_txt, coords_str_txt;
        ImageView editIcon, deleteIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            step_id_txt = itemView.findViewById(R.id.step_id_txt);
            coords_str_txt = itemView.findViewById(R.id.coords_txt);
            editIcon = itemView.findViewById(R.id.icon_edit);
            deleteIcon = itemView.findViewById(R.id.icon_delete);
            mCardView = itemView.findViewById(R.id.card_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedPos == getAdapterPosition()) {
                        selectedPos = RecyclerView.NO_POSITION;
                        notifyDataSetChanged();
                        return;
                    }
                    selectedPos = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });


            editIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.edit_move_dialog, null);

                    builder.setTitle("Edytuj krok " + position);
                    builder.setView(R.layout.edit_move_dialog);
                    builder.setView(dialogView);

                    EditText xValue = dialogView.findViewById(R.id.xValue);
                    EditText yValue = dialogView.findViewById(R.id.yValue);
                    EditText zValue = dialogView.findViewById(R.id.zValue);

                    xValue.setText((coords_str.get(position)).get(0));
                    yValue.setText((coords_str.get(position)).get(1));
                    zValue.setText((coords_str.get(position)).get(2));

                    builder.setNegativeButton("Anuluj", null);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<String> pushValues = new ArrayList<String>();
                            pushValues.add(xValue.getText().toString());
                            pushValues.add(yValue.getText().toString());
                            pushValues.add(zValue.getText().toString());
                            coords_str.set(position, pushValues);
                            notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
            });

            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    deleteIcon.setEnabled(false);
                    removeAt(position, v);
                }
            });

        }

    }

}
