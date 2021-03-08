package com.example.phoneapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {
    private static final String TAG = "PhoneAdapter";
    private List<Phone> phones;
    private MainActivity mainActivity;

    public PhoneAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.phones = mainActivity.getPhoneList();
    }

    public void addItem(Phone phone) {
        phones.add(phone);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        phones.remove(position);
        notifyDataSetChanged();
    }

    public void setItems(List<Phone> Phones) {
        this.phones = Phones;
        notifyDataSetChanged();
    }

    public void setItem(int position, Phone Phone) {
        phones.get(position).setName(Phone.getName());
        phones.get(position).setTel(Phone.getTel());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_item, parent, false);
        return new PhoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneViewHolder holder, int position) {
        holder.setItem(phones.get(position));
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class PhoneViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvTel;

        public PhoneViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvTel = itemView.findViewById(R.id.tel);

            itemView.setOnClickListener(v -> {
                Phone phone = phones.get(getAdapterPosition());
                mainActivity.update(phone, getAdapterPosition());
            });

        }

        public void setItem(Phone phone) {
            tvName.setText(phone.getName());
            tvTel.setText(phone.getTel());
        }

    }
}

