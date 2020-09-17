package com.example.clock_inontime.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock_inontime.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterJustify extends RecyclerView.Adapter<AdapterJustify.ViewHolderJustify>{
    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items;
    ArrayList<String> urls;

    public void update(String name, String url){
        items.add(name);
        urls.add(url);
        notifyDataSetChanged();
    }
    public AdapterJustify(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolderJustify onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recycler_justify, parent, false);
        return new ViewHolderJustify(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderJustify holder, int position) {
        //Intialise the elements of individual items
        holder.nameOfFile.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolderJustify extends RecyclerView.ViewHolder{
        TextView nameOfFile;
        public ViewHolderJustify(@NonNull final View itemView) {
            super(itemView);
            nameOfFile=(TextView)itemView.findViewById(R.id.nameOfFileJustify);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = recyclerView.getChildLayoutPosition(v);
                    StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Uploads").child(items.get(position));
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    String name="Uploads/"+items.get(position)+".pdf";
                    StorageReference path = storageReference.child(name);
                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setDataAndType(uri, "application/pdf");
                            context.startActivity(intent);
                        }
                    });
                }
            });
        }
    }
}
