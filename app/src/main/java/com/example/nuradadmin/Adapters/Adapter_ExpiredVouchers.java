package com.example.nuradadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nuradadmin.Models.Model_Voucher;
import com.example.nuradadmin.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Adapter_ExpiredVouchers extends RecyclerView.Adapter<Adapter_ExpiredVouchers.VoucherViewHolder> {
    private Context context;
    private List<Model_Voucher> voucherList;
    private SimpleDateFormat dateFormat;

    public Adapter_ExpiredVouchers(Context context, List<Model_Voucher> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Model_Voucher voucher = voucherList.get(position);
        holder.bindVoucher(voucher);
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public void setVoucherList(List<Model_Voucher> voucherList) {
        this.voucherList = voucherList;
        notifyDataSetChanged();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView voucherTitle, voucherDescription, voucherValidity, voucherValue, voucherCode;
        CardView cardView;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            voucherTitle = itemView.findViewById(R.id.voucher_title);
            voucherDescription = itemView.findViewById(R.id.voucher_description);
            voucherValidity = itemView.findViewById(R.id.voucher_validity);
            voucherValue = itemView.findViewById(R.id.voucher_value);
            voucherCode = itemView.findViewById(R.id.voucher_code);
        }

        public void bindVoucher(Model_Voucher voucher) {
            voucherTitle.setText(voucher.getTitle());
            voucherDescription.setText(voucher.getDescription());
            voucherValidity.setText("Valid Until: " + voucher.getValidity());
            double value = voucher.getValue();
            String formattedValue = formatPrice(value);
            voucherValue.setText("Get a Discount Value of: ₱" + formattedValue);
            voucherCode.setText("Redeem Code: " + voucher.getCode());

            // Display active vouchers with a specific background color
            cardView.setBackgroundColor(itemView.getResources().getColor(R.color.colorActiveVoucher));
        }

        private String formatPrice(double price) {
            return String.format(Locale.US, "%.2f", price);
        }
    }
}