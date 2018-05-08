package org.swistowski.agata.storeinventoryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private TextView mProductNameText;
    private TextView mProductPriceText;
    private TextView mProductQuantityText;
    private TextView mSupplierNameText;
    private TextView mSupplierPhoneNumberText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        Uri currentProductUri = intent.getData();
        if(currentProductUri != null) {
            setTitle(getString(R.string.details_activity_title));
        }

        mProductNameText = (TextView) findViewById(R.id.product_name);
        mProductPriceText = (TextView) findViewById(R.id.product_price);
        mProductQuantityText = (TextView) findViewById(R.id.product_quantity);
        mSupplierNameText = (TextView) findViewById(R.id.supplier_name);
        mSupplierPhoneNumberText = (TextView) findViewById(R.id.supplier_phone_number);
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
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
