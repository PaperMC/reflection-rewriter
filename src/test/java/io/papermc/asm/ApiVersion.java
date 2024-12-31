package io.papermc.asm;

import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public record ApiVersion(int version) implements io.papermc.asm.versioned.ApiVersion {

    public static final ApiVersion ONE = new ApiVersion(1);
    public static final ApiVersion TWO = new ApiVersion(2);
    public static final ApiVersion THREE = new ApiVersion(3);
    public static final ApiVersion FOUR = new ApiVersion(4);

    public static final List<ApiVersion> ALL_VERSIONS = List.of(ONE, TWO, THREE, FOUR);

    @Override
    public int compareTo(final io.papermc.asm.versioned.ApiVersion o) {
        return Integer.compare(this.version, ((ApiVersion) o).version);
    }
}
