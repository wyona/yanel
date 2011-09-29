package org.wyona.yanel.gwt.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;

/**
 * Asynchronous agents are calling some URL in an asynchronous way. 
 * See http://code.google.com/p/bunsenandbeaker/wiki/DevGuideHttpRequests
 */
public abstract class AsynchronousAgent implements RequestCallback{
    private RequestBuilder requestBuilder = null;
    private String url = null;
    private Map/*<String, String>*/ parameters = new HashMap/*<String, String>*/();
	
    /**
     * Default request using GET method and application/x-www-form-urlencoded content type within header
     */
    public AsynchronousAgent(String url){
        this.url = url;
        requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        initializeHeaders();
    }
	
    /**
     *
     */
    public AsynchronousAgent(String url, RequestBuilder.Method method){
        this.url = url;
        requestBuilder = new RequestBuilder(method, url);
        initializeHeaders();
    }
	
    /**
     * If no request headers have been set, the header "Content-Type" will be used with a value of "text/plain; charset=utf-8"
     */
    protected void initializeHeaders(){
        requestBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded");
    }

	/**
	 * Sending the parameter depends on a content type and the receiving party
	 * */
	protected String buildRequestData(){
		String data = "";
		
		Map copyOfParameters = new HashMap(parameters);
		for (Iterator i = copyOfParameters.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			
			data += entry.getKey() +""+ entry.getValue();
			
			if(i.hasNext()){
				data += "&";
			}
		}
		
		return data;
	}

    /**
     * Send request
     */
    public final Request execute() throws RequestException{
        //Window.alert("DEBUG: Send request: " + url);
        return requestBuilder.sendRequest(buildRequestData(), this);
    }
	
	public void addParameter(String name, String value){
		parameters.put(name, value);
	} 

    /**
     *
     */
    public void onError(Request request, Throwable exception) {
        Window.alert("Exception: " + exception.getMessage());
        if(!GWT.isScript()){
            exception.printStackTrace();
        }
    }
}
