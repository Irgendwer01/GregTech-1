package gregtech.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.Opcodes;

public class GregTechTransformer implements IClassTransformer, Opcodes {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        String internalName = transformedName.replace('.', '/');
        switch (internalName) {
            /*
             * case JEIVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(0);
             * classReader.accept(new TargetClassVisitor(classWriter, JEIVisitor.TARGET_METHOD, JEIVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case ConcretePowderVisitor.TARGET_CLASS_NAME:
             * if (ConfigHolder.recipes.disableConcreteInWorld) {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, ConcretePowderVisitor.TARGET_METHOD,
             * ConcretePowderVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             * break;
             */
            /*
             * case LayerCustomHeadVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, LayerCustomHeadVisitor.TARGET_METHOD,
             * LayerCustomHeadVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case SpecialArmorApplyVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new SpecialArmorClassVisitor(classWriter, SpecialArmorApplyVisitor.TARGET_METHOD,
             * SpecialArmorApplyVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case LayerArmorBaseVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, LayerArmorBaseVisitor.TARGET_METHOD,
             * LayerArmorBaseVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case RegionRenderCacheBuilderVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, RegionRenderCacheBuilderVisitor.TARGET_METHOD,
             * RegionRenderCacheBuilderVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case RenderChunkVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(
             * new TargetClassVisitor(classWriter, RenderChunkVisitor.TARGET_METHOD, RenderChunkVisitor::new),
             * 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case EntityRendererVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, EntityRendererVisitor.TARGET_METHOD,
             * EntityRendererVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case BlockVisitor.TARGET_CLASS_NAME: {
             * try {
             * // must use Class#forName because CTM is client side only, and there is no other way to check
             * Class.forName("team.chisel.ctm.CTM", false, Launch.classLoader);
             * } catch (ClassNotFoundException ignored) {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * ClassNode classNode = new ClassNode();
             * classReader.accept(classNode, 0);
             * BlockVisitor.handleClassNode(classNode).accept(classWriter);
             * return classWriter.toByteArray();
             * }
             * break;
             * }
             */
            /*
             * case WorldVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, WorldVisitor.TARGET_METHOD, WorldVisitor::new),
             * 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case ModelCTMVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(
             * new TargetClassVisitor(classWriter, ModelCTMVisitor.TARGET_METHOD, ModelCTMVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case AbstractCTMBakedModelVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, AbstractCTMBakedModelVisitor.TARGET_METHOD,
             * AbstractCTMBakedModelVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case LittleTilesVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(
             * new TargetClassVisitor(classWriter, LittleTilesVisitor.TARGET_METHOD, LittleTilesVisitor::new),
             * 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case CCLVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, CCLVisitor.TARGET_METHOD, CCLVisitor::new), 0);
             * return classWriter.toByteArray();
             * }
             * case RenderItemVisitor.TARGET_CLASS_NAME: {
             * ClassNode classNode = new ClassNode();
             * ClassReader classReader = new ClassReader(basicClass);
             * classReader.accept(classNode, 0);
             * Iterator<MethodNode> methods = classNode.methods.iterator();
             * RenderItemVisitor.transform(methods);
             * ClassWriter classWriter = new ClassWriter(0);
             * classNode.accept(classWriter);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case RecipeRepairItemVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * ClassNode classNode = new ClassNode();
             * classReader.accept(classNode, 0);
             * RecipeRepairItemVisitor.handleClassNode(classNode).accept(classWriter);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case DamageSourceVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * ClassNode classNode = new ClassNode();
             * classReader.accept(classNode, 0);
             * DamageSourceVisitor.handleClassNode(classNode).accept(classWriter);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case TheOneProbeVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(0);
             * classReader.accept(
             * new TargetClassVisitor(classWriter, TheOneProbeVisitor.TARGET_METHOD, TheOneProbeVisitor::new),
             * 0);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case MinecraftVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(0);
             * classReader.accept(
             * new TargetClassVisitor(classWriter, MinecraftVisitor.PROCESS_KEY_F3, MinecraftVisitor::new),
             * ClassReader.EXPAND_FRAMES);
             * return classWriter.toByteArray();
             * }
             */
            /*
             * case ModelLoaderRegistryVisitor.TARGET_CLASS_NAME: {
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(0);
             * classReader.accept(new TargetClassVisitor(classWriter, ModelLoaderRegistryVisitor.TARGET_METHOD,
             * ModelLoaderRegistryVisitor::new), ClassReader.EXPAND_FRAMES);
             * return classWriter.toByteArray();
             * }
             */
            // TODO: Remove when vintagium has proper support for other rendering layers
            // case VintagiumPassManagerVisitor.TARGET_CLASS_NAME: {
            // ClassReader classReader = new ClassReader(basicClass);
            // ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            // classReader.accept(
            // new TargetClassVisitor(classWriter, VintagiumPassManagerVisitor.TARGET_METHOD,
            // VintagiumPassManagerVisitor::new),
            // ClassReader.EXPAND_FRAMES);
            // return classWriter.toByteArray();
            // }
            // case VintagiumManagerVistor.TARGET_CLASS_NAME: {
            // ClassReader classReader = new ClassReader(basicClass);
            // ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            // classReader.accept(
            // new VintagiumManagerVistor(classWriter),
            // 0);
            // return classWriter.toByteArray();
            // }
            // case OreIngredientVisitor.TARGET_CLASS_NAME: {
            // ClassReader classReader = new ClassReader(basicClass);
            // ClassWriter classWriter = new ClassWriter(0);
            // classReader.accept(new TargetClassVisitor(classWriter, OreIngredientVisitor.TARGET_METHOD,
            // OreIngredientVisitor::new), 0);
            // return classWriter.toByteArray();
            // }
            /*
             * if (EnchantmentCanApplyVisitor.CLASS_TO_MAPPING_MAP.containsKey(internalName)) {
             * ObfMapping methodMapping = EnchantmentCanApplyVisitor.CLASS_TO_MAPPING_MAP.get(internalName);
             * ClassReader classReader = new ClassReader(basicClass);
             * ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
             * classReader.accept(new TargetClassVisitor(classWriter, methodMapping,
             * mv -> new EnchantmentCanApplyVisitor(mv, methodMapping)), ClassReader.EXPAND_FRAMES);
             * return classWriter.toByteArray();
             * }
             */
        }
        return basicClass;
    }
}
