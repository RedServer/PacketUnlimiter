package com.gamerforea.packetunlimiter;

import java.io.File;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name(CoreMod.NAME)
@IFMLLoadingPlugin.SortingIndex(1001)
public final class CoreMod implements IFMLLoadingPlugin
{
	public static final String MODID = "PacketUnlimiter";
	public static final String NAME = "PacketUnlimiter";
	public static final String VERSION = "@VERSION@";

	public static boolean isObfuscated = false;
	public static boolean bigPacketWarning = true;
	public static boolean readNbtLimit = false;

	public CoreMod()
	{
		Configuration config = new Configuration(new File("config", NAME + ".cfg"));
		config.load();
		bigPacketWarning = config.getBoolean("bigPacketWarning", "general", bigPacketWarning, "Включить оповещение при превышении стандартного лимита (2 MB)");
		readNbtLimit = config.getBoolean("readNbtLimit", "general", readNbtLimit, "Включить ограничение чтения NBT");
		config.save();
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { "com.gamerforea.packetunlimiter.asm.ASMTransformer" };
	}

	@Override
	public String getModContainerClass()
	{
		return "com.gamerforea.packetunlimiter.ModContainer";
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		isObfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
