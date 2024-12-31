package data.rename;

import data.types.rename.TestAnnotation;
import data.types.rename.TestEnum;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

@SuppressWarnings("unused")
@TestAnnotation(single = TestEnum.A, multiple = {TestEnum.A, TestEnum.B, TestEnum.C}, clazz = TestEnum.class)
public final class RenameTest {

    @TestAnnotation(single = TestEnum.A, multiple = {TestEnum.A, TestEnum.B, TestEnum.C}, clazz = TestEnum.class)
    public static void entry() throws ReflectiveOperationException {
        checkAnnotation(RenameTest.class);
        checkAnnotation(RenameTest.class.getDeclaredMethod("entry"));
        checkAnnotation(RenameTest.class.getDeclaredField("field"));
        checkAnnotation(RenameTest.class.getDeclaredField("otherField"));

        final TestEnum a = TestEnum.valueOf("A");
        System.out.println(a);
        final TestEnum fb = TestEnum.valueOf("FB");
        System.out.println(fb);
        final TestEnum ea = TestEnum.valueOf("Ea");
        System.out.println(ea);
    }

    private static void checkAnnotation(final AnnotatedElement element) {
        final TestAnnotation annotation = element.getAnnotation(TestAnnotation.class);
        System.out.println(annotation.single());
        System.out.println(Arrays.toString(annotation.multiple()));
        System.out.println(annotation.clazz());
    }

    @TestAnnotation(single = TestEnum.A, clazz = TestEnum.class)
    public static final String field = "";

    @TestAnnotation(single = TestEnum.A, multiple = {TestEnum.A, TestEnum.B, TestEnum.C})
    public static final String otherField = "";
}
