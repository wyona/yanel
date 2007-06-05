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
package org.wyona.yanel.core.workflow;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wyona.yanel.core.workflow.impl.TransitionImpl;
import org.wyona.yanel.core.workflow.impl.WorkflowImpl;

public class WorkflowBuilder {

    public Workflow buildWorkflow(InputStream stream) throws WorkflowException {
        try {
            Document document = readDocument(stream);
            Workflow workflow = buildWorkflow(document);
            return workflow;
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    private Document readDocument(InputStream stream) throws Exception {
        DocumentBuilder builder = createBuilder();
        return builder.parse(stream);
    }

    /**
     * Creates a non-validating and namespace-aware DocumentBuilder.
     * @return A new DocumentBuilder object.
     * @throws ParserConfigurationException if an error occurs
     */
    protected DocumentBuilder createBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        CatalogResolver cr = new CatalogResolver();
        builder.setEntityResolver(cr);
        return builder;
    }

    /**
     * Builds a workflow object from an XML document.
     * @param document The XML document.
     * @return A workflow implementation.
     * @throws WorkflowException when something went wrong.
     */
    protected Workflow buildWorkflow(Document document) throws WorkflowException {

        WorkflowImpl workflow = new WorkflowImpl();

        Element root = document.getDocumentElement();
        String initialState = null;

        // load states
        Element statesElement = (Element) root.getElementsByTagNameNS(Workflow.NAMESPACE, "states")
                .item(0);
        NodeList stateElements = statesElement.getElementsByTagNameNS(Workflow.NAMESPACE, "state");

        ArrayList states = new ArrayList();
        for (int i = 0; i < stateElements.getLength(); i++) {
            Element element = (Element) stateElements.item(i);
            String state = buildState(element);

            if (isInitialStateElement(element)) {
                initialState = state;
            }
            states.add(state);
        }

        workflow.setStates((String[]) states.toArray(new String[states.size()]));
        workflow.setInitialState(initialState);

        // load transitions
        Element transitionsElement = (Element) root.getElementsByTagNameNS(Workflow.NAMESPACE,
                "transitions").item(0);
        NodeList transitionElements = transitionsElement.getElementsByTagNameNS(Workflow.NAMESPACE,
                "transition");

        ArrayList transitions = new ArrayList();
        for (int i = 0; i < transitionElements.getLength(); i++) {
            Transition transition = buildTransition((Element) transitionElements.item(i));
            transitions.add(transition);
        }

        workflow.setTransitions((Transition[]) transitions.toArray(new Transition[transitions
                .size()]));

        return workflow;
    }

    /**
     * Checks if a state element contains the initial state.
     * @param element An XML element.
     * @return A boolean value.
     */
    protected boolean isInitialStateElement(Element element) {
        String initialAttribute = element.getAttribute("initial");

        return (initialAttribute != null)
                && (initialAttribute.equals("yes") || initialAttribute.equals("true"));
    }

    /**
     * Builds a state from an XML element.
     * @param element An XML element.
     * @return A state.
     */
    protected String buildState(Element element) {
        return element.getAttribute("id");
    }

    /**
     * Builds a transition from an XML element.
     * @param element An XML element.
     * @return A transition.
     * @throws WorkflowException when something went wrong.
     */
    protected Transition buildTransition(Element element) throws WorkflowException {

        String id = element.getAttribute("id");
        String source = element.getAttribute("from");
        String destination = element.getAttribute("to");

        TransitionImpl transition = new TransitionImpl(id, source, destination);

        NodeList conditionElements = element.getElementsByTagNameNS(Workflow.NAMESPACE,
        "condition");
        
        Condition[] conditions = new Condition[conditionElements.getLength()];
        for (int i = 0; i < conditionElements.getLength(); i++) {
            conditions[i] = buildCondition((Element) conditionElements.item(i));
        }

        transition.setConditions(conditions);
        
        NodeList actionElements = element.getElementsByTagNameNS(Workflow.NAMESPACE,
        "action");
        
        Action[] actions = new Action[actionElements.getLength()];
        for (int i = 0; i < actionElements.getLength(); i++) {
            actions[i] = buildAction((Element) actionElements.item(i));
        }

        transition.setActions(actions);

        NodeList descriptionElements = element.getElementsByTagNameNS(Workflow.NAMESPACE,
                "description");
        for (int i = 0; i < descriptionElements.getLength(); i++) {
            Element desc = (Element)descriptionElements.item(i);
            String language = desc.getAttribute("xml:lang");
            String description = desc.getFirstChild().getNodeValue();
            transition.addDescription(description, language);
        }
        return transition;
    }
    
    protected Condition buildCondition(Element element) throws WorkflowException {
        String className = element.getAttribute("class");
        Class conditionClass;
        Condition condition = null;
        try {
            conditionClass = Class.forName(className);
            condition = (Condition)conditionClass.newInstance();
        } catch (Exception e) {
            throw new WorkflowException(e.getMessage(), e);
        }
        Node node = element.getFirstChild();
        String expression = null;
        if (node != null) {
            expression = node.getNodeValue();
        }
        condition.setExpression(expression);
        return condition;
    }

    protected Action buildAction(Element element) throws WorkflowException {
        String className = element.getAttribute("class");
        Class actionClass;
        Action action = null;
        try {
            actionClass = Class.forName(className);
            action = (Action)actionClass.newInstance();
        } catch (Exception e) {
            throw new WorkflowException(e.getMessage(), e);
        }
        Node node = element.getFirstChild();
        String expression = null;
        if (node != null) {
            expression = node.getNodeValue();
        }
        action.setExpression(expression);
        return action;
    }

}
