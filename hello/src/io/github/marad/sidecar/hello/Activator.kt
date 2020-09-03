package io.github.marad.sidecar.hello

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

class Activator : BundleActivator {
    override fun start(context: BundleContext?) {
        println("Bundle installed!")
    }

    override fun stop(context: BundleContext?) {
    }

}