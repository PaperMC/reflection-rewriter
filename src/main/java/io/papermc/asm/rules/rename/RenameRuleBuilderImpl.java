package io.papermc.asm.rules.rename;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static io.papermc.asm.util.DescriptorUtils.toOwner;

final class RenameRuleBuilderImpl implements RenameRuleBuilder {

    RenameRuleBuilderImpl() {
    }

    final Map<String, String> mappings = new HashMap<>();
    final Map<ClassDesc, EnumRenamer> enumValueOfFieldRenames = new HashMap<>();

    @Override
    public RenameRuleBuilder method(final ClassDesc owner, final String legacyMethodName, final MethodTypeDesc methodDesc, final String newMethodName) {
        this.mappings.put("%s.%s%s".formatted(toOwner(owner), legacyMethodName, methodDesc.descriptorString()), newMethodName);
        return this;
    }

    @Override
    public RenameRuleBuilder field(final ClassDesc owner, final String legacyFieldName, final String newFieldName) {
        this.mappings.put("%s.%s".formatted(toOwner(owner), legacyFieldName), newFieldName);
        return this;
    }

    @Override
    public RenameRuleBuilder annotationAttribute(final ClassDesc owner, final String legacyName, final String newName) {
        final String ownerDescriptor = owner.descriptorString();
        if (!ownerDescriptor.startsWith("L") || !ownerDescriptor.endsWith(";")) {
            throw new IllegalArgumentException("Invalid owner descriptor: %s".formatted(ownerDescriptor));
        }
        // for some reason the remapper wants the Lpkg/name; format, but just for annotation attributes
        this.mappings.put("%s.%s".formatted(ownerDescriptor, legacyName), newName);
        return this;
    }

    @Override
    public RenameRuleBuilder type(final String legacyType, final ClassDesc newType) {
        this.mappings.put(legacyType, toOwner(newType));
        return this;
    }

    @Override
    public RenameRuleBuilder editEnum(final ClassDesc enumTypeDesc, final Consumer<EnumRenameBuilder> renamer) {
        final EnumRenameBuilder enumRenamerBuilder = new EnumRenameBuilder(enumTypeDesc);
        renamer.accept(enumRenamerBuilder);
        final EnumRenamer enumRenamer = enumRenamerBuilder.build();
        enumRenamer.fieldRenames().forEach((legacyName, newName) -> {
            this.field(enumTypeDesc, legacyName, newName);
        });
        this.enumValueOfFieldRenames.put(enumTypeDesc, enumRenamer);
        return this;
    }

    @Override
    public RenameRule build() {
        return new RenameRule(this.mappings, this.enumValueOfFieldRenames);
    }
}
