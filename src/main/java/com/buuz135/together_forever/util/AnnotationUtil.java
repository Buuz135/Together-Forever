/*
 * This file is part of Titanium
 * Copyright (C) 2024, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.buuz135.together_forever.util;


import com.buuz135.together_forever.TogetherForever;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationUtil {

    public static List<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
        List<Class<?>> classList = new ArrayList<>();
        Type type = Type.getType(annotation);
        for (ModFileScanData allScanDatum : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData allScanDatumAnnotation : allScanDatum.getAnnotations()) {
                if (Objects.equals(allScanDatumAnnotation.annotationType(), type)) {
                    try {
                        classList.add(Class.forName(allScanDatumAnnotation.memberName()));
                    } catch (ClassNotFoundException e) {
                        TogetherForever.LOGGER.error(e.toString());
                    }
                }
            }
        }
        return classList;
    }

}
