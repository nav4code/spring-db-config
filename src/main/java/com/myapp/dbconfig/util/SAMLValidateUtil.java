package com.myapp.dbconfig.util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.Instant;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class SAMLValidateUtil {
	
	@PostConstruct
	private void init() {
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String,String> getInfo(String responseMessage)  {
		try {
			
			byte[] base64DecodedResponse = new Base64().decode(responseMessage);
			ByteArrayInputStream is = new ByteArrayInputStream(base64DecodedResponse);
	
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = docBuilder.parse(is);
			Element element = document.getDocumentElement();
			Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory()
					.getUnmarshaller(document.getDocumentElement());
			XMLObject responseXmlObj = unmarshaller.unmarshall(element);
			Response response = (Response) responseXmlObj;
			System.out.println(response.getVersion());
			SignableSAMLObject samlToken = (SignableSAMLObject) response
					.getAssertions().get(0);

			Instant notBefore = ((org.opensaml.saml2.core.Assertion) samlToken)
					.getConditions().getNotBefore().toInstant();
			Instant notOnOrAfter = ((org.opensaml.saml2.core.Assertion) samlToken)
					.getConditions().getNotOnOrAfter().toInstant();
			
			Map<String,String> claims = isAttributeStatementPresent(samlToken);
			System.out.println(claims.size());
			for(Map.Entry<String, String> e : claims.entrySet() ) {
				System.out.println(e.getKey() + e.getValue());
			}
			return claims;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Map<String, String> isAttributeStatementPresent(
			SignableSAMLObject samlToken) {
		//logger.info("Attribute Values are : ");
		Map<String, String> claims = new HashMap<String, String>();
		try {
			List<AttributeStatement> attributeStatements = ((org.opensaml.saml2.core.Assertion) samlToken)
					.getAttributeStatements();
			for (AttributeStatement attrStatement : attributeStatements) {
				List<Attribute> attributes = attrStatement.getAttributes();
				for (Attribute attribute : attributes) {
					StringBuffer atts = new StringBuffer();
					for (XMLObject xml : attribute.getAttributeValues()) {
						if(atts.length() > 0){
							atts.append(",");
						}
						atts.append(xml.getDOM().getTextContent());
					}
					claims.put(attribute.getName(),atts.toString());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return claims;
	}
	

}

