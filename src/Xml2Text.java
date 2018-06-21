import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Xml2Text extends DefaultHandler{

	boolean isText = false;
	int isPicture = 0;
	int notClosedTdCount = 0;
	String textOnly = "";
	boolean needPicture = false;
	
	public String getText(){
		if(textOnly.endsWith(" ")&&textOnly.length()>1){
			textOnly = textOnly.substring(0, textOnly.length()-1);
		}
		return textOnly;
	}
	
	public void needPicture(){
		needPicture = true;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.trim().toLowerCase().equals("td")){
			notClosedTdCount++;
		}
		if(qName.trim().toLowerCase().equals("text")){
			isText = true;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.trim().toLowerCase().equals("td")){
			textOnly += " ";
			notClosedTdCount--;
		}
		if(qName.trim().toLowerCase().equals("text")){
			isText = false;
		}
	}
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}
	
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
//		System.out.println("value:\t"+new String(ch,start,length));
		if(isText){
			textOnly += new String(ch,start,length);
			if(notClosedTdCount==0){
				textOnly += "";
			}
		}
	}
	
	/**
	 * 不带图片的text
	 * @param xml
	 * @return
	 */
	public static String getTextOnly(String xml){
		SAXParserFactory saxfac = SAXParserFactory.newInstance();
		Xml2Text xto = new Xml2Text();
		try {
			InputStream in = new ByteArrayInputStream(xml.getBytes("utf-8"));
			SAXParser sax = saxfac.newSAXParser();
			sax.parse(in, xto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xto.getText();
	}	
}
