package io.papermc.asm.rules.methods;

import data.methods.Methods;
import data.types.hierarchy.Entity;
import data.types.hierarchy.Player;
import io.papermc.asm.ApiVersion;
import io.papermc.asm.TransformerTest;
import io.papermc.asm.checks.TransformerCheck;
import io.papermc.asm.rules.RewriteRule;
import io.papermc.asm.rules.builder.matcher.method.MethodTypeMatcherBuilder;
import io.papermc.asm.rules.builder.matcher.method.targeted.TargetedMethodMatcher;
import io.papermc.asm.rules.method.params.SuperTypeParamRewrite;
import io.papermc.asm.rules.method.returns.SubTypeReturnRewrite;
import io.papermc.asm.versioned.VersionedRuleFactory;
import io.papermc.asm.versioned.VersionedTester;
import io.papermc.asm.versioned.matcher.VersionedMatcher;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MethodRewritesTest {

    static final ClassDesc PLAYER = Player.class.describeConstable().orElseThrow();
    static final ClassDesc ENTITY = Entity.class.describeConstable().orElseThrow();

    @SuppressWarnings("Convert2MethodRef")
    @TransformerTest("data.methods.inplace.SuperTypeParamUser")
    void testSuperTypeParam(final TransformerCheck check) {
        final RewriteRule rule = RewriteRule.forOwnerClass(Methods.class, builder -> {
            builder.changeParamToSuper(
                Entity.class,
                TargetedMethodMatcher.builder()
                    .match("consume", b -> b.virtual())
                    .match("consumeStatic", b -> b.statik())
                    .targetParam(PLAYER)
                    .build()
            );
        });
        check.run(rule);
    }

    @Test
    void testVersionedSuperTypeParam() {
        final VersionedRuleFactory factory = VersionedRuleFactory.forOwnerClass(String.class, builder -> {
            final TargetedMethodMatcher method1 = TargetedMethodMatcher.builder()
                .match("method1").targetParam(ConstantDescs.CD_int).build();
            final TargetedMethodMatcher method3 = TargetedMethodMatcher.builder()
                .match("method1").targetParam(ConstantDescs.CD_long).build();
            builder.changeParamToSuper(
                String.class,
                VersionedMatcher.targetedMethodBuilder()
                    .with(ApiVersion.ONE, method1)
                    .with(ApiVersion.THREE, method3)
                    .build()
            );
        });

        final VersionedTester tester = new VersionedTester(factory, ApiVersion.ALL_VERSIONS);
        tester.test(SuperTypeParamRewrite::oldParamType, Map.of(
            ApiVersion.ONE, ConstantDescs.CD_int,
            ApiVersion.THREE, ConstantDescs.CD_long
        ));
    }

    @TransformerTest("data.methods.inplace.SubTypeReturnUser")
    void testSubTypeReturn(final TransformerCheck check) {
        final RewriteRule rule = RewriteRule.forOwnerClass(Methods.class, builder -> {
            builder.changeReturnTypeToSub(
                Player.class,
                TargetedMethodMatcher.builder()
                    .match("get", MethodTypeMatcherBuilder::virtual)
                    .match("getStatic", MethodTypeMatcherBuilder::statik)
                    .targetReturn(ENTITY)
                    .build()
            );
        });
        check.run(rule);
    }

    @Test
    void testVersionedSubTypeReturn() {
        final VersionedRuleFactory factory = VersionedRuleFactory.forOwnerClass(String.class, builder -> {
            final TargetedMethodMatcher method1 = TargetedMethodMatcher.builder()
                .match("method1").targetReturn(ConstantDescs.CD_int).build();
            final TargetedMethodMatcher method3 = TargetedMethodMatcher.builder()
                .match("method1").targetReturn(ConstantDescs.CD_long).build();
            builder.changeReturnTypeToSub(
                String.class,
                VersionedMatcher.targetedMethodBuilder()
                    .with(ApiVersion.ONE, method1)
                    .with(ApiVersion.THREE, method3)
                    .build()
            );
        });

        final VersionedTester tester = new VersionedTester(factory, ApiVersion.ALL_VERSIONS);
        tester.test(SubTypeReturnRewrite::oldReturnType, Map.of(
            ApiVersion.ONE, ConstantDescs.CD_int,
            ApiVersion.THREE, ConstantDescs.CD_long
        ));
    }
}
