package com.smartlines.laravel_restful.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.smartlines.laravel_restful.Activities.MainActivity;
import com.smartlines.laravel_restful.Models.Article;
import com.smartlines.laravel_restful.R;

import java.util.ArrayList;


/**
 * Created by Ivan Galves on 20/10/2019.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ObjetoViewHolder> implements Filterable {
    private ArrayList<Article> articles;
    public ArrayList<Article> mFilteredList;
    private Context context;

    private View.OnClickListener onClickListener;
    private View.OnCreateContextMenuListener onCreateContextMenuListener;

    public RecyclerAdapter(Context context, ArrayList<Article> articles) {
        this.articles = articles;
        this.mFilteredList = articles;
        this.context = context;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnCreateContextMenuListener(View.OnCreateContextMenuListener onCreateContextMenuListener) {
        this.onCreateContextMenuListener = onCreateContextMenuListener;
    }

    @Override
    public ObjetoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_articles, parent, false);
        v.setOnClickListener(onClickListener);
        v.setOnCreateContextMenuListener(onCreateContextMenuListener);
        return new ObjetoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ObjetoViewHolder holder, int position) {
        holder.titulo.setText(mFilteredList.get(position).getTitle());
        holder.body.setText(mFilteredList.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    mFilteredList = articles;
                } else {
                    ArrayList<Article> filteredList = new ArrayList<>();
                    for (Article article : articles) {
                        Log.i("Filtor","Intento");
                        if (article.getTitle().toLowerCase().contains(charString)||article.getBody().toLowerCase().contains(charString)) {
                            Log.i("Filtor","Coincidencia");
                            filteredList.add(article);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Article>) filterResults.values;
                notifyDataSetChanged();

                if (getItemCount() == 0)
                    MainActivity.tv.setText("No se encontro coincidencia con \"" + charSequence.toString().trim() + "\"");
                else
                    MainActivity.tv.setText("");
            }
        };
    }

    public void borrar(int position){

        mFilteredList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mFilteredList.size());

    }

    public void borrararticle(int id){
        int position=getPositionArticle(id);
        articles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, articles.size());
    }

    public int getPositionArticle(int id) {
        int pos = -1;
        int i = 0;
        for (Article c : articles) {
            if (c.getId()==(id)) {
                pos = i;
                break;
            }
            i++;
        }
        return pos;
    }

    public void insertar(/*DEBE RECIBIR POSICION Y OBJETO AGREGADO A LA LISTA*/){
        //animalsList.add(position,"" + itemLabel);
        //notifyItemInserted(position);
        //mRecyclerView.scrollToPosition(position);
    }

    class ObjetoViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView body;

        ObjetoViewHolder(View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
            body = (TextView) itemView.findViewById(R.id.body);
        }
    }
}
