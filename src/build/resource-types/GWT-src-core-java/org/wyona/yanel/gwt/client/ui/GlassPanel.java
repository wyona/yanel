package org.wyona.yanel.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * This panel is positioned absolutely and covers the whole client area of the browser.
 * It can be used to disable everything underneath it with appropriate z-index.
 * <p>
 * For instance a dialog can attach it to the root panel during opening, and remove it during closing.
 * <p>
 * CSS:<br>
 * .yanel-GlassPanel
 * 
 * <p>
 * Example:<br>
 * Add and remove this widget whenever you want, and style it as follows:
 * <pre>
 * .yanel-GlassPanel{
 *   background-color: black;
 *   filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;
 * }
 * </pre>
 * */
public class GlassPanel extends Composite implements WindowResizeListener{
    public static final String STYLE = "yanel-GlassPanel";
    
    private SimplePanel panel = new SimplePanel();
    
    public GlassPanel(){
        initWidget(panel);
        
        panel.setStylePrimaryName(STYLE);
        
        Window.addWindowResizeListener(this);
        
        resize(Window.getClientWidth(), Window.getClientHeight());
    }
    
    public void onWindowResized(int width, int height) {
        resize(width, height);
        
    }
    
    public void show(){
        // Override the styles explicitly, because it's needed every time the widget is detached
        DOM.setStyleAttribute(panel.getElement(), "left", "0");
        DOM.setStyleAttribute(panel.getElement(), "top", "0");
        DOM.setStyleAttribute(panel.getElement(), "position", "absolute");
        
        RootPanel.get().add(this);
    }
    
    public void hide(){
        RootPanel.get().remove(this);
    }
    
    private void resize(int width, int height){
        panel.setSize(width+"px", height+"px");
    }
}
