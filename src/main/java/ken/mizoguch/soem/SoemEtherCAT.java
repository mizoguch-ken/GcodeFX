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
public interface SoemEtherCAT {

    public static final int EC_NSEC_PER_SEC = 1000000000;
    public static final int EC_TIMEOUTMON = 500;

    /**
     * Context structure , referenced by all ecx functions
     */
    public class ecx_parcelt extends Struct {

        private final Pointer _context;
        public final SoemEtherCATMain.ecx_contextt context;
        public final Pointer thread;
        private final Pointer _wkc;
        public final SoemLibrary.Int32 wkc;
        private final Pointer _cycletime;
        public final SoemLibrary.Int64 cycletime;
        private final Pointer _dorun;
        public final SoemLibrary.Bool dorun;
        private final Pointer _isprocess;
        public final SoemLibrary.Bool isprocess;

        public ecx_parcelt(jnr.ffi.Runtime runtime) {
            super(runtime);

            _context = new Pointer();
            context = new SoemEtherCATMain.ecx_contextt(runtime);
            thread = new Pointer();
            _wkc = new Pointer();
            wkc = new SoemLibrary.Int32(runtime);
            _cycletime = new Pointer();
            cycletime = new SoemLibrary.Int64(runtime);
            _dorun = new Pointer();
            dorun = new SoemLibrary.Bool(runtime);
            _isprocess = new Pointer();
            isprocess = new SoemLibrary.Bool(runtime);
        }

        public ecx_parcelt register() {
            context.useMemory(_context.get());
            wkc.useMemory(_wkc.get());
            cycletime.useMemory(_cycletime.get());
            dorun.useMemory(_dorun.get());
            isprocess.useMemory(_isprocess.get());
            return this;
        }
    }
}
