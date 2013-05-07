/**
 * This file is part of the Eurelis OpenCms Admin Module.
 * 
 * Copyright (c) 2013 Eurelis (http://www.eurelis.com)
 *
 * This module is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this module. 
 * If not, see <http://www.gnu.org/licenses/>
 */

package com.eurelis.tools.xml.transformation.processors;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.eurelis.tools.xml.transformation.Journal;
import com.eurelis.tools.xml.transformation.model.TemplateParameter;
import com.eurelis.tools.xml.transformation.model.TemplateTransformation;


/**
 * The Class TemplateTransformationProcessor.
 */
public class TemplateTransformationProcessor extends Processor {
	
	
	/** The template transformation. */
	private TemplateTransformation templateTransformation = null;
	
	
	/**
	 * Instantiates a new template transformation processor.
	 *
	 * @param journal the journal where to put events
	 * @param unitaryTransformationProcessor the parent unitary transformation processor
	 * @param templateTransformation the template transformation description
	 */
	public TemplateTransformationProcessor(Journal journal, UnitaryTransformationProcessor unitaryTransformationProcessor, TemplateTransformation templateTransformation) {
		super(journal, unitaryTransformationProcessor);
		this.templateTransformation = templateTransformation;
	}

	
	/**
	 * Process the template transformation.
	 *
	 * @param source the source node relative to which look for template parameter values
	 * @return the root element of the xml document created while processing this template transformation
	 */
	@SuppressWarnings("unchecked")
	public Element processTransformation(Node source) {
		
		String result = this.templateTransformation.getTemplate();
		
		for (TemplateParameter tp : templateTransformation.parameters) {
			String name = tp.getName();
			String stringValue = "";
			Object value = tp.getXpath().evaluate(source);
			if (value != null) {
				if (value instanceof List) {
					StringBuilder valueBuilder = new StringBuilder();
					
					for (Node n : (List<Node>)value) {
						valueBuilder.append(n.asXML());
					}
					
					stringValue = valueBuilder.toString();
				}
				else if (value instanceof Node) {
					
					if (value instanceof Attribute) {
						stringValue = ((Attribute)value).getValue();
					}
					else {
						stringValue = ((Node)value).asXML();
					}
				}
				else if (value instanceof String) {
					stringValue = (String)value;
				}
			}
			
			result = result.replace('$' + name, stringValue);
		}
		
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(result);
		} catch (DocumentException e) {
			System.err.println(result);
			e.printStackTrace();
		}
		
		return (doc != null)?doc.getRootElement():null;
	}
	
	
	/* (non-Javadoc)
	 * @see com.eurelis.tools.xml.transformation.processors.Processor#getName()
	 */
	public String getName() {
		return "TemplateTransformation";
	}
	
}