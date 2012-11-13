/*******************************************************************************
 * Copyright 2012 Geoscience Australia
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
package au.gov.ga.earthsci.catalog.part;

import java.net.URL;
import java.util.WeakHashMap;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.ga.earthsci.core.model.catalog.ICatalogTreeNode;
import au.gov.ga.earthsci.core.model.catalog.dataset.DatasetCatalogTreeNode;

/**
 * An {@link ICatalogTreeNodeControlProvider} that supports {@link DatasetCatalogTreeNode}s
 * 
 * @author James Navin (james.navin@ga.gov.au)
 */
public class DatasetCatalogTreeNodeControlProvider implements ICatalogTreeNodeControlProvider
{

	private static final Logger logger = LoggerFactory.getLogger(DatasetCatalogTreeNodeControlProvider.class);

	private final WeakHashMap<URL, Image> imageCache = new WeakHashMap<URL, Image>();
	
	@Override
	public boolean supports(ICatalogTreeNode node)
	{
		return node instanceof DatasetCatalogTreeNode;
	}

	@Override
	public Image getIcon(ICatalogTreeNode node)
	{
		DatasetCatalogTreeNode datasetNode = (DatasetCatalogTreeNode)node;
		
		if (datasetNode.getIconURL() == null)
		{
			return null;
		}
		
		return getImage(datasetNode.getIconURL());
	}

	@Override
	public String getLabel(ICatalogTreeNode node)
	{
		return node.getLabelOrName();
	}

	@Override
	public URL getInfoURL(ICatalogTreeNode node)
	{
		return ((DatasetCatalogTreeNode)node).getInfoURL();
	}

	@Override
	public void dispose()
	{
		for (Image i : imageCache.values())
		{
			if (i != null && !i.isDisposed())
			{
				i.dispose();
			}
		}
		imageCache.clear();
	}
	
	private Image getImage(URL imageURL)
	{
		Image image = imageCache.get(imageURL);
		if (image != null && !image.isDisposed())
		{
			return image;
		}
		try
		{
			image = new Image(Display.getDefault(), imageURL.openStream());
			imageCache.put(imageURL, image);
			return image;
		}
		catch (Exception e)
		{
			logger.debug("Unable to load icon {}", imageURL, e); //$NON-NLS-1$
			return null;
		}
	}
	
}
