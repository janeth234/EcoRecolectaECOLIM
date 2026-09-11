package com.senati.ecorecolecta;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/**
 * Adaptador que enlaza la lista de objetos Residuo obtenidos de SQLite
 * con las tarjetas (item_residuo.xml) mostradas en el RecyclerView.
 */
public class ResiduoAdapter extends RecyclerView.Adapter<ResiduoAdapter.ResiduoViewHolder> {

    private final List<Residuo> listaResiduos;

    public ResiduoAdapter(List<Residuo> listaResiduos) {
        this.listaResiduos = listaResiduos;
    }

    @NonNull
    @Override
    public ResiduoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_residuo, parent, false);
        return new ResiduoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ResiduoViewHolder holder, int position) {
        Residuo r = listaResiduos.get(position);
        holder.tvTipo.setText(r.getTipoResiduo());
        holder.tvCantidad.setText(String.format(Locale.getDefault(), "Cantidad: %.2f kg", r.getCantidadKg()));
        holder.tvArea.setText("Área: " + r.getArea());
        holder.tvFecha.setText(r.getFechaHora());

        if (r.getSincronizado() == 1) {
            holder.tvSincronizado.setText("✔ Sincronizado con servidor");
            holder.tvSincronizado.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvSincronizado.setText("⏳ Pendiente de sincronizar");
            holder.tvSincronizado.setTextColor(Color.parseColor("#D32F2F"));
        }
    }

    @Override
    public int getItemCount() {
        return listaResiduos.size();
    }

    static class ResiduoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipo, tvCantidad, tvArea, tvFecha, tvSincronizado;

        ResiduoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvArea = itemView.findViewById(R.id.tvArea);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvSincronizado = itemView.findViewById(R.id.tvSincronizado);
        }
    }
}
