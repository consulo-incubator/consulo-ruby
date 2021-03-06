package org.jetbrains.plugins.ruby.ruby.sdk.gemRootType;

import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.ui.SdkPathEditor;
import com.intellij.openapi.roots.ui.OrderRootTypeUIFactory;
import consulo.awt.TargetAWT;
import consulo.ui.image.Image;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class GemOrderRootTypeUIFactory implements OrderRootTypeUIFactory
{
	@Override
	public SdkPathEditor createPathEditor(Sdk sdk)
	{
		return new SdkPathEditor(getNodeText(), GemOrderRootType.getInstance(), new FileChooserDescriptor(true, false, false, false, false, true),
				sdk);
	}

	@Override
	public Image getIcon()
	{
		return RubyIcons.RUBY_ICON;
	}

	@Override
	public String getNodeText()
	{
		return "Gem's";
	}
}
