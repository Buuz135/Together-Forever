package com.buuz135.togetherforever.utils;

import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotationHelper {

    public static List<Class<?>> getAnnotatedClasses(ASMDataTable data, Class<? extends Annotation> annotationClass) {
        return data.getAll(annotationClass.getCanonicalName()).stream().map(data1 -> {
            try {
                return Class.forName(data1.getClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
