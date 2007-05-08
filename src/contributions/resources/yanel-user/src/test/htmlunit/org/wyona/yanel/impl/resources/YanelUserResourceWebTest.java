/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wyona.yanel.impl.resources;

import org.wyona.yanel.htmlunit.AbstractHtmlUnitTest;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;



import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.map.Realm;



public class YanelUserResourceWebTest extends AbstractHtmlUnitTest {
    
    protected Yanel yanel;
    protected Realm realm;
    
    protected void setUp() throws Exception {
	super.setUp();
        this.testName = "Yanel User Resource Web Test";
        
    }

    /**
     * Test successful password update 
     */   
    public void testSuccessfulPasswordUpdate() throws Exception {
        loadHtmlPage("test/use-cases/alice"); 
                
        final HtmlForm form1 = this.currentPage.getFormByName("user-password-form");
        
        final HtmlSubmitInput button1 = (HtmlSubmitInput)form1.getInputByName("submitPassword");
        
        final HtmlPasswordInput oldPasswordField1 = (HtmlPasswordInput)form1.getInputByName("oldPassword");        
        oldPasswordField1.setValueAttribute("alicia");
        
        final HtmlPasswordInput newPasswordField1 = (HtmlPasswordInput)form1.getInputByName("newPassword");        
        newPasswordField1.setValueAttribute("foo");
        
        final HtmlPasswordInput newPasswordConfField1 = (HtmlPasswordInput)form1.getInputByName("newPasswordConfirmation");        
        newPasswordConfField1.setValueAttribute("foo");   
        
        click(button1);         
        
        assertPageContainsText("Password updated successfully");         
        
        
        loadHtmlPage("test/use-cases/alice"); 
        
        final HtmlForm form2 = this.currentPage.getFormByName("user-password-form");
        
        final HtmlSubmitInput button2 = (HtmlSubmitInput)form2.getInputByName("submitPassword");
        
        final HtmlPasswordInput oldPasswordField2 = (HtmlPasswordInput)form2.getInputByName("oldPassword");        
        oldPasswordField2.setValueAttribute("foo");
        
        final HtmlPasswordInput newPasswordField2 = (HtmlPasswordInput)form2.getInputByName("newPassword");        
        newPasswordField2.setValueAttribute("alicia");
        
        final HtmlPasswordInput newPasswordConfField2 = (HtmlPasswordInput)form2.getInputByName("newPasswordConfirmation");        
        newPasswordConfField2.setValueAttribute("alicia");   
        
        click(button2);         
        
        assertPageContainsText("Password updated successfully");              
        
    }   
        
    /**
     * Test unsuccessful password update - Incorrect current password
     */
    public void testAuthenticationInPasswordUpdate() throws Exception {
        loadHtmlPage("test/use-cases/alice"); 
                
        final HtmlForm form1 = this.currentPage.getFormByName("user-password-form");
        
        final HtmlSubmitInput button1 = (HtmlSubmitInput)form1.getInputByName("submitPassword");
        
        final HtmlPasswordInput oldPasswordField1 = (HtmlPasswordInput)form1.getInputByName("oldPassword");        
        oldPasswordField1.setValueAttribute("sugus");
        
        final HtmlPasswordInput newPasswordField1 = (HtmlPasswordInput)form1.getInputByName("newPassword");        
        newPasswordField1.setValueAttribute("foo");
        
        final HtmlPasswordInput newPasswordConfField1 = (HtmlPasswordInput)form1.getInputByName("newPasswordConfirmation");        
        newPasswordConfField1.setValueAttribute("foo");   
        
        click(button1);         
        
        assertPageContainsText("Authentication failed!"); 
    }
        
    /**
     * Test unsuccessful password update - Differences between new password and its confirmation
     */    
    public void testNewPasswordValidationInPasswordUpdate() throws Exception {
        loadHtmlPage("test/use-cases/alice"); 
                
        final HtmlForm form = this.currentPage.getFormByName("user-password-form");
        
        final HtmlSubmitInput button = (HtmlSubmitInput)form.getInputByName("submitPassword");
        
        final HtmlPasswordInput oldPasswordField = (HtmlPasswordInput)form.getInputByName("oldPassword");        
        oldPasswordField.setValueAttribute("levi");
        
        final HtmlPasswordInput newPasswordField = (HtmlPasswordInput)form.getInputByName("newPassword");        
        newPasswordField.setValueAttribute("foo");
        
        final HtmlPasswordInput newPasswordConfField = (HtmlPasswordInput)form.getInputByName("newPasswordConfirmation");        
        newPasswordConfField.setValueAttribute("lala");   
        
        click(button);  
        
        assertPageContainsText("An error occurred");  
    }
        
    /**
     * Test successful user profile update
     */
    public void testSuccessfulProfileUpdate() throws Exception {
        loadHtmlPage("test/use-cases/alice"); 
                
        final HtmlForm form = this.currentPage.getFormByName("user-profile-form");
        
        final HtmlSubmitInput button = (HtmlSubmitInput)form.getInputByName("submitProfile");
        
        final HtmlTextInput userNameField = (HtmlTextInput)form.getInputByName("userName");        
        userNameField.setValueAttribute("Michael Wechner");
        
        final HtmlTextInput newEmailField = (HtmlTextInput)form.getInputByName("email");        
        newEmailField.setValueAttribute("michi@wyona.org");     
        
        click(button);  
        
        assertPageContainsText("Profile updated successfully");  
    }
    
    /**
     * Test unsuccessful user profile update - email not valid
     */
    public void testUnsuccessfulProfileUpdate() throws Exception {
        loadHtmlPage("test/use-cases/alice"); 
                
        final HtmlForm form = this.currentPage.getFormByName("user-profile-form");
        
        final HtmlSubmitInput button = (HtmlSubmitInput)form.getInputByName("submitProfile");
        
        final HtmlTextInput userNameField = (HtmlTextInput)form.getInputByName("userName");        
        userNameField.setValueAttribute("Michael Wechner");
        
        final HtmlTextInput newPasswordField = (HtmlTextInput)form.getInputByName("email");        
        newPasswordField.setValueAttribute("michi");     
        
        click(button);  
        
        assertPageContainsText("An error occurred: emailNotValid");  
    }
    
    /**
     * Test addition and deletion from group
     */ 
/*
    public void testAdditionAndDeletionFromGroup() throws Exception {       
	
	loadHtmlPage("test/use-cases/alice"); 	       
        final HtmlForm form1 = this.currentPage.getFormByName("Group_admin");        
        final HtmlSubmitInput button1 = (HtmlSubmitInput)form1.getInputByName("submitDeleteFromGroup_admin");      
        
        click(button1); 
        
        assertPageContainsText("User successfully deleted from group");         
        
        loadHtmlPage("test/use-cases/alice");        
        final HtmlForm form2 = this.currentPage.getFormByName("add-user-to-group");        
        final HtmlSubmitInput button2 = (HtmlSubmitInput)form2.getInputByName("submitAddToGroup");        
        final HtmlSelect select = (HtmlSelect)form2.getSelectByName("Group");
        final HtmlOption option = (HtmlOption)select.getOptionByValue("admin");
        
        option.click();        
        click(button2);
       
        assertPageContainsText("User successfully added to group");         
    }
*/
    
    
    /**
     * Test deletion from all groups - Users must belong at least to one group
     */ 
    /*
    public void testDeletionFromAllGroupsNotAllowed() throws Exception {
	loadHtmlPage("test/use-cases/alice"); 	       
        final HtmlForm form1 = this.currentPage.getFormByName("Group_admin");        
        final HtmlSubmitInput button1 = (HtmlSubmitInput)form1.getInputByName("submitDeleteFromGroup_admin");      
        
        click(button1); 
        
        loadHtmlPage("test/use-cases/alice"); 	       
        final HtmlForm form2 = this.currentPage.getFormByName("Group_editor");        
        final HtmlSubmitInput button2 = (HtmlSubmitInput)form2.getInputByName("submitDeleteFromGroup_editor");      
        
        click(button2); 
        
        assertPageContainsText("An error occurred: User can not be removed from group");
        
        loadHtmlPage("test/use-cases/alice");        
        final HtmlForm form3 = this.currentPage.getFormByName("add-user-to-group");        
        final HtmlSubmitInput button3 = (HtmlSubmitInput)form2.getInputByName("submitAddToGroup");        
        final HtmlSelect select = (HtmlSelect)form3.getSelectByName("Group");
        final HtmlOption option = (HtmlOption)select.getOptionByValue("admin");
        
        option.click();        
        click(button3);	
    }
    */
        

}
