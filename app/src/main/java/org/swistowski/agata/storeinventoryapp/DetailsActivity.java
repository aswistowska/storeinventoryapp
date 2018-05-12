package org.swistowski.agata.storeinventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.swistowski.agata.storeinventoryapp.data.ProductContract.ProductEntry;


public class DetailsActivity extends AppCompatActivity
        implements android.app.LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private TextView mProductNameText;
    private TextView mProductPriceText;
    private TextView mProductQuantityText;
    private TextView mSupplierNameText;
    private TextView mSupplierPhoneNumberText;
    private Button mRemoveButton;
    private Button mReciveButton;
    private Button mOrderButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if(mCurrentProductUri != null) {
            setTitle(getString(R.string.details_activity_title));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mProductNameText = (TextView) findViewById(R.id.product_name);
        mProductPriceText = (TextView) findViewById(R.id.product_price);
        mProductQuantityText = (TextView) findViewById(R.id.product_quantity);
        mSupplierNameText = (TextView) findViewById(R.id.supplier_name);
        mSupplierPhoneNumberText = (TextView) findViewById(R.id.supplier_phone_number);
        mRemoveButton = (Button) findViewById(R.id.remove);
        mReciveButton = (Button) findViewById(R.id.recive);
        mOrderButton = (Button) findViewById(R.id.order);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_edit:
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (MainActivity)
                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String [] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this, mCurrentProductUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, final Cursor cursor) {
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String productName = cursor.getString(productNameColumnIndex);
            int productPrice = cursor.getInt(productPriceColumnIndex);
            int productQuantity = cursor.getInt(productQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            mProductNameText.setText(productName);
            mProductPriceText.setText(Integer.toString(productPrice));
            mProductQuantityText.setText(Integer.toString(productQuantity));
            mSupplierNameText.setText(supplierName);
            mSupplierPhoneNumberText.setText(supplierPhoneNumber);

            mRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int columnIdColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
                    int quantityRemoveColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                    int columnId = cursor.getInt(columnIdColumnIndex);
                    int quantityRemove = cursor.getInt(quantityRemoveColumnIndex);

                    if (quantityRemove > 0) {
                        quantityRemove = quantityRemove - 1;

                        ContentValues values = new ContentValues();
                        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityRemove);

                        Uri updateUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, columnId);
                        getContentResolver().update(updateUri, values, null, null);
                    }
                    if (quantityRemove == 0){
                        Toast.makeText(DetailsActivity.this, getString(R.string.details_quantity_empty),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mReciveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int columnIdColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
                    int quantityRecieveColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                    int columnId = cursor.getInt(columnIdColumnIndex);
                    int quantityRecieve = cursor.getInt(quantityRecieveColumnIndex);

                    quantityRecieve = quantityRecieve + 1;

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityRecieve);

                    Uri updateUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, columnId);
                    getContentResolver().update(updateUri, values, null, null);
                }
            });

            mOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
                    String suppliePhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + suppliePhoneNumber));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsDeleted == 0) {
                // If no rows were affected, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
