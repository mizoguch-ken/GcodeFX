/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.soem;

import jnr.ffi.Struct;

/**
 *
 * @author mizoguch-ken
 */
public interface SoemOsal {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public class ec_timet extends Struct {

        public final Unsigned32 sec;
        /*< Seconds elapsed since the Epoch (Jan 1, 1970) */
        public final Unsigned32 usec;

        /*< Microseconds elapsed since last second boundary */
        public ec_timet(jnr.ffi.Runtime runtime) {
            super(runtime);

            sec = new Unsigned32();
            usec = new Unsigned32();
        }
    }

    public class osal_timert extends Struct {

        public final ec_timet stop_time;

        public osal_timert(jnr.ffi.Runtime runtime) {
            super(runtime);

            stop_time = inner(new ec_timet(runtime));
        }
    }
}
