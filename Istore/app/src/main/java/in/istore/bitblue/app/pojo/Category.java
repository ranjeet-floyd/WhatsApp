package in.istore.bitblue.app.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private String CategoryName;
    private String StoreId;

    public Category(Parcel source) {
        CategoryName = source.readString();
        StoreId = source.readString();
    }

    public Category(String CategoryName, String StoreId) {
        this.CategoryName = CategoryName;
        this.StoreId = StoreId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel data, int flags) {
        data.writeString(CategoryName);
        data.writeString(StoreId);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new Category(parcel);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };















    public Category() {
    }
    public Category(String categoryName) {
        CategoryName = categoryName;
    }
    public String getCategoryName() {
        return CategoryName;
    }
    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
    public String getStoreId() {
        return StoreId;
    }
    public void setStoreId(String storeId) {
        StoreId = storeId;
    }
}
