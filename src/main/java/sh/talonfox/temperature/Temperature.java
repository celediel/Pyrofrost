package sh.talonfox.temperature;

import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.PalettedContainer;
import sh.talonfox.Pyrofrost;

import java.util.HashMap;
import java.util.Map;

/*
 private static final BiomeData BOG = new BiomeData(0.351F, 60.0F, 40F, 10F);
    private static final BiomeData COLD_OCEAN = new BiomeData(0.373F, 20.0F, 20F, 5F);
    private static final BiomeData COLD_FOREST = new BiomeData(0.373F, 60.0F, 40F, 12F);
    private static final BiomeData COLD_DESERT = new BiomeData(0.395F, 20.0F, 40F, 20F);
    private static final BiomeData DEEP_COLD_OCEAN = new BiomeData(0.440F, 20.0F, 20F, 5F);
    private static final BiomeData ICY = new BiomeData(0.507F, 20.0F, 20F, 5F);
    private static final BiomeData TAIGA = new BiomeData(0.507F, 50.0F, 40F, 10F);
    private static final BiomeData OCEAN = new BiomeData(0.551F, 70.0F, 40F, 10F);
    private static final BiomeData RIVER = new BiomeData(0.551F, 70.0F, 40F, 10F);
    private static final BiomeData DEEP_LUKEWARM_OCEAN = new BiomeData(0.596F, 70.0F, 40F, 10F);
    private static final BiomeData EXTREME_HILLS = new BiomeData(0.618F, 50.0F, 40F, 10F);
    private static final BiomeData MOUNTAIN = new BiomeData(0.618F, 50.0F, 40F, 10F);
    private static final BiomeData LUKEWARM_OCEAN = new BiomeData(0.640F, 70.0F, 40F, 10F);
    private static final BiomeData BEACH = new BiomeData(0.663F, 70.0F, 40F, 10F);
    private static final BiomeData FOREST = new BiomeData(0.663F, 50.0F, 40F, 12F);
    public static final BiomeData UNDERGROUND = new BiomeData(0.663F, 40.0F, 40F, 12F);
    private static final BiomeData SWAMP = new BiomeData(0.685F, 90.0F, 40F, 12F);
    private static final BiomeData MUSHROOM = new BiomeData(0.685F, 70.0F, 40F, 12F);
    private static final BiomeData WARM_OCEAN = new BiomeData(0.730F, 70.0F, 40F, 10F);
    private static final BiomeData PLAINS = new BiomeData(0.774F, 60.0F, 40F, 15F);
    private static final BiomeData LUSH_DESERT = new BiomeData(0.886F, 60.0F, 40F, 15F);
    private static final BiomeData DRYLAND = new BiomeData(0.886F, 35.0F, 40F, 15F);
    private static final BiomeData RAINFOREST = new BiomeData(0.886F, 95.0F, 40F, 15F);
    private static final BiomeData JUNGLE = new BiomeData(0.997F, 90.0F, 40F, 15F);
    private static final BiomeData VOLCANIC = new BiomeData(1.04F, 35.0F, 40F, 15F);
    private static final BiomeData SAVANNA = new BiomeData(1.108F, 30.0F, 40F, 15F);
    private static final BiomeData MESA = new BiomeData(1.309F, 20.0F, 40F, 15F);
    private static final BiomeData DESERT = new BiomeData(1.354F, 20.0F, 40F, 20F);
    private static final BiomeData NONE = new BiomeData(0.15F, 40.0F, 40F, 0F);
    private static final BiomeData THEEND = new BiomeData(0.551F, 40.0F, 40F, 0F);
    private static final BiomeData NETHER = new BiomeData(1.666F, 20.0F, 40F, 0F);
 */

public class Temperature {
    private int wetness;
    private float coreTemp;
    private float skinTemp;
    private ServerPlayerEntity serverPlayer;
    private boolean isServerSide;
    private double envRadiation;
    private int ticks = 0;
    private static HashMap<TagKey<Biome>, Float> temperature = new HashMap<>();
    private static HashMap<TagKey<Biome>, Float> humidity = new HashMap<>();

    public static void initialize() {
        temperature.put(BiomeTags.IS_BADLANDS,1.309F);
        humidity.put(BiomeTags.IS_BADLANDS,20.0F);
        temperature.put(BiomeTags.IS_BEACH,0.663F);
        humidity.put(BiomeTags.IS_BEACH,70.0F);
        temperature.put(BiomeTags.IS_FOREST,0.663F);
        humidity.put(BiomeTags.IS_FOREST,50.0F);
        temperature.put(BiomeTags.IS_END,0.551F);
        humidity.put(BiomeTags.IS_END,40.0F);
        temperature.put(BiomeTags.IS_HILL,0.618F);
        humidity.put(BiomeTags.IS_HILL,50.0F);
        temperature.put(BiomeTags.IS_DEEP_OCEAN,0.596F);
        humidity.put(BiomeTags.IS_DEEP_OCEAN,70.0F);
        temperature.put(BiomeTags.IS_OCEAN,0.640F);
        humidity.put(BiomeTags.IS_OCEAN,70.0F);
        temperature.put(BiomeTags.IS_MOUNTAIN,0.618F);
        humidity.put(BiomeTags.IS_MOUNTAIN,50.0F);
        temperature.put(BiomeTags.IS_JUNGLE,0.997F);
        humidity.put(BiomeTags.IS_JUNGLE,90.0F);
        temperature.put(BiomeTags.IS_NETHER,1.666F);
        humidity.put(BiomeTags.IS_NETHER,20.0F);
        temperature.put(BiomeTags.IS_RIVER,0.551F);
        humidity.put(BiomeTags.IS_RIVER,70.0F);
        temperature.put(BiomeTags.IS_SAVANNA,1.108F);
        humidity.put(BiomeTags.IS_SAVANNA,30.0F);
        temperature.put(BiomeTags.IS_TAIGA,0.507F);
        humidity.put(BiomeTags.IS_TAIGA,50.0F);
        temperature.put(BiomeTags.IGLOO_HAS_STRUCTURE,0.507F);
        humidity.put(BiomeTags.IGLOO_HAS_STRUCTURE,20.0F);
        temperature.put(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE,0.774F);
        humidity.put(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE,60.0F);
        temperature.put(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE,0.685F);
        humidity.put(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE,90.0F);
        temperature.put(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE,1.354F);
        humidity.put(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE,20.0F);
    }

    public Temperature(ServerPlayerEntity player, boolean shouldUpdate) {
        isServerSide = shouldUpdate;
        serverPlayer = player;
    }

    public void tick() {
        ticks += 1;
        if(ticks % 20 == 0) {
            Pyrofrost.LOGGER.info("WGBT: "+getWBGT());
        }
    }

    private static double mcTempToCelsius(float temp) {
        double out = 25.27027027 + (44.86486486 * temp);
        out = (out - 32) * 0.5556;
        return temp;
    }

    private static double mcTempConv(float temp) {
        return 25.27027027 + (44.86486486 * temp);
    }

    private static double tempToCelsius(double temp) {
        double out = (temp / 0.5556) + 32;
        return (out - 25.27027027) / 44.86486486;
    }

    private static double tempToF(double temp) {
        return (temp - 25.27027027) / 44.86486486;
    }

    private static double getBlackGlobe(double radiation, float dryTemp, double relativeHumidity) {
        double dryTempC = mcTempToCelsius(dryTemp);

        double blackGlobeTemp = (0.01498 * radiation) + (1.184 * dryTempC) - (0.0789 * (relativeHumidity / 100)) - 2.739;

        return tempToCelsius(blackGlobeTemp);
    }

    private static float getBiomeTemperature(RegistryEntry<Biome> biome) {
        for(Map.Entry<TagKey<Biome>,Float> entry : temperature.entrySet()) {
            if (biome.isIn(entry.getKey())) {
                return entry.getValue();
            }
        }
        return 0.663F;
    }

    private static float getBiomeHumidity(RegistryEntry<Biome> biome) {
        for(Map.Entry<TagKey<Biome>,Float> entry : humidity.entrySet()) {
            if (biome.isIn(entry.getKey())) {
                return entry.getValue();
            }
        }
        return 40.0F;
    }

    private static double getHeatIndex(float dryTemp, double rh) {
        double dryTempF = mcTempConv(dryTemp);
        double hIndex;

        if (dryTempF < 80.0) {
            hIndex = 0.5 * (dryTempF + 61.0 +((dryTempF - 68.0) * 1.2)) + (rh*0.094);
        }
        else {
            hIndex = -42.379 + 2.04901523 * dryTempF + 10.14333127 * rh;
            hIndex = hIndex - 0.22475541 * dryTempF * rh - 6.83783 * Math.pow(10, -3) * dryTempF * dryTempF;
            hIndex = hIndex - 5.481717 * Math.pow(10, -2) * rh * rh;
            hIndex = hIndex + 1.22874 * Math.pow(10, -3) * dryTempF * dryTempF * rh;
            hIndex = hIndex + 8.5282 * Math.pow(10, -4) * dryTempF * rh * rh;
            hIndex = hIndex - 1.99 * Math.pow(10, -6) * dryTempF * dryTempF * rh * rh;
        }

        return tempToF(hIndex);
    }

    private double getWBGT() {
        float dryTemperature = getBiomeTemperature(serverPlayer.getServerWorld().getBiome(serverPlayer.getBlockPos()));
        float humidity = getBiomeHumidity(serverPlayer.getServerWorld().getBiome(serverPlayer.getBlockPos()));
        double wetTemperature = getHeatIndex(dryTemperature,humidity);
        double blackGlobeTemp = (float)getBlackGlobe(getSolarRadiation(serverPlayer.getServerWorld(),serverPlayer.getBlockPos()), dryTemperature, humidity);
        EnvironmentData data = getInfo();
        double airTemperature;
        if (data.isSheltered() || data.isUnderground()) {
            airTemperature = (wetTemperature * 0.7F) + (blackGlobeTemp * 0.3F);
        } else {
            airTemperature = (wetTemperature * 0.7F) + (blackGlobeTemp * 0.2F) + (dryTemperature * 0.1F);
        }
        return airTemperature;
    }

    private EnvironmentData getInfo() {
        boolean isSheltered = true; // So basically me
        boolean isUnderground = true;
        double waterBlocks = 0;
        double totalBlocks = 0;
        double radiation = 0.0;
        BlockPos pos = serverPlayer.getBlockPos();
        for (int x = -12; x <= 12; x++) {
            for (int z = -12; z <= 12; z++) {
                if (isSheltered && (x <= 2 && x >= -2) && (z <= 2 && z >= -2)) {
                    isSheltered = !serverPlayer.getServerWorld().isSkyVisible(BlockPos.ofFloored(serverPlayer.getEyePos()).add(x, 0, z).up());
                }
                for (int y = -3; y <= 11; y++) {
                    ChunkPos chunkPos = new ChunkPos((pos.getX() + x) >> 4,(pos.getZ() + z) >> 4);
                    Chunk chunk = serverPlayer.getServerWorld().getChunk(chunkPos.getStartPos());

                    if (chunk == null) continue;
                    BlockPos blockpos = pos.add(x, y, z);
                    PalettedContainer<BlockState> palette;
                    try {
                        palette = chunk.getSection((blockpos.getY() >> 4) - chunk.getHighestNonEmptySection()).getBlockStateContainer();

                    }
                    catch (Exception e) {
                        continue;
                    }
                    BlockState state = palette.get(blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15);
                    boolean isWater = state.isOf(Blocks.WATER);
                    if (isUnderground && y >= 0 && !isWater) {
                        isUnderground = !serverPlayer.getServerWorld().isSkyVisible(BlockPos.ofFloored(serverPlayer.getEyePos()).add(x, y, z).up());
                    }
                    if ((x <= 5 && x >= -5) && (y <= 5) && (z <= 5 && z >= -5)) {
                        totalBlocks++;

                        if (isWater) {
                            waterBlocks++;
                        }
                    }
                    if(y <= 3) {
                        Float rad = ThermalRadiation.radiationBlocks.get(new Identifier(state.getBlock().toString()));
                        if (rad != null) {
                            radiation += rad;
                        }
                    }
                }
            }
        }
        return new EnvironmentData(isUnderground,isSheltered,radiation);
    }

    private static float getSolarRadiation(ServerWorld world, BlockPos pos) {
        double radiation = 0.0;
        double sunlight = world.getLightLevel(LightType.SKY, pos.up()) - world.getAmbientDarkness();
        float f = world.getSkyAngleRadians(1.0F);

        if (sunlight > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f += (f1 - f) * 0.2F;
            sunlight = sunlight * MathHelper.cos(f);
        }

        radiation += sunlight * 100;

        return (float)Math.max(radiation, 0);
    }
}
