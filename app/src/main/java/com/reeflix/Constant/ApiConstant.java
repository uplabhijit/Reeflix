package com.reeflix.Constant;

/*Api used in the app*/
public class ApiConstant {

    /*base url*/
    private final static String reeflix_api_prefix = "https://api.viseee.com/";

    public final static String video_dashboard_api = reeflix_api_prefix + "categories/getAll";
    public final static String video_view_all_api = reeflix_api_prefix + "categories/getByid?id=";

    public final static String api_imageupload_url = reeflix_api_prefix + "image/upload";
    public final static String getimage_url = reeflix_api_prefix + "image/download?id=";

    public final static String api_registration_url = reeflix_api_prefix + "user/create";
    public final static String api_login_url = reeflix_api_prefix + "user/login";
    public final static String api_updateuser_url = reeflix_api_prefix + "user/update?id=";
    public final static String api_verifyemail_url = reeflix_api_prefix + "user/forgotpasswordotp";
    public final static String api_verifyotp_url = reeflix_api_prefix + "user/verifyotp";
    public final static String api_resetpassword_url = reeflix_api_prefix + "user/passwordChange";
    public final static String api_verifyotpduringregistration_url = reeflix_api_prefix + "user/accountactivation";
    public final static String search_api = reeflix_api_prefix + "content/textsearch?text=";
    public final static String relatedmovie_search_api = reeflix_api_prefix+"content/relatedsearch?id=";
}