package edu.ucsd.troll.app;

/**
 * Created by shalomabitan on 5/22/14.
 */

import java.util.List;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import java.util.HashMap;
import org.apache.http.message.BasicNameValuePair;


public class APILoginHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    APIServiceHandler sh = new APIServiceHandler();


    public String makeLoginCall(String url, int method,
                                List<NameValuePair> params){

        return sh.makeServiceCall(url, method, params);

    }

    public String checkLoginCall(String url, int method,
                                List<NameValuePair> params){

        return sh.makeServiceCall(url, method, params);

    }




}
