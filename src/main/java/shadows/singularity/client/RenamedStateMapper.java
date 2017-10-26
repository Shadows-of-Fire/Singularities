package shadows.singularity.client;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shadows.singularity.Singularities;

@SideOnly(Side.CLIENT)
public class RenamedStateMapper implements IStateMapper {

	final String path;
	String append = "";
	String variant = "";

	public RenamedStateMapper(String path) {
		this.path = path;
	}

	public RenamedStateMapper(String path, String append) {
		this.path = path;
		this.append = append;
	}

	public RenamedStateMapper(String path, String append, String variant) {
		this(path, append);
		this.variant = variant;
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		Map<IBlockState, ModelResourceLocation> map = new DefaultStateMapper().putStateModelLocations(block);
		for (IBlockState state : map.keySet()) {
			ModelResourceLocation loc = map.get(state);
			String var = variant.length() == 0 ? loc.getVariant() : variant;

			map.put(state, new ModelResourceLocation(Singularities.MODID + ":" + path, var + append));
		}
		return map;
	}

}
