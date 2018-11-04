package net.thegreshams.firebase4j.demo;

import android.util.Log;

import com.vegfreshbox.ecommerce.pojo.AddressPojo;

import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.model.FirebaseResponse;
import net.thegreshams.firebase4j.service.Firebase;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    protected static final Logger LOGGER = Logger.getRootLogger();

    public static void main(String[] args) throws FirebaseException, JsonParseException, JsonMappingException,
            IOException, JacksonUtilityException, JSONException {

        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/user/";

        if (firebase_baseUrl == null || firebase_baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Program-argument 'baseUrl' not found but required");
        }

        // create the firebase
        Firebase firebase = new Firebase(firebase_baseUrl);

        // "DELETE" (the fb4jDemo-root)
        FirebaseResponse response = null;// firebase.delete();

        // "PUT" (test-map into the fb4jDemo-root)

        // createUser("test","name","ab@b.com","12345678");
         isEmailAvailble("ab@b.com");
        // createNewAddress("1988256483", "17,Nethaji Avenue main road", "
        // Nerkundram", "Chennai", "600107");
//		Product p1 = new Product();
//		p1.setProductId("1");
//		p1.setProductName("Chilli");
//		p1.setProductPrice(7);
//		p1.setProductQuantity(2);
//		p1.setSubTotal(p1.getProductQuantity() * p1.getProductPrice());
//
//		Product p2 = new Product();
//		p2.setProductId("2");
//		p2.setProductName("Carrot");
//		p2.setProductPrice(70);
//		p2.setProductQuantity(2);
//		p2.setSubTotal(p2.getProductQuantity() * p2.getProductPrice());
//
//		List<Product> productList = new ArrayList<Product>();
//		productList.add(p1);
//		productList.add(p2);

        //createNewOrder("1988256483", "120", productList, "17,Nethaji Avenue main road Nerkundram Chennai 600107");

        //System.out.println(getUserById("1988256483"));

        isCredentialValid("ganesan186@gmail.com", "1234");
        // firebase.addQuery("orderBy", "\"email\"");
        // firebase.addQuery("equalTo", "\"a1@b.com\"");
        //
        // // "GET" (the fb4jDemo-root)
        // response = firebase.get();
        // System.out.println("\n\nResult of GET:\n" + response);
        System.out.println("\n");

    }

    public static FirebaseResponse createUser(String firstName, String email, String password, String token) {

        FirebaseResponse response = null;
        String generatedString = getCurrentTime();
        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/users/" + generatedString;
        Firebase firebase;
        try {

            firebase = new Firebase(firebase_baseUrl);
            Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
            dataMap.put("uid", generatedString);
            dataMap.put("firstName", firstName);
            dataMap.put("email", email);
            dataMap.put("password", password);
            dataMap.put("token", token);
            response = firebase.put(dataMap);

        } catch (FirebaseException | UnsupportedEncodingException | JacksonUtilityException e) {
            e.printStackTrace();
            Log.e("Registration", e.getMessage());
            LOGGER.error(e);
        }

        Log.e("Registration", ("\n\nResult of Create User :\n" + response));
        return response;
    }

    public static boolean isEmailAvailble(String email) {

        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/users/";
        Firebase firebase;
        FirebaseResponse response = null;

        try {
            firebase = new Firebase(firebase_baseUrl);
            firebase.addQuery("orderBy", "\"email\"");
            firebase.addQuery("equalTo", "\"" + email + "\"");

            response = firebase.get();

            if (response.getRawBody() != null) {

                JSONObject resp = new JSONObject(response.getRawBody());
                Iterator<String> keys = resp.keys();

                while (keys.hasNext()) {
                    //System.out.println(keys.next());
                    JSONObject user = (JSONObject) resp.get(keys.next());
                    System.out.println(user.get("email"));
                    if (email.equals(user.get("email"))) {
                        return true;
                    }
                }
            }

        } catch (FirebaseException | UnsupportedEncodingException | JSONException e) {
            LOGGER.error(e);
            return false;
        }

        System.out.println("\n\nResult of isEmailAvailble :\n" + response);
        return false;

    }

    public static String isCredentialValid(String email, String password) {

        Log.e("Login ", "email=" + email + " Passord=" + password);
        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/users/";
        String userId = null;
        FirebaseResponse response = null;

        try {
            Firebase firebase = new Firebase(firebase_baseUrl);
            firebase.addQuery("orderBy", "\"email\"");
            firebase.addQuery("equalTo", "\"" + email + "\"");

            response = firebase.get();
        //    Log.e("Login call code ", "" + response.getCode());
            if (response.getRawBody() != null) {

                JSONObject resp = new JSONObject(response.getRawBody());
                // System.out.println(resp.toString());

                Iterator<String> keys = resp.keys();
                while (keys.hasNext()) {

                    userId = keys.next();
                    Log.e("Login", "LoginId=="+userId);

                    JSONObject user = (JSONObject) resp.get(userId);
                    System.out.println(user.get("email"));
                    if (email.equals(user.get("email")) && password.equals(user.get("password"))) {
                        return userId;
                    } else {
                        userId = null;
                    }
                }
            }

        } catch (FirebaseException | UnsupportedEncodingException | JSONException e) {
            Log.e("Login call error ", userId,e);
            return userId;
        }
        Log.e("Login call response=", userId+"\n\nResult of Check User credentilas:\n" + response);
        return userId;

    }

    static public boolean createNewAddress(String userId, String fullName, String mobileNumber, String address1, String address2, String city, String pincode) {

        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/users/" + userId + "/address/"
                + getAddressId();
        Firebase firebase;
        FirebaseResponse response = null;
        try {
            firebase = new Firebase(firebase_baseUrl);

            Map<String, Object> addressMap = new LinkedHashMap<String, Object>();
            addressMap.put("address1", address1);
            addressMap.put("address2", address2);
            addressMap.put("fullName", fullName);
            addressMap.put("mobileNumber", mobileNumber);
            addressMap.put("city", city);
            addressMap.put("pincode", pincode);
            addressMap.put("createDate", getCurrentDate());
            response = firebase.patch(addressMap);

            response = firebase.get();

        } catch (FirebaseException | UnsupportedEncodingException | JacksonUtilityException e) {
            LOGGER.error(e);
            return false;
        }

        System.out.println("\n\nResult of new address\n" + response);
        return true;

    }

    static public FirebaseResponse getUserById(String userId) {

        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/users/" + userId;
        Firebase firebase;
        FirebaseResponse response = null;
        try {
            firebase = new Firebase(firebase_baseUrl);

            response = firebase.get();

            JSONObject resp = new JSONObject(response.getRawBody());

            if (resp.has("address")) {
                JSONObject address = (JSONObject) resp.get("address");
                Iterator<String> keys = address.keys();

                while (keys.hasNext()) {
                    //System.out.println(keys.next());
                    JSONObject addr = (JSONObject) address.get(keys.next());
                    System.out.println("address1=" + addr.getString("address1"));
                    System.out.println("address2=" + addr.getString("address2"));
                    System.out.println("city=" + addr.getString("city"));
                    System.out.println("pincode=" + addr.getString("pincode"));

                }
            }

        } catch (FirebaseException | UnsupportedEncodingException | JSONException e) {
            LOGGER.error(e);
        }

        System.out.println("\n\nResult of get user details\n" + response);
        return response;

    }

    private static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        return formatter.format(date);
    }

    private static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    private static String getAddressId() {
        SimpleDateFormat formatter = new SimpleDateFormat("mmss");
        Date date = new Date();
        return "1" + formatter.format(date);
    }

    private static String getOrderId() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return "1" + formatter.format(date);
    }

    private static String getWeek() {
        Date d1 = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(d1);
        return "Week" + cl.get(Calendar.WEEK_OF_YEAR);
    }

    public static Order createUserOrder(String userId, String orderAmt, List<Product> products,
                                        String deliveryAddress) {
        Order order = null;

        String date = getCurrentDate();
        String orderId = getAddressId();

        FirebaseResponse response = null;
        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/users/" + userId + "/orders/"
                + getAddressId();
        Firebase firebase;
        try {
            firebase = new Firebase(firebase_baseUrl);

            Map<String, Object> orderMap = new LinkedHashMap<String, Object>();
            orderMap.put("orderAmt", orderAmt);
            orderMap.put("deliveryAddress", deliveryAddress);
            orderMap.put("products", products);
            orderMap.put("orderDate", date);
            orderMap.put("status", "Placed");

            response = firebase.patch(orderMap);


            response = firebase.get();

            order = new Order();

            order.setOrderDate(date);
            order.setOrderId(orderId);
            order.setOrderTotal(orderAmt);

            System.out.println("\n\nResult of get order details\n" + response);
        } catch (FirebaseException | UnsupportedEncodingException | JacksonUtilityException e) {
            LOGGER.error(e);
            Log.e("Error", e.getMessage(), e);

        }

        System.out.println("\n\nResult of orders\n" + response);
        return order;

    }


    public static String getUserOrder(String userId) {
        String orders = null;
        //System.out.println("getUserOrder\n" + userId);
        FirebaseResponse response = null;
        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/orders/" + userId;
        Firebase firebase;
        try {
            firebase = new Firebase(firebase_baseUrl);
            response = firebase.get();
            orders=response.getRawBody();
           // Log.e("getorderdetails" ,response.getRawBody());
        } catch (FirebaseException | UnsupportedEncodingException e) {
            LOGGER.error(e);
            Log.e("Error", e.getMessage(), e);

        }

     //  System.out.println("\n\nResult of orders\n" + response);
        return orders;

    }


    public static Order createNewOrder(String uId, String orderAmt, List<Product> products,
                                       AddressPojo deliveryAddress,String email) {
        String date = getCurrentDate();
        Order order = null;
        FirebaseResponse response = null;
        String orderId =getOrderId();
        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/orders/" + uId + "/" + orderId;
        Firebase firebase;
        try {
            firebase = new Firebase(firebase_baseUrl);

            Map<String, Object> orderMap = new LinkedHashMap<String, Object>();

            orderMap.put("orderId", orderId);
            orderMap.put("orderAmt", orderAmt);
            orderMap.put("deliveryAddress", deliveryAddress);
            orderMap.put("orderDate", date);
            orderMap.put("products", products);
            orderMap.put("status", "InProcess");
            orderMap.put("email", email);


            response = firebase.patch(orderMap);
            order = new Order();

            order.setOrderDate(date);
            order.setOrderId(orderId);
            order.setOrderTotal(orderAmt);
            order.setProducts(products);
            order.setDeliveryAddress(deliveryAddress.toString());

            System.out.println("\n\nResult of get order details\n" + response);
        } catch (FirebaseException | UnsupportedEncodingException | JacksonUtilityException e) {
            LOGGER.error(e);
            Log.e("Error", e.getMessage(), e);

        }

        System.out.println("\n\nResult of orders\n" + response);
        return order;

    }


    static public FirebaseResponse getCategory() {

        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/catalog";
        Firebase firebase;
        FirebaseResponse response = null;
        try {
            firebase = new Firebase(firebase_baseUrl);

            response = firebase.get();

            JSONObject resp = new JSONObject(response.getRawBody());


        } catch (FirebaseException | UnsupportedEncodingException | JSONException e) {
            LOGGER.error(e);
        }

        System.out.println("\n\nResult of category\n" + response);
        return response;

    }


    public static String getEmailAddress(String userData) {
        try {
            if (userData != null) {
                JSONObject user = new JSONObject(userData);
                Log.e("", "has email===" + user.has("email"));
                if (user.has("email")) {
                    userData = (String) user.get("email");
                } else {
                    userData = "anandkumarreddy@gmail.com";
                }
            }
        } catch (JSONException e) {
            Log.e("Error while ", "", e);
        }
        Log.d("Result of email address" , userData);
        return userData;

    }


    static public Map<String, Object> getConfig() {

        String firebase_baseUrl = "https://freshvegbox-735a1.firebaseio.com/config";
        Firebase firebase;
        FirebaseResponse response = null;
        Map<String, Object> configMap = new HashMap<>();
        try {
            firebase = new Firebase(firebase_baseUrl);
            response = firebase.get();
            JSONObject config = new JSONObject(response.getRawBody());

            if (config != null && config.has("gmailEmail")) {
                configMap.put("gmailEmail",config.get("gmailEmail"));
            }
            if (config != null && config.has("gmailPassword")) {
                configMap.put("gmailPassword", config.get("gmailPassword"));
            }
            if (config != null && config.has("cansendmail")) {
                configMap.put("cansendmail",config.get("cansendmail"));
            }
            if (config != null && config.has("cancreateorder")) {
                configMap.put("cancreateorder",config.get("cancreateorder"));
            }
        } catch (FirebaseException | UnsupportedEncodingException | JSONException e) {
            LOGGER.error(e);
        }
        Log.e("\n\nResult of Config\n" , response.getRawBody());
        return configMap;

    }

}
