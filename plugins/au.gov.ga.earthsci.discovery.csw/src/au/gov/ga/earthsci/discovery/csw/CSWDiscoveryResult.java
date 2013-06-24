/*******************************************************************************
 * Copyright 2013 Geoscience Australia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package au.gov.ga.earthsci.discovery.csw;

import gov.nasa.worldwind.util.WWXML;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import au.gov.ga.earthsci.discovery.IDiscoveryResult;

/**
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 * 
 */
public class CSWDiscoveryResult implements IDiscoveryResult
{
	/*
	FULL RESPONSE:
	<?xml version="1.0" encoding="UTF-8" standalone="no"?>
	<csw:GetRecordsResponse xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcmiBox="http://dublincore.org/documents/2000/07/11/dcmi-box/" xmlns:dct="http://purl.org/dc/terms/" xmlns:gml="http://www.opengis.net/gml" xmlns:ows="http://www.opengis.net/ows" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		<csw:SearchStatus timestamp="2013-05-20T09:48:33+10:00"/>
		<csw:SearchResults elementSet="full" nextRecord="11" numberOfRecordsMatched="63" numberOfRecordsReturned="10" recordSchema="http://www.opengis.net/cat/csw/2.0.2">
			<csw:Record>
				...
			</csw:Record>
		</csw:SearchResults>
	</csw:GetRecordsResponse>
	
	INDIVIDUAL RECORD:
	<csw:Record>
		<dc:identifier scheme="urn:x-esri:specification:ServiceType:ArcIMS:Metadata:FileID">{1E1BA4EA-E8FB-47FD-9551-A045A550A478}</dc:identifier>
		<dc:identifier scheme="urn:x-esri:specification:ServiceType:ArcIMS:Metadata:DocID">{F58AC930-A27F-45BB-A46C-F38551AE9A65}</dc:identifier>
		<dc:title>Surface Geology of Australia WMS</dc:title>
		<dc:type scheme="urn:x-esri:specification:ServiceType:ArcIMS:Metadata:ContentType">liveData</dc:type>
		<dc:subject>OneGeology</dc:subject>
		<dc:subject>Australia</dc:subject>
		<dc:subject>geology</dc:subject>
		<dc:subject>bedrock</dc:subject>
		<dc:subject>surficial</dc:subject>
		<dc:subject>lithology</dc:subject>
		<dc:subject>lithostratigraphy</dc:subject>
		<dc:subject>age</dc:subject>
		<dc:subject>contact</dc:subject>
		<dc:subject>fault</dc:subject>
		<dc:subject>shear</dc:subject>
		<dct:modified>2013-01-18T11:03:53+11:00</dct:modified>
		<dct:abstract>The Surface Geology of Australia Web Map Service provides two seamless national coverages of bedrock and surficial geology, compiled for use at 1:1 million scale and 1:2.5 million scale. The data sets were released in 2012. The 1:1M data is limited so that it does not display at scales less than 1:1,500,000. The 1:2.5M data is limited so that it does not display at scales greater than 1:1,500,000. The data represents outcropping or near-outcropping bedrock units, and unconsolidated or poorly consolidated regolith material covering bedrock. Geological units are represented as polygon and line geometries, and are attributed with information regarding stratigraphic name and hierarchy, age, lithology, and primary data source. Layers are available for geological units coloured by lithostratigraphy, age, and lithology. The dataset also contains geological contacts, structural features such as faults and shears, and miscellaneous supporting lines like the boundaries of water and ice bodies. Copyright Commonwealth of Australia (Geoscience Australia) 2012.  This material is released free under the Creative Commons Attribution 3.0 Australia Licence - http://creativecommons.org/licenses/by/3.0/au/</dct:abstract>
		<dct:references scheme="urn:x-esri:specification:ServiceType:ArcIMS:Metadata:Server">http://www.ga.gov.au/gis/services/earth_science/GA_Surface_Geology_of_Australia/MapServer/WMSServer</dct:references>
		<dct:references scheme="urn:x-esri:specification:ServiceType:ArcIMS:Metadata:Document">http://www.ga.gov.au/geoportal/csw?getxml=%7BF58AC930-A27F-45BB-A46C-F38551AE9A65%7D</dct:references>
		<ows:WGS84BoundingBox>
			<ows:LowerCorner>72.0 -55.0</ows:LowerCorner>
			<ows:UpperCorner>168.0 -9.0</ows:UpperCorner>
		</ows:WGS84BoundingBox>
		<ows:BoundingBox>
			<ows:LowerCorner>72.0 -55.0</ows:LowerCorner>
			<ows:UpperCorner>168.0 -9.0</ows:UpperCorner>
		</ows:BoundingBox>
	</csw:Record>
	 */

	private final int index;
	private final String title;
	private final String description;
	private final URL endpoint;

	public CSWDiscoveryResult(int index, Element cswRecordElement) throws XPathExpressionException
	{
		this.index = index;

		XPath xpath = WWXML.makeXPath();

		String title = (String) xpath.compile("title/text()").evaluate(cswRecordElement, XPathConstants.STRING); //$NON-NLS-1$
		title = StringEscapeUtils.unescapeXml(title);

		String description =
				(String) xpath.compile("abstract/text()").evaluate(cswRecordElement, XPathConstants.STRING); //$NON-NLS-1$
		description = StringEscapeUtils.unescapeXml(description);

		//normalize newlines
		description = description.replace("\r\n", "\n").replace("\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		this.title = title;
		this.description = description;


		URL endpoint = null;
		NodeList references = (NodeList) xpath.compile("references").evaluate(cswRecordElement, XPathConstants.NODESET); //$NON-NLS-1$
		for (int i = 0; i < references.getLength(); i++)
		{
			Element reference = (Element) references.item(i);
			if (reference.getAttribute("scheme").contains("Server")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				try
				{
					endpoint = new URL(reference.getTextContent());
					break;
				}
				catch (MalformedURLException e)
				{
				}
			}
		}

		this.endpoint = endpoint;
	}

	@Override
	public int getIndex()
	{
		return index;
	}

	@Override
	public URI getContentURI() throws URISyntaxException
	{
		String urlString = endpoint.toString();
		int queryStart = urlString.indexOf('?');
		if (queryStart >= 0)
		{
			urlString = urlString.substring(0, queryStart);
		}
		urlString += "?request=GetCapabilities"; //$NON-NLS-1$
		return new URI(urlString);
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	@Override
	public String toString()
	{
		return getIndex() + ": " + getTitle(); //$NON-NLS-1$
	}
}