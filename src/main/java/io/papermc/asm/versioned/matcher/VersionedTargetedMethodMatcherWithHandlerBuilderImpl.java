package io.papermc.asm.versioned.matcher;

import io.papermc.asm.rules.builder.matcher.method.targeted.TargetedMethodMatcher;
import io.papermc.asm.versioned.ApiVersion;
import java.lang.reflect.Method;

public final class VersionedTargetedMethodMatcherWithHandlerBuilderImpl
    extends VersionedMatcherBuilderImpl<TargetedMethodMatcherWithHandler>
    implements VersionedTargetedMethodMatcherWithHandlerBuilder {

    @Override
    public VersionedTargetedMethodMatcherWithHandlerBuilder with(final ApiVersion apiVersion, final TargetedMethodMatcherWithHandler context) {
        return (VersionedTargetedMethodMatcherWithHandlerBuilder) super.with(apiVersion, context);
    }

    @Override
    public VersionedTargetedMethodMatcherWithHandlerBuilder with(final ApiVersion apiVersion, final TargetedMethodMatcher matcher, final Method staticHandler) {
        return this.with(apiVersion, new TargetedMethodMatcherWithHandler(matcher, staticHandler));
    }
}
