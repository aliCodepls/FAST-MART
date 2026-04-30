package com.example.fastmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("#" + order.orderId);
        // ✅ Use orderDate (matches Firebase schema)
        holder.tvOrderTimestamp.setText(order.orderDate != null ? order.orderDate : "");
        holder.tvOrderStatus.setText(order.status != null ? order.status : "");
        // ✅ Use totalAmount (matches Firebase schema)
        holder.tvOrderTotal.setText("Total: $" + String.format("%.2f", order.totalAmount));

        // Build items summary from Map
        StringBuilder itemsSb = new StringBuilder();
        if (order.items != null) {
            for (Order.OrderItem item : order.items.values()) {
                itemsSb.append(item.productName)
                        .append("  x").append(item.quantity)
                        .append("  $").append(String.format("%.2f", item.price))
                        .append("\n");
            }
        }
        holder.tvOrderItems.setText(itemsSb.toString().trim());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderTimestamp, tvOrderStatus, tvOrderItems, tvOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderTimestamp = itemView.findViewById(R.id.tvOrderTimestamp);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}