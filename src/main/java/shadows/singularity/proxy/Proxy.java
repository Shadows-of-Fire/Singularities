package shadows.singularity.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Proxy {

	public void preInit(FMLPreInitializationEvent e) {
	}

	public void init(FMLInitializationEvent e) {
	}

	@SuppressWarnings("deprecation")
	public String translate(String lang, Object... args) {
		return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(lang, args);
	}
}
