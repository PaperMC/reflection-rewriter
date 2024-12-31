package io.papermc.asm.rules.rename;

import data.types.rename.RenamedTestEnum;
import data.types.rename.TestAnnotation;
import io.papermc.asm.ApiVersion;
import io.papermc.asm.TransformerTest;
import io.papermc.asm.checks.TransformerCheck;
import io.papermc.asm.versioned.MappedVersionRuleFactory;
import io.papermc.asm.versioned.VersionedRuleFactory;
import java.lang.constant.ClassDesc;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static io.papermc.asm.util.DescriptorUtils.fromOwner;
import static io.papermc.asm.util.DescriptorUtils.methodDesc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RenameRuleTest {

    private static final ClassDesc TEST_ENUM = fromOwner("data/types/rename/TestEnum");

    @TransformerTest("data.rename.RenameTest")
    void testRenamerRule(final TransformerCheck check) {
        final RenameRule rule = RenameRule.builder()
            .type("data/types/rename/TestEnum", RenamedTestEnum.class)
            .editEnum(TEST_ENUM, builder -> {
                builder
                    .rename("A", "ONE")
                    .rename("B", "TWO")
                    .rename("C", "THREE")
                    .rename("FB", "FOUR")
                    .rename("Ea", "FIVE");
            })
            .annotationAttribute(TestAnnotation.class, "single", "value")
            .methodByClass(TestAnnotation.class, "single", methodDesc("()Ldata/types/rename/TestEnum;"), "value")
            .build();

        check.run(rule);
    }

    @Test
    void testVersionedRenamerRule() {
        final Map<ApiVersion, RenameRule> versions = new HashMap<>();
        versions.put(ApiVersion.ONE, RenameRule.builder()
            .methodByClass(TestAnnotation.class, "single", methodDesc("()Ldata/types/rename/TestEnum;"), "value")
            .editEnum(TEST_ENUM, builder -> builder
                .rename("A", "ONE")
            )
            .build()
        );
        versions.put(ApiVersion.THREE, RenameRule.builder()
            .methodByClass(TestAnnotation.class, "newValue", methodDesc("()Ldata/types/rename/TestEnum;"), "value")
            .annotationAttribute(TestAnnotation.class, "newValue", "value")
            .editEnum(TEST_ENUM, builder -> builder
                .rename("OTHER_A", "ONE")
                .rename("B", "TWO")
            )
            .build()
        );

        final VersionedRuleFactory factory = MappedVersionRuleFactory.mergeable(new TreeMap<>(versions));
        final RenameRule ruleOne = (RenameRule) factory.createRule(ApiVersion.ONE);
        final RenameRule ruleTwo = (RenameRule) factory.createRule(ApiVersion.TWO);
        assertEquals("value", annotationMethod("single").apply(ruleOne));
        assertEquals("value", annotationMethod("newValue").apply(ruleOne));

        assertNull(annotationMethod("single").apply(ruleTwo));
        assertEquals("value", annotationMethod("newValue").apply(ruleTwo));

        assertEquals("ONE", enumField("A").apply(ruleOne));
        assertEquals("ONE", enumField("OTHER_A").apply(ruleOne));
        assertEquals("TWO", enumField("B").apply(ruleOne));

        assertEquals("ONE", enumField("OTHER_A").apply(ruleTwo));
        assertNull(enumField("A").apply(ruleTwo));
        assertEquals("TWO", enumField("B").apply(ruleTwo));
    }

    private static Function<RenameRule, @Nullable String> annotationMethod(final String legacyName) {
        return renameRule -> renameRule.renames().get("%s.%s%s".formatted("data/types/rename/TestAnnotation", legacyName, "()Ldata/types/rename/TestEnum;"));
    }

    private static Function<RenameRule, @Nullable String> enumField(final String legacyName) {
        return renameRule -> renameRule.enumFieldRenames().get(TEST_ENUM).fieldRenames().get(legacyName);
    }
}
