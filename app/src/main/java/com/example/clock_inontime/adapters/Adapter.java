package com.example.clock_inontime.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock_inontime.R;
import com.example.clock_inontime.TimeCardDetailActivity;
import com.example.clock_inontime.entities.TimeCard;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.TimeCardViewHolder>{
    List<TimeCard> list;

    public Adapter(List<TimeCard> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TimeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler,parent,false);
        TimeCardViewHolder holder = new TimeCardViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeCardViewHolder holder, int position) {
        TimeCard timecard = list.get(position);
        if(timecard.getEntryTime().equals("") && timecard.getOutTime().equals("")){
            holder.justify.setText("Motivo: "+timecard.getJustify());
            holder.workhour.setText("Jornada laboral no existente");
            holder.date.setText(timecard.getDate());
            holder.entry.setText("Inicio: No registrado");
            holder.out.setText("Fin: No registrado");
            holder.entryLocation.setText("Sin ubicación");
            holder.outLocation.setText("Sin ubicación");
        }else{
            holder.workhour.setText("Jornada: "+timecard.getHour()+ " horas y "+ timecard.getMin()+" minutos.");
            holder.date.setText(timecard.getDate());
            holder.justify.setText("Sin justificación");
            holder.entry.setText("Inicio: "+timecard.getEntryTime());
            holder.out.setText("Fin: "+timecard.getOutTime());
            holder.entryLocation.setText(timecard.getEntryLocation());
            holder.outLocation.setText(timecard.getOutLocation());
        }
        holder.setOnClickListeners();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class TimeCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView date;
        TextView entry;
        TextView out;
        TextView entryLocation;
        TextView justify;
        TextView outLocation;
        TextView workhour;
        CardView card_view;

        //Context reference
        Context context;
        public TimeCardViewHolder(@NonNull View itemView) {
            super(itemView);

            context=itemView.getContext();
            date=(TextView)itemView.findViewById(R.id.textview_date);
            justify=(TextView)itemView.findViewById(R.id.justifyText);
            entry=(TextView)itemView.findViewById(R.id.textview_entry);
            out=(TextView)itemView.findViewById(R.id.textview_out);
            entryLocation=(TextView) itemView.findViewById(R.id.entryLocationText);
            outLocation=(TextView)itemView.findViewById(R.id.outLocationText);
            workhour=(TextView)itemView.findViewById(R.id.workHourText);
            card_view=(CardView)itemView.findViewById(R.id.card_viewOut);
        }
        void setOnClickListeners(){
           card_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, TimeCardDetailActivity.class);

            intent.putExtra("entryLocationDetail", entryLocation.getText());
            intent.putExtra("justifyDetail", justify.getText());
            intent.putExtra("outLocationDetail", outLocation.getText());
            intent.putExtra("dateDetail",date.getText());
            intent.putExtra("entryDetail",entry.getText());
            intent.putExtra("outDetail",out.getText());
            intent.putExtra("workHour",workhour.getText());
            context.startActivity(intent);
        }
    }
}
