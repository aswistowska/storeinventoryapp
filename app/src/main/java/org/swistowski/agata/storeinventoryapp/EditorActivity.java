package org.swistowski.agata.storeinventoryapp;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.swistowski.agata.storeinventoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity
        implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private boolean mProductHasChanged = false;

    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_add_product));
        } else {
            setTitle(getString(R.string.editor_activity_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mProductPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

    }

    private boolean saveProduct() {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        if (mCurrentProductUri == null && TextUtils.isEmpty(productNameString) &&
                TextUtils.isEmpty(productPriceString) && TextUtils.isEmpty(productQuantityString)
                && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneNumberString)) {
            return true;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        int productPrice = 0;
        int productQuantity = 0;

        if (!TextUtils.isEmpty(productPriceString)) {
            productPrice = Integer.parseInt(productPriceString);
        }
        if (!TextUtils.isEmpty(productQuantityString)) {
            productQuantity = Integer.parseInt(productQuantityString);
        }

        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

        if (mCurrentProductUri == null) {
            Uri newUri;
            try {
                newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            int rowsChanged;
            try {
                rowsChanged = getContentResolver().update(mCurrentProductUri, values, null, null);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (rowsChanged == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if (saveProduct()) {
                    finish();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (mCurrentProductUri == null) {
                    if (!mProductHasChanged) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        return true;
                    }
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };
                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                } else {
                    final Intent intent = new Intent(this, DetailsActivity.class);
                    intent.setData(mCurrentProductUri);

                    if (!mProductHasChanged) {
                        NavUtils.navigateUpTo(EditorActivity.this, intent);
                        return true;
                    }
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    NavUtils.navigateUpTo(EditorActivity.this, intent);
                                }
                            };
                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
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
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
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

            mProductNameEditText.setText(productName);
            mProductPriceEditText.setText(Integer.toString(productPrice));
            mProductQuantityEditText.setText(Integer.toString(productQuantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentProductUri == null) {
            if (!mProductHasChanged) {
                super.onBackPressed();
                return;
            }
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    };
            showUnsavedChangesDialog(discardButtonClickListener);
        } else {
            final Intent intent = new Intent(this, DetailsActivity.class);
            intent.setData(mCurrentProductUri);

            if (!mProductHasChanged) {
                NavUtils.navigateUpTo(EditorActivity.this, intent);
                return;
            }
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, navigate to parent activity.
                            NavUtils.navigateUpTo(EditorActivity.this, intent);
                        }
                    };
            // Show a dialog that notifies the user they have unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
            return;
        }
    }
}
