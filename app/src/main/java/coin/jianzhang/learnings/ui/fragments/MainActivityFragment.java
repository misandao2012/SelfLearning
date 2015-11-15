package coin.jianzhang.learnings.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import coin.jianzhang.learnings.R;
import coin.jianzhang.learnings.constants.Constants;
import coin.jianzhang.learnings.constants.Preference;
import coin.jianzhang.learnings.database.CreditCardDatabaseHelper;
import coin.jianzhang.learnings.database.CreditCardDatabaseHelper.CreditCardCursor;
import coin.jianzhang.learnings.domainobjects.CreditCard;
import coin.jianzhang.learnings.loader.SQLiteCursorLoader;
import coin.jianzhang.learnings.network.WebUtil;
import coin.jianzhang.learnings.utils.ImageLoader;
import coin.jianzhang.learnings.utils.Utils;


public class MainActivityFragment extends VisibleFragment implements LoaderCallbacks<Cursor> {

    private static final String TAG = "CreditCards";
    private ImageLoader mImageLoader;
    private static CreditCardDatabaseHelper mDatabaseHelper;
    private CreditCardCursor mCursor;
    private ProgressBar mProgressBar;
    private CreditCardCursorAdapter mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        getLoaderManager().initLoader(0, null, this);

        mImageLoader = new ImageLoader(getActivity());
        mDatabaseHelper = new CreditCardDatabaseHelper(getActivity());

        View parentView = inflater.inflate(R.layout.fragment_main, container, false);
        mProgressBar = (ProgressBar) parentView.findViewById(R.id.progress_bar);
        return parentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (sharedPrefs.getBoolean(Preference.PREF_FIRST_TIME_START, true)) {
            //first time install the App
            if (WebUtil.networkConnected(getActivity())) {
                FirstTimeSetupDataTask firstTimeSetupTask = new FirstTimeSetupDataTask();
                firstTimeSetupTask.execute();
            } else {
                WebUtil.showNetworkDialog(getActivity());
            }
        } else {
            //after first time, the database is already setup
            if (WebUtil.networkConnected(getActivity())) {
                UpdateDataTask updateDataTask = new UpdateDataTask();
                updateDataTask.execute();
            } else {
                WebUtil.showNetworkDialog(getActivity());
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CardListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // create an adapter to point at this cursor
        CreditCardCursorAdapter adapter = new CreditCardCursorAdapter(getActivity(),
                (CreditCardCursor)cursor);
        getListView().setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
        super.onDestroy();
    }

    private void setUpDataBaseFromJson(String jsonData) throws JSONException {

        JSONObject jMessage = new JSONObject(jsonData);
        mDatabaseHelper.deleteAllData();
        JSONArray jArr = jMessage.getJSONArray("results");

        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jCreditCard = jArr.getJSONObject(i);
            CreditCard card = new CreditCard();
            card.setUpdatedTime(Utils.parseDateTime(jCreditCard.getString("updated"),
                    "yyyy-MM-dd'T'HH:mm:ssZ"));
            card.setGuid(jCreditCard.getString("guid"));
            setSomeCardInfo(card, jCreditCard);
            //insert all the data to database
            card.setId(mDatabaseHelper.insertCreditCard(card));
        }
    }

    private void updateBaseFromJson(String jsonData) throws JSONException {

        JSONObject jMessage = new JSONObject(jsonData);
        JSONArray jArr = jMessage.getJSONArray("results");

        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jCreditCard = jArr.getJSONObject(i);
            long updatedTime = Utils.parseDateTime(jCreditCard.getString("updated"),
                    "yyyy-MM-dd'T'HH:mm:ssZ");
            String guid = jCreditCard.getString("guid");
            //get the card by the guid
            CreditCardCursor cursor = mDatabaseHelper.queryCardByGuid(guid);
            cursor.moveToFirst();

            if (cursor.isAfterLast()) {
                //if the guid doesn't exists, delete the card from database
                mDatabaseHelper.deleteCardByGuid(guid);
                updateAdapter();
            } else if (updatedTime > cursor.getCard().getUpdatedTime()) {
                //if the updated time changed, update the card
                CreditCard card = cursor.getCard();
                setSomeCardInfo(card, jCreditCard);
                card.setUpdatedTime(updatedTime);
                mDatabaseHelper.updateCardByGuid(card);
                updateAdapter();
            }
            cursor.close();
        }
    }

    private class FirstTimeSetupDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mImageLoader.clearCache();
        }

        @Override
        protected String doInBackground(Void... params) {
            return WebUtil.getJson(Constants.COIN_BASE_URL);
        }

        @Override
        protected void onPostExecute(final String jsonData) {
            super.onPostExecute(jsonData);
            mProgressBar.setVisibility(View.GONE);
            try {
                setUpDataBaseFromJson(jsonData);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            setupListViewAdapter();
            //set the preference "first time" to false
            setFirstTimeStartFalse();
        }
    }

    private class UpdateDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return WebUtil.getJson(Constants.COIN_BASE_URL);
        }

        @Override
        protected void onPostExecute(final String jsonData) {
            super.onPostExecute(jsonData);
            try {
                updateBaseFromJson(jsonData);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void setSomeCardInfo(CreditCard card, JSONObject jCreditCard) {

        try {
            card.setCardNumber(jCreditCard.getString("card_number"));
            card.setBckgImageUrl(jCreditCard.getString("background_image_url"));
            card.setLastName(jCreditCard.getString("last_name"));
            card.setFirstName(jCreditCard.getString("first_name"));
            card.setExpireDate(jCreditCard.getString("expiration_date"));
            card.setCreatedTime(Utils.parseDateTime(jCreditCard.getString("created"),
                    "yyyy-MM-dd'T'HH:mm:ssZ"));
            card.setEnabled(jCreditCard.getString("enabled"));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void setupListViewAdapter() {

        //get all the enabled cards ordered by created time
        mCursor = mDatabaseHelper.queryEnabledCardsOrderByCreatedTime();
        mAdapter = new CreditCardCursorAdapter(getActivity(),
                mCursor);
        getListView().setAdapter(mAdapter);
    }

    private void setFirstTimeStartFalse() {

        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(Preference.PREF_FIRST_TIME_START, false);
        editor.apply();
    }

    private void updateAdapter(){

        mCursor = mDatabaseHelper.queryEnabledCardsOrderByCreatedTime();
        mAdapter.notifyDataSetChanged();
    }

    private static class CardListCursorLoader extends SQLiteCursorLoader {

        public CardListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            // query the list of runs
            return mDatabaseHelper.queryEnabledCardsOrderByCreatedTime();
        }

    }

    private static class ViewHolder {

        TextView cardNumberTextView;
        TextView firstNameTextView;
        TextView lastNameTextView;
        TextView expireDateTextView;
        ImageView backgroundImageView;
    }

    private class CreditCardCursorAdapter extends CursorAdapter {

        private CreditCardCursor mCreditCardCursor;

        public CreditCardCursorAdapter(Context context, CreditCardCursor cursor) {
            super(context, cursor, 0);
            mCreditCardCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_card,
                    parent, false);
            ViewHolder holder = new ViewHolder();
            findViewsForViewHolder(holder, rowView);
            rowView.setTag(holder);
            return rowView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            CreditCard card = mCreditCardCursor.getCard();
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.cardNumberTextView.setText(Utils.formatCardNumber(card.getCardNumber()));
            holder.firstNameTextView.setText(card.getFirstName().toUpperCase());
            holder.lastNameTextView.setText(card.getLastName().toUpperCase());
            holder.expireDateTextView.setText(Utils.formatExpireDate(card.getExpireDate()));
            mImageLoader.DisplayImage(card.getBckgImageUrl(), holder.backgroundImageView);
        }
    }

    private void findViewsForViewHolder(ViewHolder viewHolder, View rowView) {

        viewHolder.cardNumberTextView = (TextView) rowView
                .findViewById(R.id.card_list_item_cardNumberTextView);
        viewHolder.firstNameTextView = (TextView) rowView
                .findViewById(R.id.card_list_item_firstNameTextView);
        viewHolder.lastNameTextView = (TextView) rowView
                .findViewById(R.id.card_list_item_lastNameTextView);
        viewHolder.expireDateTextView = (TextView) rowView
                .findViewById(R.id.card_list_item_expireDateTextView);
        viewHolder.backgroundImageView = (ImageView) rowView
                .findViewById(R.id.card_list_item_backgroundImageView);
    }
}