package in.istore.bitblue.app.utilities.api;

public class API {

    //Login
    public final static String BITSTORE_LOGIN = "http://192.168.0.104:4488/BitStore/Login";

    //SignUP
    public final static String BITSTORE_ADMIN_SIGN_UP = "http://192.168.0.104:4488/BitStore/AdminSignUp";
    public final static String BITSTORE_STAFF_SIGN_UP = "http://192.168.0.104:4488/BitStore/StaffSignUp";

    //Change Password
    public final static String BITSTORE_ADMIN_CHANGE_PASSWORD = "http://192.168.0.104:4488/BitStore/changePassword";
    public final static String BITSTORE_STAFF_CHANGE_PASSWORD = "http://192.168.0.104:4488/BitStore/StaffchangePassword";

    //Forgot Password
    public final static String BITSTORE_FORGOT_PASSWORD = "http://192.168.0.104:4488/BitStore/ForgotPassword";

    //Staff Management
    public final static String BITSTORE_VIEW_STAFF = "http://192.168.0.104:4488/BitStore/ViewStaff";

    //Supplier
    public final static String BITSTORE_ADD_SUPPLIER = "http://192.168.0.104:4488/BitStore/SupplierSignUp";
    public final static String BITSTORE_SUPPLIER_INFO = "http://192.168.0.104:4488/BitStore/SupplierInfo";

    //Product
    public final static String BITSTORE_ADD_PRODUCT = "http://192.168.0.104:4488/BitStore/ProductInsert";
    public final static String BITSTORE_ADD_PRODUCT_QUANTITY = "http://192.168.0.104:4488/BitStore/AddProductQuantity";
    public final static String BITSTORE_GET_ALLPRODUCTS_FORCATEGORY = "http://192.168.0.104:4488/BitStore/ListAllProducts";
    public final static String BITSTORE_GET_PRODUCTDETAILS = "http://192.168.0.104:4488/BitStore/GetProductDetails";
    public final static String BITSTORE_CHECK_EXISTINGPRODUCTID = "http://192.168.0.104:4488/BitStore/CheckproductId";
    public final static String BITSTORE_UPDATE_PRODUCTDETAILS = "http://192.168.0.104:4488/BitStore/ProductTableUpdate";

    //Sold Product
    public final static String BITSTORE_ADD_SOLDITEM = "http://192.168.0.104:4488/BitStore/SoldItemInsert";
    public final static String BITSTORE_GET_ALLSOLDPRODUCTS = "http://192.168.0.104:4488/BitStore/ListAllSoldProducts";
    public final static String BITSTORE_GET_SOLDPRODUCTDETAILS = "http://192.168.0.104:4488/BitStore/GetSoldProductDetail";

    //Category
    public final static String BITSTORE_ADD_CATEGORY = "http://192.168.0.104:4488/BitStore/CategoryInsert";
    public final static String BITSTORE_ADD_SUBCATEGORY = "http://192.168.0.104:4488/BitStore/SubcategoryInsert";
    public final static String BITSTORE_GET_ALL_CATEGORIES = "http://192.168.0.104:4488/BitStore/GetCategory";
    public final static String BITSTORE_GET_ALL_SUBCATEGORIES = "http://192.168.0.104:4488/BitStore/GetSubCategory";

    //Out Of Stock Items
    public final static String BITSTORE_ADD_OUTOFSTOCKITEMS = "http://192.168.0.104:4488/BitStore/OutOfStockItemsInsert";
    public final static String BITSTORE_GET_OUTOFSTOCKITEMS = "http://192.168.0.104:4488/BitStore/OutOfStockItems";

    //Customer
    public final static String BITSTORE_VIEW_ALLCUSTOMERS = "http://192.168.0.104:4488/BitStore/ViewCustomer";
    public final static String BITSTORE_VIEW_STAFF_CUSTOMERS = "http://192.168.0.104:4488/BitStore/StaffViewCustomer";

    //Admin Transaction
    public final static String BITSTORE_GET_TOTAL_REVENUE = "http://192.168.0.104:4488/BitStore/StockSalesTotalRevenue";
    public final static String BITSTORE_GET_TODAY_SALES = "http://192.168.0.104:4488/BitStore/GetTodaySale";
    public final static String BITSTORE_GET_TOTAL_REVENUE_FOR_RANGE = "http://192.168.0.104:4488/BitStore/GetTotalRevenue";
    public final static String BITSTORE_GET_TOTAL_REVENUE_FOR_STAFF = "http://192.168.0.104:4488/BitStore/GetTotalRevenueForStaff";
    public final static String BITSTORE_GET_SUM_OF_TOTAL_REVENUE_FOR_STAFF_BETWEEN_RANGE = "http://192.168.0.104:4488/BitStore/GetTotalRevenueSumForStaff";
    public final static String BITSTORE_GET_SUM_OF_TOTAL_REVENUE_FOR_PRODUCT_BETWEEN_RANGE = "http://192.168.0.104:4488/BitStore/ProductTotalRevenue";
    public final static String BITSTORE_GET_STAFF_IDS = "http://192.168.0.104:4488/BitStore/GetStaffIds";
    public final static String BITSTORE_GET_PRODUCTNAMES = "http://192.168.0.104:4488/BitStore/GetProductNames";
    public final static String BITSTORE_GET_OUTOFSTOCK_ITEMS = "http://192.168.0.104:4488/BitStore/GetOutOfStockItems";

    //Staff Transaction
    public final static String BITSTORE_GET_TODAY_SALES_FORSTAFF = "http://192.168.0.104:4488/";      ///

    //Cart
    public final static String BITSTORE_ADD_TO_CART = "http://192.168.0.104:4488/BitStore/InsertIntoCart";
    public final static String BITSTORE_GET_CART_ITEMS = "http://192.168.0.104:4488/BitStore/ItemsInCart";

}
