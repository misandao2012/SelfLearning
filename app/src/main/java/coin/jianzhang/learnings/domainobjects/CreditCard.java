package coin.jianzhang.learnings.domainobjects;

/**
 * Created by jianzhang on 10/17/15.
 */
public class CreditCard {

    private long mId;
    private String mCardNumber;
    private String mBckgImageUrl;
    private String mLastName;
    private String mExpireDate;
    private String mFirstName;
    private long mUpdatedTime;
    private long mCreatedTime;
    private String mGuid;
    private String mEnabled;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getExpireDate() {
        return mExpireDate;
    }

    public void setExpireDate(String expireDate) {
        mExpireDate = expireDate;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getBckgImageUrl() {
        return mBckgImageUrl;
    }

    public void setBckgImageUrl(String bckgImageUrl) {
        mBckgImageUrl = bckgImageUrl;
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
    }

    public long getUpdatedTime() {
        return mUpdatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        mUpdatedTime = updatedTime;
    }

    public long getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(long createdTime) {
        mCreatedTime = createdTime;
    }

    public String getGuid() {
        return mGuid;
    }

    public void setGuid(String guid) {
        mGuid = guid;
    }

    public String getEnabled() {
        return mEnabled;
    }

    public void setEnabled(String enabled) {
        mEnabled = enabled;
    }
}
