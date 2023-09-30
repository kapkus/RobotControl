package com.pl.edu.prz.robotui.control.moves;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.edu.prz.robotui.R;

import java.util.ArrayList;

public class DBRecyclerViewAdapter extends RecyclerView.Adapter<DBRecyclerViewAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList moves_id;
    private final ArrayList moves_name;
    private final ArrayList moves_pos;
    private final ArrayList moves_count;
    private final getSequenceListener sequenceListener;
    private int mItemSelected = -1;

    DBRecyclerViewAdapter(Context context, ArrayList moves_id, ArrayList moves_name, ArrayList moves_pos, ArrayList moves_count, getSequenceListener listener) {
        this.context = context;
        this.moves_id = moves_id;
        this.moves_name = moves_name;
        this.moves_pos = moves_pos;
        this.moves_count = moves_count;
        this.sequenceListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.db_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.moves_id_txt.setText(String.valueOf(moves_id.get(position)));
        holder.moves_name_txt.setText(String.valueOf(moves_name.get(position)));
        holder.moves_pos_txt.setText(String.valueOf(moves_pos.get(position)));
        holder.moves_count_txt.setText(String.valueOf(moves_count.get(position)));

        if (mItemSelected == position) {
            holder.mainLayout.setSelected(true);
            holder.mainLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.moves_pos_txt.setVisibility(View.VISIBLE);
            holder.loadButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.mainLayout.setSelected(false);
            holder.mainLayout.setBackgroundColor(Color.parseColor("#000000"));
            holder.moves_pos_txt.setVisibility(View.GONE);
            holder.loadButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return moves_id.size();
    }

    public interface getSequenceListener {
        void onLoadButtonClick(int pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView moves_id_txt, moves_name_txt, moves_pos_txt, moves_count_txt;
        LinearLayout mainLayout;
        Button loadButton, deleteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            loadButton = itemView.findViewById(R.id.button_load);
            deleteButton = itemView.findViewById(R.id.button_delete);
            moves_id_txt = itemView.findViewById(R.id.coords_txt);
            moves_name_txt = itemView.findViewById(R.id.moves_name_txt);
            moves_pos_txt = itemView.findViewById(R.id.moves_pos_txt);
            moves_count_txt = itemView.findViewById(R.id.moves_count_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemSelected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    removeAt(position, v);
                }
            });

            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int loadPosition = getAdapterPosition();
                    sequenceListener.onLoadButtonClick(Integer.parseInt(moves_id.get(loadPosition).toString()));
                }
            });
        }

        public void removeAt(int position, View v) {
            moves_id.remove(position);
            moves_name.remove(position);
            moves_pos.remove(position);
            moves_count.remove(position);
            MyDatabaseHelper myDB = new MyDatabaseHelper(context);
            myDB.deleteMoves("'" + moves_name_txt.getText() + "'");
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, moves_id.size());
        }

    }
}
