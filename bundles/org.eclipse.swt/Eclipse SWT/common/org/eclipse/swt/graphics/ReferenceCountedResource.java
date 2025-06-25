/*******************************************************************************
 * Copyright (c) 2025, 2025 Hannes Wellmann and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Hannes Wellmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.swt.graphics;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * @since 3.129
 * @noreference This class is not intended to be referenced by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class ReferenceCountedResource extends Resource { // TODO: rename to AutoDisposedResource,
																	// AutoManagedResource?
	private int references = 0;
	// TODO: try to move the initializer field to this class? Maybe add a second
	// runnable for immediate checks (until flexible constructor bodies are
	// available).

	public ReferenceCountedResource() {
		super();
	}

	public ReferenceCountedResource(Device device) {
		super(device);
	}

	// FIXME: Actually this requires immutable resources (like images), which is not
	// always the case.
	// So at least for immutable cases the state has to be saved on dispose?!
	// Or for images only an initializer with GC should be allowed by the GC should
	// not be accessible directly/always.
	// So creating a new GC(image) is a problem!
	// Problematic are Resources that are also Drawable

	public void referenceNativeResource() {
		if (references++ == 0) {
			initNonDisposeTracking(); // TODO: necessary?
			allocateNativeResource();
		}
	}

	public void unreferenceNativeResource() {
		if (--references == 0) {
			releaseNativeResource();
		}
	}

	protected boolean isReferenced() {
		return references > 0;
	}

	interface RuntimeCloseable extends AutoCloseable {
		@Override
		void close();
	}

	public RuntimeCloseable withNativeResources() {
		referenceNativeResource();
		return () -> unreferenceNativeResource();
	}

	protected abstract void allocateNativeResource();

	protected void releaseNativeResource() {
		dispose();
		// destroy(); // TODO: use destroy?!
	};

	final DisposeListener disposeListener = e -> unreferenceNativeResource();

	// TODO: instead of listener, implement this in the corresponding controls? This
	// would save the additional listener reference but would complicate the impls
	public static <T extends ReferenceCountedResource> T resetResource(T oldResource, T newResource, Widget control) {
		if (oldResource != null) {
			oldResource.unreferenceNativeResource();
			control.removeDisposeListener(oldResource.disposeListener);
		}
		newResource.referenceNativeResource();
		control.addDisposeListener(newResource.disposeListener);

		return newResource;
	}

}
