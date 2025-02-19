package com.gamerforea.packetunlimiter.asm;

import net.minecraftforge.fml.relauncher.FMLRelaunchLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.gamerforea.packetunlimiter.CoreMod;

import net.minecraft.launchwrapper.IClassTransformer;
import java.util.ListIterator;

public final class ASMTransformer implements IClassTransformer, Opcodes {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.network.PacketBuffer"))
			return patchPacketBuffer(basicClass);

		return basicClass;
	}

	private static byte[] patchPacketBuffer(byte[] basicClass) {
		boolean patched = false;
		String readNbt = CoreMod.isObfuscated ? "func_150793_b" : "readCompoundTag";

		ClassNode cNode = new ClassNode();
		new ClassReader(basicClass).accept(cNode, 0);

		for (MethodNode mNode : cNode.methods)
			if (mNode.name.equals(readNbt) && mNode.desc.equals("()Lnet/minecraft/nbt/NBTTagCompound;")) {
				if (CoreMod.readNbtLimit)
					continue;

				ListIterator<AbstractInsnNode> it = mNode.instructions.iterator();
				while (it.hasNext()) {
					AbstractInsnNode insn = it.next();

					if (insn.getType() == AbstractInsnNode.LDC_INSN) {
						LdcInsnNode ldcNode = (LdcInsnNode) insn;
						AbstractInsnNode next = insn.getNext();

						if (next.getType() == AbstractInsnNode.METHOD_INSN) {
							MethodInsnNode methodNode = (MethodInsnNode) next;

							if ("net/minecraft/nbt/NBTSizeTracker".equals(methodNode.owner)) {
								ldcNode.cst = Long.MAX_VALUE;
								patched = true;
								break;
							}
						}
					}
				}
			}

		if (patched) {
			FMLRelaunchLog.info("[PacketUnlimiter] PacketBuffer successfully patched!");

			ClassWriter cWriter = new ClassWriter(0);
			cNode.accept(cWriter);
			return cWriter.toByteArray();
		}

		return basicClass;
	}
}
