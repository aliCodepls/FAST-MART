package com.example.fastmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fastmart.db";
    private static final int DB_VERSION = 2; // bumped to handle schema change

    private static final String TABLE_FAVOURITES = "favourites";
    private static final String TABLE_CART = "cart";

    private static final String COL_PRODUCT_ID = "productId";
    private static final String COL_SELLER_ID = "sellerId";
    private static final String COL_NAME = "name";
    private static final String COL_TYPE = "type";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_PRICE = "price";
    private static final String COL_IMAGE_URL = "imageUrl";
    private static final String COL_QUANTITY = "quantity";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FAVOURITES + " (" +
                COL_PRODUCT_ID + " TEXT PRIMARY KEY, " +
                COL_SELLER_ID + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_TYPE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_PRICE + " REAL, " +
                COL_IMAGE_URL + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_CART + " (" +
                COL_PRODUCT_ID + " TEXT PRIMARY KEY, " +
                COL_SELLER_ID + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_TYPE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_PRICE + " REAL, " +
                COL_IMAGE_URL + " TEXT, " +
                COL_QUANTITY + " INTEGER DEFAULT 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    private ContentValues productToValues(Product p) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PRODUCT_ID, p.productId);
        cv.put(COL_SELLER_ID, p.sellerId);
        cv.put(COL_NAME, p.name);
        cv.put(COL_TYPE, p.type);
        cv.put(COL_DESCRIPTION, p.description);
        cv.put(COL_PRICE, p.price);
        cv.put(COL_IMAGE_URL, p.imageUrl != null ? p.imageUrl : "");
        return cv;
    }

    private Product cursorToProduct(Cursor c) {
        Product p = new Product();
        p.productId = c.getString(c.getColumnIndexOrThrow(COL_PRODUCT_ID));
        p.sellerId = c.getString(c.getColumnIndexOrThrow(COL_SELLER_ID));
        p.name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
        p.type = c.getString(c.getColumnIndexOrThrow(COL_TYPE));
        p.description = c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION));
        p.price = c.getDouble(c.getColumnIndexOrThrow(COL_PRICE));
        p.imageUrl = c.getString(c.getColumnIndexOrThrow(COL_IMAGE_URL));
        return p;
    }

    public void addFavourite(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        db.insertWithOnConflict(TABLE_FAVOURITES, null, productToValues(p), SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeFavourite(String productId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_FAVOURITES, COL_PRODUCT_ID + "=?", new String[]{productId});
    }

    public boolean isFavourite(String productId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_FAVOURITES, new String[]{COL_PRODUCT_ID},
                COL_PRODUCT_ID + "=?", new String[]{productId}, null, null, null);
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    public List<Product> getAllFavourites() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_FAVOURITES, null, null, null, null, null, null);
        while (c.moveToNext()) list.add(cursorToProduct(c));
        c.close();
        return list;
    }

    public void addToCart(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_CART, new String[]{COL_QUANTITY},
                COL_PRODUCT_ID + "=?", new String[]{p.productId}, null, null, null);
        if (c.moveToFirst()) {
            int qty = c.getInt(0) + 1;
            ContentValues cv = new ContentValues();
            cv.put(COL_QUANTITY, qty);
            db.update(TABLE_CART, cv, COL_PRODUCT_ID + "=?", new String[]{p.productId});
        } else {
            ContentValues cv = productToValues(p);
            cv.put(COL_QUANTITY, 1);
            db.insert(TABLE_CART, null, cv);
        }
        c.close();
    }

    public void removeFromCart(String productId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CART, COL_PRODUCT_ID + "=?", new String[]{productId});
    }

    public void updateCartQuantity(String productId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_QUANTITY, quantity);
        db.update(TABLE_CART, cv, COL_PRODUCT_ID + "=?", new String[]{productId});
    }

    public List<CartItem> getAllCartItems() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CART, null, null, null, null, null, null);
        while (c.moveToNext()) {
            Product p = cursorToProduct(c);
            int qty = c.getInt(c.getColumnIndexOrThrow(COL_QUANTITY));
            list.add(new CartItem(p, qty));
        }
        c.close();
        return list;
    }

    public void clearCart() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CART, null, null);
    }

    public static class CartItem {
        public Product product;
        public int quantity;
        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}