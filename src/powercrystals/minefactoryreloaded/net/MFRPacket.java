package powercrystals.minefactoryreloaded.net;

import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoDisenchanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoEnchanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockSmasher;

public class MFRPacket extends PacketCoFHBase {

	public static void initialize() {

		PacketHandler.instance.registerPacket(MFRPacket.class);
	}

	public enum PacketType {

		HAM_UPDATE, UPGRADE_LEVEL, ENCHANTER_BUTTON, HARVESTER_BUTTON, CHRONOTYPER_BUTTON, AUTO_JUKEBOX_BUTTON, AUTO_SPAWNER_BUTTON, CIRCUIT_DEFINITION,
		LOGIC_SET_CIRCUIT, LOGIC_SET_PIN, LOGIC_REINITIALIZE, ROUTER_BUTTON, FAKE_SLOT_CHANGE, ROCKET_LAUNCH
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		try {
			int type = getByte();
			BlockPos pos;
			TileEntity te;
			World world = player.worldObj;

			switch(PacketType.values()[type]) {
				case HAM_UPDATE:
					pos = getCoords();
					te = world.getTileEntity(pos);
					if (te instanceof TileEntityFactory && ((TileEntityFactory) te).hasHAM()) {
						sendUpgradeLevelToClient((TileEntityFactory) te);
					}
					return;
				case UPGRADE_LEVEL:
					pos = getCoords();
					te = world.getTileEntity(pos);
					if (te instanceof TileEntityFactory && ((TileEntityFactory) te).hasHAM()) {
						((TileEntityFactory) te).getHAM().setUpgradeLevel(getByte());
					}
					return;
				case ENCHANTER_BUTTON: // client -> server: autoenchanter GUI buttons
					pos = getCoords();
					te = world.getTileEntity(pos);

					byte amt = getByte();
					if (te instanceof TileEntityAutoEnchanter) {
						((TileEntityAutoEnchanter) te).setTargetLevel(((TileEntityAutoEnchanter) te).getTargetLevel() + amt);
					} else if (te instanceof TileEntityBlockSmasher) {
						((TileEntityBlockSmasher) te).setFortune(((TileEntityBlockSmasher) te).getFortune() + amt);
					} else if (te instanceof TileEntityAutoDisenchanter) {
						((TileEntityAutoDisenchanter) te).setRepeatDisenchant(amt == 1 ? true : false);
					}
					break;
				case HARVESTER_BUTTON: // client -> server: harvester setting
					pos = getCoords();
					te = world.getTileEntity(new BlockPos(x, y, z));

					if (te instanceof TileEntityHarvester) {
						((TileEntityHarvester) te).getSettings().put(ByteBufUtils.readUTF8String(data), data.readBoolean());
					}
					break;
			}

		} catch (Exception e) {
			MineFactoryReloadedCore.log().error("Packet payload failure! Please check your config files!");
			e.printStackTrace();
		}
		switch (data.readUnsignedShort()) {
			case Packets.ChronotyperButton: // client -> server: toggle chronotyper
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));

				if (te instanceof TileEntityChronotyper) {
					((TileEntityChronotyper) te).setMoveOld(!((TileEntityChronotyper) te).getMoveOld());
				} else if (te instanceof TileEntityDeepStorageUnit) {
					((TileEntityDeepStorageUnit) te).setIsActive(!((TileEntityDeepStorageUnit) te).isActive());
					((TileEntityDeepStorageUnit) te).markForUpdate();
					Packets.sendToAllPlayersWatching(te);
				}
				break;
			case Packets.AutoJukeboxButton: // client -> server: copy record
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));

				if (te instanceof TileEntityAutoJukebox) {
					TileEntityAutoJukebox j = ((TileEntityAutoJukebox) te);
					int button = data.readByte();
					if (button == 1)
						j.playRecord();
					else if (button == 2)
						j.stopRecord();
					else if (button == 3) j.copyRecord();
				}
				break;
			case Packets.AutoSpawnerButton: // client -> server: toggle autospawner
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));

				if (te instanceof TileEntityAutoSpawner) {
					((TileEntityAutoSpawner) te).setSpawnExact(!((TileEntityAutoSpawner) te).getSpawnExact());
				}
				break;
			case Packets.CircuitDefinition: // client -> server: request circuit from server
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));

				if (te instanceof TileEntityRedNetLogic) {
					((TileEntityRedNetLogic) te).sendCircuitDefinition(data.readInt());
				}
				break;
			case Packets.LogicSetCircuit: // client -> server: set circuit
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));

				int circuit = data.readInt();
				if (te instanceof TileEntityRedNetLogic) {
					((TileEntityRedNetLogic) te).initCircuit(circuit, ByteBufUtils.readUTF8String(data));
					((TileEntityRedNetLogic) te).sendCircuitDefinition(circuit);
				}
				break;
			case Packets.LogicSetPin: // client -> server: set pin
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));

				amt = data.readByte();
				int circuitIndex = data.readInt(),
						pinIndex = data.readInt(),
						buffer = data.readInt(),
						pin = data.readInt();
				if (te instanceof TileEntityRedNetLogic) {
					if (amt == 0) {
						((TileEntityRedNetLogic) te).setInputPinMapping(circuitIndex, pinIndex, buffer, pin);
					} else if (amt == 1) {
						((TileEntityRedNetLogic) te).setOutputPinMapping(circuitIndex, pinIndex, buffer, pin);
					}
					((TileEntityRedNetLogic) te).sendCircuitDefinition(circuitIndex);
				}
				break;
			case Packets.LogicReinitialize: // client -> server: set circuit
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));
				player = (EntityPlayer) world.getEntityByID(data.readInt());

				if (te instanceof TileEntityRedNetLogic) {
					((TileEntityRedNetLogic) te).reinitialize(player);
				}
				break;
			case Packets.RouterButton: // client -> server: toggle 'levels'/'reject unmapped' mode
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));

				a = data.readInt();
				if (te instanceof TileEntityEnchantmentRouter) {
					switch (a) {
						case 2:
							((TileEntityItemRouter) te).setRejectUnmapped(!((TileEntityItemRouter) te).getRejectUnmapped());
							break;
						case 1:
							((TileEntityEnchantmentRouter) te).setMatchLevels(!((TileEntityEnchantmentRouter) te).getMatchLevels());
							break;
					}
				} else if (te instanceof TileEntityItemRouter) {
					((TileEntityItemRouter) te).setRejectUnmapped(!((TileEntityItemRouter) te).getRejectUnmapped());
				} else if (te instanceof TileEntityEjector) {
					switch (a) {
						case 1:
							((TileEntityEjector) te).setIsWhitelist(!((TileEntityEjector) te).getIsWhitelist());
							break;
						case 2:
							((TileEntityEjector) te).setIsNBTMatch(!((TileEntityEjector) te).getIsNBTMatch());
							break;
						case 3:
							((TileEntityEjector) te).setIsIDMatch(!((TileEntityEjector) te).getIsIDMatch());
							break;
					}
				} else if (te instanceof TileEntityAutoAnvil) {
					((TileEntityAutoAnvil) te).setRepairOnly(!((TileEntityAutoAnvil) te).getRepairOnly());
				} else if (te instanceof TileEntityChunkLoader) {
					((TileEntityChunkLoader) te).setRadius((short) a);
				} else if (te instanceof TileEntityPlanter) {
					((TileEntityPlanter) te).setConsumeAll(!((TileEntityPlanter) te).getConsumeAll());
				} else if (te instanceof TileEntityMobRouter) {
					switch (a) {
						case 1:
							((TileEntityMobRouter) te).setWhiteList(!((TileEntityMobRouter) te).getWhiteList());
							break;
						case 2:
							((TileEntityMobRouter) te).setMatchMode(((TileEntityMobRouter) te).getMatchMode() + 1);
							break;
						case 3:
							((TileEntityMobRouter) te).setMatchMode(((TileEntityMobRouter) te).getMatchMode() - 1);
							break;
					}
				}
				break;
			case Packets.FakeSlotChange: // client -> server: client clicked on a fake slot
				pos = getCoords();
				te = world.getTileEntity(new BlockPos(x, y, z));
				player = (EntityPlayer) world.getEntityByID(data.readInt());

				ItemStack playerStack = player.inventory.getItemStack();
				int slotNumber = data.readInt(),
						click = data.readByte();
				if (te instanceof IInventory) {
					if (playerStack == null) {
						((IInventory) te).setInventorySlotContents(slotNumber, null);
					} else {
						playerStack = playerStack.copy();
						playerStack.stackSize = click == 1 ? -1 : 1;
						ItemStack s = ((IInventory) te).getStackInSlot(slotNumber);
						if (!UtilInventory.stacksEqual(s, playerStack))
							playerStack.stackSize = 1;
						else
							playerStack.stackSize = Math.max(playerStack.stackSize + s.stackSize, 1);
						((IInventory) te).setInventorySlotContents(slotNumber, playerStack);
					}
				}
				break;
			case Packets.RocketLaunch: // client -> server: client firing SPAMR missile
				Entity owner = world.getEntityByID(data.readInt());
				int t = data.readInt();
				Entity target = null;
				if (t != Integer.MIN_VALUE) {
					target = world.getEntityByID(t);
				}

				if (owner instanceof EntityLivingBase) {
					EntityRocket r = new EntityRocket(world, ((EntityLivingBase) owner), target);
					world.spawnEntityInWorld(r);
				}
				break;
		}
		return null;
	}

	public static void sendHAMUpdateToServer(TileEntity te) {

		PacketHandler.sendToServer(getPacket(PacketType.HAM_UPDATE).addCoords(te));
	}

	public static void sendUpgradeLevelToClient(TileEntityFactory te) {

		if (te.hasHAM()) {
			PacketHandler.sendToDimension(getPacket(PacketType.UPGRADE_LEVEL).addCoords(te).addByte(te.getHAM().getUpgradeLevel()),
					te.getWorld().provider.getDimension());
		}
	}

	public static void sendEnchanterButtonToServer(TileEntity te) {
		finish
	}

	public static PacketCoFHBase getPacket(PacketType type) {

		return new MFRPacket().addByte(type.ordinal());
	}
}
