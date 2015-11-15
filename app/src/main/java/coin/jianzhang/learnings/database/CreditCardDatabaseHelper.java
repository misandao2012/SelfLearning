package coin.jianzhang.learnings.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import coin.jianzhang.learnings.domainobjects.CreditCard;

/**
 * Created by jianzhang on 10/17/15.
 */
public class CreditCardDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cards.sqlite";
    private static final int VERSION = 1;
    private static final String TABLE_CARD = "card";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_UPTATED_TIME = "updated";
    private static final String COLUMN_CARD_NUMBER = "card_number";
    private static final String COLUMN_IMAGE_URL = "background_image_url";
    private static final String COLUMN_ENABLED = "enabled";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_EXPIRE_DATE = "expiration_date";
    private static final String COLUMN_CREATED_TIME = "created";
    private static final String COLUMN_GUID = "guid";
    private static final String COLUMN_FIRST_NAME = "first_name";

    public CreditCardDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_CARD + " ("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_UPTATED_TIME + " integer, "
                + COLUMN_CARD_NUMBER + " varchar(256), "
                + COLUMN_IMAGE_URL + " varchar(512), "
                + COLUMN_ENABLED + " varchar(256), "
                + COLUMN_LAST_NAME + " varchar(256), "
                + COLUMN_EXPIRE_DATE + " varchar(256), "
                + COLUMN_CREATED_TIME + " integer, "
                + COLUMN_GUID + " varchar(256), "
                + COLUMN_FIRST_NAME + " varchar(256))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteAllData() {

        getWritableDatabase().delete(TABLE_CARD, null, null);
    }

    public long insertCreditCard(CreditCard card) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_UPTATED_TIME, card.getUpdatedTime());
        values.put(COLUMN_CARD_NUMBER, card.getCardNumber());
        values.put(COLUMN_IMAGE_URL, card.getBckgImageUrl());
        values.put(COLUMN_ENABLED, card.getEnabled());
        values.put(COLUMN_LAST_NAME, card.getLastName());
        values.put(COLUMN_EXPIRE_DATE, card.getExpireDate());
        values.put(COLUMN_CREATED_TIME, card.getCreatedTime());
        values.put(COLUMN_GUID, card.getGuid());
        values.put(COLUMN_FIRST_NAME, card.getFirstName());
        return getWritableDatabase().insert(TABLE_CARD, null, values);
    }

    public CreditCardCursor queryEnabledCardsOrderByCreatedTime() {

        Cursor wrapped = getReadableDatabase().query(TABLE_CARD,
                null,
                COLUMN_ENABLED + " = ?",
                new String[]{"true"},
                null,
                null,
                COLUMN_CREATED_TIME + " desc");
        return new CreditCardCursor(wrapped);
    }

    public CreditCardCursor queryCardByGuid(String guid) {

        Cursor wrapped = getReadableDatabase().query(TABLE_CARD,
                null,
                COLUMN_GUID + " = ?",
                new String[]{guid},
                null,
                null,
                null,
                "1");
        return new CreditCardCursor(wrapped);
    }

    public boolean updateCardByGuid(CreditCard card) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_UPTATED_TIME, card.getUpdatedTime());
        values.put(COLUMN_CARD_NUMBER, card.getCardNumber());
        values.put(COLUMN_IMAGE_URL, card.getBckgImageUrl());
        values.put(COLUMN_ENABLED, card.getEnabled());
        values.put(COLUMN_LAST_NAME, card.getLastName());
        values.put(COLUMN_EXPIRE_DATE, card.getExpireDate());
        values.put(COLUMN_CREATED_TIME, card.getCreatedTime());
        values.put(COLUMN_FIRST_NAME, card.getFirstName());
        return getWritableDatabase().update(TABLE_CARD, values, COLUMN_GUID + " =?"
                , new String[]{card.getGuid()}) > 0;
    }

    public boolean deleteCardByGuid(String guid) {

        return getWritableDatabase().delete(TABLE_CARD, COLUMN_GUID + " =?", new String[]{guid}) > 0;
    }

    public static class CreditCardCursor extends CursorWrapper {

        public CreditCardCursor(Cursor c) {
            super(c);
        }

        public CreditCard getCard() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            CreditCard card = new CreditCard();
            card.setId(getLong(getColumnIndex(COLUMN_ID)));
            card.setUpdatedTime(getLong(getColumnIndex(COLUMN_UPTATED_TIME)));
            card.setCardNumber(getString(getColumnIndex(COLUMN_CARD_NUMBER)));
            card.setBckgImageUrl(getString(getColumnIndex(COLUMN_IMAGE_URL)));
            card.setEnabled(getString(getColumnIndex(COLUMN_ENABLED)));
            card.setLastName(getString(getColumnIndex(COLUMN_LAST_NAME)));
            card.setExpireDate(getString(getColumnIndex(COLUMN_EXPIRE_DATE)));
            card.setCreatedTime(getLong(getColumnIndex(COLUMN_CREATED_TIME)));
            card.setGuid(getString(getColumnIndex(COLUMN_GUID)));
            card.setFirstName(getString(getColumnIndex(COLUMN_FIRST_NAME)));
            return card;
        }
    }
}
