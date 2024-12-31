package io.papermc.asm.versioned;

import io.papermc.asm.rules.RewriteRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.BinaryOperator;

public record MappedVersionRuleFactory<R extends RewriteRule>(NavigableMap<ApiVersion, ? extends R> versions, BinaryOperator<R> mergeFunction) implements VersionedRuleFactory {

    public static <M extends RewriteRule & Mergeable<M>> MappedVersionRuleFactory<M> mergeable(final NavigableMap<ApiVersion, M> versions) {
        return new MappedVersionRuleFactory<>(versions, Mergeable::merge);
    }

    @Override
    public RewriteRule createRule(final ApiVersion apiVersion) {
        final List<R> toMerge = new ArrayList<>(this.versions.tailMap(apiVersion, true).values());
        if (toMerge.isEmpty()) {
            return RewriteRule.EMPTY;
        } else if (toMerge.size() == 1) {
            return toMerge.get(0);
        }
        Collections.reverse(toMerge);
        return toMerge.stream().reduce(this.mergeFunction).orElseThrow();
    }
}
