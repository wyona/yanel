package org.wyona.yanel.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Helper class for developing configurable components. There are cases when the module has to be
 * included into the page in different places. Since GWT obfuscates all the methods, one cannot create new
 * instances of modules. Rather EntryPoint should be extended and the same module can be added 
 * to a different root panel.
 * <p>
 * The components must be configured as:<br/>
 * <pre>
 * Yanel.[ComponentNamespace] = {configurations : new Array()};
 * Yanel.[ComponentNamespace].configurations[x] = {prop1:value1, prop2:value2...};
 * </pre>
 * <p>
 * &lt;ComponentNamespace> coincides with the EntryPoint subtype name.
 * */
//TODO: avoid duplicated code in overloaded methods. Better call an overload. 
public abstract class ConfigurableComponentsAware{
	private RootPanel [] panels = null;
	
	protected ConfigurableComponentsAware(){}
	
	protected String getComponentNamespace(){
		String [] typeComponents = GWT.getTypeName(this).split("\\.");
		return typeComponents[typeComponents.length-1];
	}
	
	/**
	 * @return the first available parameter value
	 * */
	protected final native String getConfiguration(String parameterName)/*-{
		var ns = $wnd.Yanel[this.@org.wyona.yanel.gwt.client.ConfigurableComponentsAware::getComponentNamespace()()];
		
		if($wnd.Yanel.isArray(ns.configurations)){
			return ns.configurations[0][parameterName];
		}
    	return ns.configurations[parameterName]; 
	}-*/;
	
	/**
	 * Executes a parameterless function specified in the configuration, e.g.
	 * <br>
	 * Yanel.MyComponent.configurations[0] = {id:"myid", myfun: function(){window.alert('Hello');}};
	 * */
	protected final native void executeConfigurationFunction(String parameterName)/*-{
		var ns = $wnd.Yanel[this.@org.wyona.yanel.gwt.client.ConfigurableComponentsAware::getComponentNamespace()()];
		
		if($wnd.Yanel.isArray(ns.configurations)){
			ns.configurations[0][parameterName]();
		}else{
			ns.configurations[parameterName]();
		}
	}-*/;
	
	protected final native void executeConfigurationFunction(String parameterName, int index)/*-{
		var ns = $wnd.Yanel[this.@org.wyona.yanel.gwt.client.ConfigurableComponentsAware::getComponentNamespace()()];
		
		if($wnd.Yanel.isArray(ns.configurations)){
			ns.configurations[index][parameterName]();
		}else{
			ns.configurations[parameterName]();
		}
	}-*/;
	
	/**
	 * Executes a function with a parameter specified in the configuration, e.g.
	 * <br>
	 * Yanel.MyComponent.configurations[0] = {id:"myid", onMyEvent: function(param){window.alert(param);}};
	 * */
	protected final native void executeConfigurationFunction(String parameterName, Object functionParameter)/*-{
		var ns = $wnd.Yanel[this.@org.wyona.yanel.gwt.client.ConfigurableComponentsAware::getComponentNamespace()()];
		
		if($wnd.Yanel.isArray(ns.configurations)){
			ns.configurations[0][parameterName](functionParameter);
		}else{
			ns.configurations[parameterName](functionParameter);
		}
	}-*/;
	
	protected final native void executeConfigurationFunction(String parameterName, Object functionParameter, int index)/*-{
		var ns = $wnd.Yanel[this.@org.wyona.yanel.gwt.client.ConfigurableComponentsAware::getComponentNamespace()()];
		
		if($wnd.Yanel.isArray(ns.configurations)){
			ns.configurations[index][parameterName](functionParameter);
		}else{
			ns.configurations[parameterName](functionParameter);
		}
	}-*/;

	/**
	 * @return the parameter value available at the specified index
	 * */
	protected final native String getConfiguration(String parameterName, int index)/*-{
		var ns = $wnd.Yanel[this.@org.wyona.yanel.gwt.client.ConfigurableComponentsAware::getComponentNamespace()()];
		if($wnd.Yanel.isArray(ns.configurations)){
			return ns.configurations[index][parameterName];
		}
    	return ns.configurations[parameterName]; 
	}-*/;
	
	/**
	 * Detects how many components are there configured
	 * */
	protected final native int getConfigurationSize()/*-{
		var ns = $wnd.Yanel[this.@org.wyona.yanel.gwt.client.ConfigurableComponentsAware::getComponentNamespace()()];
		if($wnd.Yanel.isArray(ns.configurations)){
			return ns.configurations.length;
		}
		return 1;
	}-*/;
	
	/**
	 * A component always reside within a RootPanel (an element with a specific id).
	 * 
	 * @return the parameter name where the ids of root panels are specified. Default is 'id'
	 * */
	protected String getComponentHolderIdentifyingParameter(){
		return "id";
	}
	
	/**
	 * Looks for configured panels.
	 * @return root panels as specified in the configuration. Note that some root panels may be <code>null</code> when it is not found
	 * */
	public final RootPanel [] getRootPanels(){
		if(panels == null){
			panels = new RootPanel[getConfigurationSize()];
			for (int i = 0; i < panels.length; i++) {
	    		panels[i] = RootPanel.get(getConfiguration(getComponentHolderIdentifyingParameter(), i));
	    		
			}
		}
		return panels;
	}
	
	/**
	 * Gets an iterator for all the configured root panels. Iterator.next() will never return <code>null</code>
	 * */
	public final Iterator/*<RootPanel>*/ getRootPanelIterator(){
		RootPanel [] panels = getRootPanels();
		List/*<RootPanel>*/ listOfRootPanels = new ArrayList/*<RootPanel>*/();
		
		for (int i = 0; i < panels.length; i++) {
			if(panels[i] != null){
				listOfRootPanels.add(panels[i]);
			}
		}
		
		return listOfRootPanels.iterator();
	}
	
	/**
	 * Implement and call this method in order to create and configure component instances 
	 * */
	protected abstract void initializeInstances();
}
