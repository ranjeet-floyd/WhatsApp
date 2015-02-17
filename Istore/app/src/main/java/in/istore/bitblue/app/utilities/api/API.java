package in.istore.bitblue.app.utilities.api;

public class API {

    //Login
    public final static String BITSTORE_LOGIN = "http://118.139.162.161:4488/BitStore/Login";

    //SignUP
    public final static String BITSTORE_ADMIN_SIGN_UP = "http://118.139.162.161:4488/BitStore/AdminSignUp";
    public final static String BITSTORE_STAFF_SIGN_UP = "http://118.139.162.161:4488/BitStore/StaffSignUp";

    //Change Password
    public final static String BITSTORE_ADMIN_CHANGE_PASSWORD = "http://118.139.162.161:4488/BitStore/changePassword";
    public final static String BITSTORE_STAFF_CHANGE_PASSWORD = "http://118.139.162.161:4488/BitStore/StaffchangePassword";

    //Forgot Password
    public final static String BITSTORE_FORGOT_PASSWORD = "http://118.139.162.161:4488/BitStore/ForgotPassword";

    //Staff Management
    public final static String BITSTORE_VIEW_STAFF = "http://118.139.162.161:4488/BitStore/ViewStaff";

    //Supplier
    public final static String BITSTORE_ADD_SUPPLIER = "http://118.139.162.161:4488/BitStore/SupplierSignUp";
    public final static String BITSTORE_SUPPLIER_INFO = "http://118.139.162.161:4488/BitStore/SupplierInfo";

    //Product
    public final static String BITSTORE_ADD_PRODUCT = "http://118.139.162.161:4488/BitStore/ProductInsert";
    public final static String BITSTORE_ADD_PRODUCT_QUANTITY = "http://118.139.162.161:4488/BitStore/AddProductQuantity";
    public final static String BITSTORE_GET_ALLPRODUCTS_FORCATEGORY = "http://118.139.162.161:4488/BitStore/ListAllProducts";
    public final static String BITSTORE_GET_PRODUCTDETAILS = "http://118.139.162.161:4488/BitStore/GetProductDetails";
    public final static String BITSTORE_CHECK_PRODUCTID = "http://118.139.162.161:4488/BitStore/CheckproductId";
    public final static String BITSTORE_UPDATE_PRODUCTDETAILS = "http://118.139.162.161:4488/BitStore/ProductTableUpdate";

    //Sold Product
    public final static String BITSTORE_ADD_SOLDITEM = "http://118.139.162.161:4488/BitStore/SoldItemInsert";
    public final static String BITSTORE_GET_ALLSOLDPRODUCTS = "http://118.139.162.161:4488/BitStore/ListAllSoldProducts";
    public final static String BITSTORE_GET_SOLDPRODUCTDETAILS = "http://118.139.162.161:4488/BitStore/GetSoldProductDetail";

    //Category
    public final static String BITSTORE_ADD_CATEGORY = "http://118.139.162.161:4488/BitStore/CategoryInsert";
    public final static String BITSTORE_ADD_SUBCATEGORY = "http://118.139.162.161:4488/BitStore/SubcategoryInsert";
    public final static String BITSTORE_GET_ALL_CATEGORIES = "http://118.139.162.161:4488/BitStore/GetCategory";

    //Out Of Stock Items
    public final static String BITSTORE_ADD_OUTOFSTOCKITEMS = "http://118.139.162.161:4488/BitStore/OutOfStockItemsInsert";
    public final static String BITSTORE_GET_OUTOFSTOCKITEMS = "http://118.139.162.161:4488/BitStore/OutOfStockItems";

    //Customer
    public final static String BITSTORE_VIEW_ALLCUSTOMERS = "http://118.139.162.161:4488/BitStore/ViewCustomer";
    public final static String BITSTORE_VIEW_STAFF_CUSTOMERS = "http://118.139.162.161:4488/BitStore/StaffViewCustomer";

    //Admin Transaction
    public final static String BITSTORE_GET_TOTAL_REVENUE = "http://118.139.162.161:4488/BitStore/StockSalesTotalRevenue";
    public final static String BITSTORE_GET_TODAY_SALES = "http://118.139.162.161:4488/BitStore/StockSalesTotalRevenue";
    public final static String BITSTORE_GET_TOTAL_REVENUE_FOR_RANGE = "http://118.139.162.161:4488/BitStore/GetTotalRevenue";
    public final static String BITSTORE_GET_TOTAL_REVENUE_FOR_STAFF = "http://118.139.162.161:4488/BitStore/GetTotalRevenueForStaff";
    public final static String BITSTORE_GET_SUM_OF_TOTAL_REVENUE_FOR_STAFF_BETWEEN_RANGE = "http://118.139.162.161:4488/BitStore/GetTotalRevenueSumForStaff";
    public final static String BITSTORE_GET_SUM_OF_TOTAL_REVENUE_FOR_PRODUCT_BETWEEN_RANGE = "http://118.139.162.161:4488/BitStore/ProductTotalRevenue";
    public final static String BITSTORE_GET_STAFF_IDS = "http://118.139.162.161:4488/BitStore/GetStaffIds";
    public final static String BITSTORE_GET_PRODUCTNAMES = "http://118.139.162.161:4488/BitStore/GetProductNames";
    public final static String BITSTORE_GET_OUTOFSTOCK_ITEMS = "http://118.139.162.161:4488/BitStore/GetOutOfStockItems";

    //Staff Transaction
    public final static String BITSTORE_GET_TODAY_SALES_FORSTAFF = "http://118.139.162.161:4488/";      ///

    //Cart
    public final static String BITSTORE_GET_CART_ITEMS = "http://118.139.162.161:4488/BitStore/ItemsInCart";

}
