package consulo.jruby.module.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.projectRoots.Sdk;
import consulo.disposer.Disposable;
import consulo.extension.ui.ModuleExtensionBundleBoxBuilder;
import consulo.module.extension.MutableModuleExtensionWithSdk;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.roots.ModuleRootLayer;
import consulo.ruby.module.extension.RubyModuleExtension;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyMutableModuleExtension extends JRubyModuleExtension implements RubyModuleExtension<JRubyModuleExtension>, MutableModuleExtensionWithSdk<JRubyModuleExtension>
{
	public JRubyMutableModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);
	}

	@Nonnull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
	}

	@RequiredUIAccess
	@Nullable
	@Override
	public Component createConfigurationComponent(@Nonnull Disposable disposable, @Nonnull Runnable runnable)
	{
		VerticalLayout layout = VerticalLayout.create();
		layout.add(ModuleExtensionBundleBoxBuilder.createAndDefine(this, disposable, runnable).build());
		return layout;
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@Nonnull JRubyModuleExtension extension)
	{
		return isModifiedImpl(extension);
	}
}
