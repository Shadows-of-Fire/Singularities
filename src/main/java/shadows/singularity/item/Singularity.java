package shadows.singularity.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import shadows.singularity.Singularities;
import shadows.singularity.Singularities.SingularityComparator;

public class Singularity {

	private static final Map<String, Singularity> NAME_TO_SINGULARITY = new HashMap<>();
	private static final Map<Integer, Singularity> META_TO_SINGULARITY = new HashMap<>();
	private static final Singularity MISSING = new Singularity("MISSING", 0x00000, 0x00000, OreDictionary.WILDCARD_VALUE, EnumRarity.EPIC);
	private static final List<Singularity> SORTED = new ArrayList<>();

	private final String name;
	private final int color1;
	private final int color2;
	private final int id;
	private final EnumRarity rarity;

	public Singularity(String name, int color1, int color2, int meta, EnumRarity rarity) {
		Preconditions.checkArgument(NAME_TO_SINGULARITY.get(name) == null, "Duplicate singularity! Name: " + name);
		Preconditions.checkArgument(META_TO_SINGULARITY.get(meta) == null, "Meta already in use! Name: " + name + ", Meta: " + meta);
		this.name = name;
		this.color1 = color1;
		this.color2 = color2;
		this.id = meta;
		this.rarity = rarity;
	}

	public Singularity(String name, int color1, int color2, int meta) {
		this(name, color1, color2, meta, EnumRarity.UNCOMMON);
	}

	public String getName() {
		return name;
	}

	public String makeOreName() {
		String[] split = name.split(" ");
		String concat = "";
		for (String s : split)
			concat += s;
		return "singularity" + StringUtils.capitalize(concat);
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
		return NAME_TO_SINGULARITY.getOrDefault(name, MISSING);
	}

	public static Singularity getByID(int id) {
		return META_TO_SINGULARITY.getOrDefault(id, MISSING);
	}

	public static void init() {
	}

	public static Singularity fromString(String s) {
		String[] split = s.split(", ");
		Preconditions.checkArgument(split.length == 5, "Invalid Singularity! String: " + s);
		String name = split[0];
		int color1 = Integer.parseInt(split[1].contains("0x") ? split[1].substring(2) : split[1], split[1].contains("0x") ? 16 : 10);
		int color2 = Integer.parseInt(split[2].contains("0x") ? split[2].substring(2) : split[2], split[2].contains("0x") ? 16 : 10);
		int rarity = Integer.parseInt(split[3]);
		int meta = Integer.parseInt(split[4]);
		Preconditions.checkArgument(rarity >= 0 && rarity <= 3, "Invalid rarity int value, must be between 0 and 3.  Current: " + rarity);
		return new Singularity(name, color1, color2, meta, EnumRarity.values()[rarity]);
	}

	/**
	 * Singularities are sorted during Register<Item>.  Register them before this time, or they will be neglected.
	 */
	public static void register(Singularity s) {
		NAME_TO_SINGULARITY.put(s.getName(), s);
		META_TO_SINGULARITY.put(s.getID(), s);
	}

	public static String[] updateToV220(String[] sing) {
		String[] ret = new String[sing.length];
		for (int i = 0; i < sing.length; i++) {
			String[] split = sing[i].split(", ");
			if (split.length == 5) {
				ret[i] = sing[i];
				continue;
			} else {
				ret[i] = sing[i] + ", " + i;
			}
		}
		return ret;
	}

	public static void operateSorted(Consumer<? super Singularity> c) {
		if (SORTED.isEmpty()) SORTED.addAll(Singularity.getSingularities().stream().sorted(SingularityComparator.INSTANCE).collect(Collectors.toList()));
		SORTED.forEach(c);
	}

}
