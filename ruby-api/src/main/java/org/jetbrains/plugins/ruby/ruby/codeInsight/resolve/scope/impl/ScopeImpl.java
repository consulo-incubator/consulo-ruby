/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.PseudoScopeHolder;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.Scope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 4, 2007
 */
public class ScopeImpl implements Scope
{
	private HashMap<String, ScopeVariable> myVariables = new HashMap<String, ScopeVariable>();
	private final Object LOCK = new Object();
	private PseudoScopeHolder myHolder;
	private ArrayList<Scope> mySubScopes = new ArrayList<Scope>();

	public ScopeImpl(@Nonnull PseudoScopeHolder holder)
	{
		myHolder = holder;
	}

	@Override
	@Nonnull
	public PseudoScopeHolder getHolder()
	{
		return myHolder;
	}

	@Override
	@Nonnull
	public Collection<Scope> getSubScopes()
	{
		return mySubScopes;
	}

	public void addSubScope(@Nonnull final Scope scope)
	{
		mySubScopes.add(scope);
	}

	@Override
	@Nonnull
	public Collection<ScopeVariable> getVariables()
	{
		synchronized(LOCK)
		{
			return myVariables.values();
		}
	}

	@Override
	public void processIdentifier(@Nonnull final RIdentifier identifier)
	{
		final String name = identifier.getText();
		synchronized(LOCK)
		{
			if(getVariableByName(name) == null)
			{
				myVariables.put(name, new ScopeVariableImpl(name, identifier, identifier.isParameter()));
			}
		}
	}

	@Override
	@Nullable
	public ScopeVariable getVariableByName(@Nonnull final String name)
	{
		synchronized(LOCK)
		{
			return myVariables.get(name);
		}
	}

	@Override
	@Nonnull
	public Set<String> getScopeNames()
	{
		return myVariables.keySet();
	}
}
