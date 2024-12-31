package io.papermc.asm.versioned;

import io.papermc.asm.ApiVersion;
import io.papermc.asm.rules.RewriteRule;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class VersionedTester {

    private final VersionedRuleFactory factory;
    private final List<ApiVersion> versions;

    public VersionedTester(final VersionedRuleFactory factory, final List<ApiVersion> versions) {
        this.factory = factory;
        this.versions = versions;
    }

    @SuppressWarnings("unchecked")
    public <R extends RewriteRule, C> void test(final Function<R, C> comparisonGetter, final Map<ApiVersion, C> expectedValues) {
        final Map<ApiVersion, C> sortedExpectedValues = new TreeMap<>(expectedValues);
        if (sortedExpectedValues.size() + 1 > this.versions.size()) {
            throw new IllegalArgumentException("Expected values size does not match versions size");
        }
        final Iterator<Map.Entry<ApiVersion, C>> expectedEntryIter = sortedExpectedValues.entrySet().iterator();
        Map.Entry<ApiVersion, C> current = expectedEntryIter.next();
        for (int i = 0; i < this.versions.size() - 1; i++) {
            final ApiVersion version = this.versions.get(i);
            final R rule = (R) this.factory.createRule(version);
            if (version.isNewerThan(current.getKey())) {
                current = expectedEntryIter.next();
            }
            final C expected = current.getValue();
            final C actual = comparisonGetter.apply(rule);
            assertEquals(expected, actual, "Expected " + expected + " but got " + actual + " for version " + version);
        }
        assertFalse(expectedEntryIter.hasNext());
        final RewriteRule rule = this.factory.createRule(this.versions.get(this.versions.size() - 1));
        assertEquals(RewriteRule.EMPTY, rule, "Expected empty rule for version " + this.versions.get(this.versions.size() - 1));
    }
}
