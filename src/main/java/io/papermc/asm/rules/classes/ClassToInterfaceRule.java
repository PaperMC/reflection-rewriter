package io.papermc.asm.rules.classes;

import io.papermc.asm.ClassProcessingContext;
import io.papermc.asm.rules.RewriteRule;
import io.papermc.asm.rules.method.OwnableMethodRewriteRule;
import io.papermc.asm.rules.method.StaticRewrite;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import static io.papermc.asm.util.DescriptorUtils.toOwner;
import static io.papermc.asm.util.OpcodeUtils.isStatic;

public class ClassToInterfaceRule implements RewriteRule.Delegate {

    private final ClassDesc owner;
    private final @Nullable ClassDesc redirectExtension;
    private final RewriteRule rule;

    public ClassToInterfaceRule(final ClassDesc owner, final @Nullable ClassDesc redirectExtension) {
        this.owner = owner;
        this.redirectExtension = redirectExtension;
        if (redirectExtension != null) {
            this.rule = RewriteRule.chain(new ExtensionRewriter(redirectExtension), new MethodRewriter());
        } else {
            this.rule = new MethodRewriter();
        }
    }

    @Override
    public RewriteRule delegate() {
        return this.rule;
    }

    final class MethodRewriter implements OwnableMethodRewriteRule {

        @Override
        public Set<ClassDesc> owners() {
            return Set.of(ClassToInterfaceRule.this.owner);
        }

        @Override
        public Rewrite<?> rewrite(final ClassProcessingContext context, final boolean isInvokeDynamic, final int opcode, final ClassDesc owner, final String name, final MethodTypeDesc descriptor, final boolean isInterface) {
            if (isStatic(opcode, isInvokeDynamic)) {
                return new RewriteSingle(opcode, owner, name, descriptor, true, isInvokeDynamic);
            }
            final int newOpcode;
            if (isInvokeDynamic && opcode == Opcodes.H_INVOKEVIRTUAL) {
                newOpcode = Opcodes.H_INVOKEINTERFACE;
            } else if (!isInvokeDynamic && opcode == Opcodes.INVOKEVIRTUAL) {
                newOpcode = Opcodes.INVOKEINTERFACE;
            } else if (ClassToInterfaceRule.this.redirectExtension != null && opcode == Opcodes.INVOKESPECIAL && StaticRewrite.CONSTRUCTOR_METHOD_NAME.equals(name)) {
                return new RewriteSingle(opcode, ClassToInterfaceRule.this.redirectExtension, name, descriptor, isInterface, isInvokeDynamic);
            } else {
                throw new IllegalStateException("Unexpected opcode: " + opcode + ". There should only be invokevirtual or h_invokevirtual opcodes here.");
            }
            return new RewriteSingle(newOpcode, owner, name, descriptor, true, isInvokeDynamic);
        }
    }

    final class ExtensionRewriter implements RewriteRule {

        private final ClassDesc replacement;

        ExtensionRewriter(final ClassDesc replacement) {
            this.replacement = replacement;
        }

        @Override
        public ClassVisitor createVisitor(final int api, final ClassVisitor parent, final ClassProcessingContext context) {
            return new ClassVisitor(api, parent) {
                @Override
                public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                    if (toOwner(ClassToInterfaceRule.this.owner).equals(superName)) {
                        super.visit(version, access, name, signature, toOwner(ExtensionRewriter.this.replacement), interfaces);
                    } else {
                        super.visit(version, access, name, signature, superName, interfaces);
                    }
                }
            };
        }
    }
}