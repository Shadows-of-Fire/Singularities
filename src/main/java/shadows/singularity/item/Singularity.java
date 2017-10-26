package shadows.singularity.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import shadows.singularity.Singularities;

public class Singularity {

	private static final Map<String, Singularity> NAME_TO_SINGULARITY = new HashMap<>();
	private static final Map<Integer, Singularity> META_TO_SINGULARITY = new HashMap<>();

	private static int lastMeta = 0;

	private final String name;
	private final int color1;
	private final int color2;
	private final int id;
	private final EnumRarity rarity;

	public Singularity(String name, int color1, int color2, EnumRarity rarity) {
		Preconditions.checkArgument(NAME_TO_SINGULARITY.get(name) == null, "Duplicate singularity! Name: " + name);
		this.name = name;
		this.color1 = color1;
		this.color2 = color2;
		this.id = lastMeta++;
		this.rarity = rarity;
	}

	public Singularity(String name, int color1, int color2) {
		this(name, color1, color2, EnumRarity.UNCOMMON);
	}

	public String getName() {
		return name;
	}

	public int getColor1() {
		return color1;
	}

	public int getColor2() {
		return color2;
	}

	public int getID() {
		return id;
	}

	public ItemStack getStack() {
		return new ItemStack(Singularities.SINGULARITY, 1, id);
	}

	public EnumRarity getRarity() {
		return rarity;
	}

	public static Collection<Singularity> getSingularities() {
		return NAME_TO_SINGULARITY.values();
	}

	public static Singularity getByName(String name) {
		return NAME_TO_SINGULARITY.get(name);
	}

	public static Singularity getByID(int id) {
		return META_TO_SINGULARITY.get(id);
	}

	public static int getTotalSingularityCount() {
		return lastMeta;
	}

	public static void init() {
	}

	public static Singularity fromString(String s) {
		String[] split = s.split(", ");
		Preconditions.checkArgument(split.length == 4, "Attempting to parse invalid string for singularity! String: " + s);
		String name = split[0];
		int color1 = Integer.parseInt(split[1].contains("0x") ? split[1].substring(2) : split[1], split[1].contains("0x") ? 16 : 10);
		int color2 = Integer.parseInt(split[2].contains("0x") ? split[2].substring(2) : split[2], split[2].contains("0x") ? 16 : 10);
		int rarity = Integer.parseInt(split[3]);
		Preconditions.checkArgument(rarity >= 0 && rarity <= 3, "Invalid rarity int value, must be between 0 and 3.  Current: " + rarity);
		return new Singularity(name, color1, color2, EnumRarity.values()[rarity]);
	}

	public static void register(Singularity s) {
		NAME_TO_SINGULARITY.put(s.getName(), s);
		META_TO_SINGULARITY.put(s.getID(), s);
	}

}
