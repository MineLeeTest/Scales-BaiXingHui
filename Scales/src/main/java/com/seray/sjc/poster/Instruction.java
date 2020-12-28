package com.seray.sjc.poster;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author licheng
 * @since 2019/6/5 13:50
 */
@StringDef(
        {
                Instruction.DISPLAY_ON,
                Instruction.DISPLAY_OFF,
                Instruction.DISPLAY_SYNC,
                Instruction.DISPLAY_ASYNC,
        }
)
@Retention(RetentionPolicy.RUNTIME)
public @interface Instruction {

    String DISPLAY_ON = "DISPLAY-ON";

    String DISPLAY_OFF = "DISPLAY-OFF";

    String DISPLAY_SYNC = "DISPLAY_SYNC";

    String DISPLAY_ASYNC = "DISPLAY-ASYNC";

}
