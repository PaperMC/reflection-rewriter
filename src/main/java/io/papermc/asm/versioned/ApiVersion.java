package io.papermc.asm.versioned;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.OverrideOnly
public interface ApiVersion extends Comparable<ApiVersion> {

    default boolean isNewerThan(final ApiVersion apiVersion) {
        return this.compareTo(apiVersion) > 0;
    }

    default boolean isOlderThan(final ApiVersion apiVersion) {
        return this.compareTo(apiVersion) < 0;
    }

    default boolean isNewerThanOrSameAs(final ApiVersion apiVersion) {
        return this.compareTo(apiVersion) >= 0;
    }

    default boolean isOlderThanOrSameAs(final ApiVersion apiVersion) {
        return this.compareTo(apiVersion) <= 0;
    }
}
