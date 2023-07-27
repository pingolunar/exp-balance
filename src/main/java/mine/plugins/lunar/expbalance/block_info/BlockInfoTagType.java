package mine.plugins.lunar.expbalance.block_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mine.plugins.lunar.expbalance.block_info.tags.CropTag;
import mine.plugins.lunar.expbalance.block_info.tags.NaturalTag;
import mine.plugins.lunar.expbalance.block_info.tags.ToolTag;

@SuppressWarnings("unused")
@AllArgsConstructor
public enum BlockInfoTagType {
    CROP(new CropTag()),
    NATURAL(new NaturalTag()),
    TOOL(new ToolTag());

    @Getter private final BlockInfoTag blockInfoTag;

}
