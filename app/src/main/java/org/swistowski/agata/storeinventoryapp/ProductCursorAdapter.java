package org.swistowski.agata.storeinventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.swistowski.agata.storeinventoryapp.data.ProductContract.ProductEntry;


public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price);
        TextView productQuantityView = (TextView) view.findViewById(R.id.product_quantity);
        Button sellButton = (Button) view.findViewById(R.id.sell);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);

        productNameTextView.setText(productName);
        productPriceTextView.setText(Integer.toString(productPrice));
        productQuantityView.setText(Integer.toString(productQuantity));

        final int cursorPosition = cursor.getPosition();

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cursor.moveToPosition(cursorPosition);

                int columnIdIndex = cursor.getColumnIndex(ProductEntry._ID);
                int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                String columnId = cursor.getString(columnIdIndex);
                int quantity = cursor.getInt(quantityColumnIndex);
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.decreaseCount( Integer.valueOf(columnId), Integer.valueOf(quantity));
            }
        });
    }
}
