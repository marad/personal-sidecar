package gh.marad.sidecar.template

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

class Activator : BundleActivator {
    override fun start(context: BundleContext?) {
        println("Bundle started!!")
    }

    override fun stop(context: BundleContext?) {
        println("Bundle stopped!")
    }

}