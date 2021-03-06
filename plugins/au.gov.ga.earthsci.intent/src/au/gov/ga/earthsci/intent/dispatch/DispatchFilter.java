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
package au.gov.ga.earthsci.intent.dispatch;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import au.gov.ga.earthsci.common.util.ExtensionPointHelper;

/**
 * Filter used to match dispatched objects of certain type(s) to a
 * {@link IDispatchHandler} implementation.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public class DispatchFilter
{
	private Class<? extends IDispatchHandler> handler;
	private final Set<Class<?>> types = new HashSet<Class<?>>();
	private int priority = 0;
	private String name;

	public DispatchFilter()
	{
	}

	@SuppressWarnings("unchecked")
	public DispatchFilter(IConfigurationElement element) throws ClassNotFoundException
	{
		this.handler = (Class<? extends IDispatchHandler>) ExtensionPointHelper.getClassForProperty(element, "class"); //$NON-NLS-1$
		this.name = element.getAttribute("name"); //$NON-NLS-1$
		IConfigurationElement[] types = element.getChildren("type"); //$NON-NLS-1$
		for (IConfigurationElement type : types)
		{
			Class<?> c = ExtensionPointHelper.getClassForProperty(type, "class"); //$NON-NLS-1$
			this.types.add(c);
		}
		try
		{
			priority = Integer.parseInt(element.getAttribute("priority")); //$NON-NLS-1$
		}
		catch (NumberFormatException e)
		{
			//ignore
		}
	}

	/**
	 * @return Class that handles dispatched objects for this filter
	 */
	public Class<? extends IDispatchHandler> getHandler()
	{
		return handler;
	}

	/**
	 * Set the class that handles dispatched objects for this filter.
	 * 
	 * @param handler
	 * @return this
	 */
	public DispatchFilter setHandler(Class<? extends IDispatchHandler> handler)
	{
		this.handler = handler;
		return this;
	}

	/**
	 * @return Classes of dispatched objects that this filter matches
	 */
	public Set<Class<?>> getTypes()
	{
		return types;
	}

	/**
	 * Add a class to the classes of dispatched objects that this filter will
	 * match.
	 * 
	 * @param type
	 * @return this
	 */
	public DispatchFilter addType(Class<?> type)
	{
		types.add(type);
		return this;
	}

	/**
	 * Remove a class from the classes of dispatched objects that this filter
	 * will match.
	 * 
	 * @param type
	 * @return this
	 */
	public DispatchFilter removeType(Class<?> type)
	{
		types.remove(type);
		return this;
	}

	/**
	 * The priority of this filter. If multiple filters match on a dispatched
	 * object's type, the filter with the highest priority will be used.
	 * Defaults to 0.
	 * 
	 * @return Priority of this filter
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 * Set the priority of this filter. If multiple filters match on a
	 * dispatched object's type, the filter with the highest priority will be
	 * used.
	 * 
	 * @param priority
	 * @return this
	 */
	public DispatchFilter setPriority(int priority)
	{
		this.priority = priority;
		return this;
	}

	/**
	 * @return The name of this dispatch filter
	 * @see #setName(String)
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the name of this dispatch filter. This should be a pretty name that
	 * can be displayed to the user to prompt them whether to handle the object
	 * with this dispatch handler.
	 * 
	 * @param name
	 * @return
	 */
	public DispatchFilter setName(String name)
	{
		this.name = name;
		return this;
	}
}
