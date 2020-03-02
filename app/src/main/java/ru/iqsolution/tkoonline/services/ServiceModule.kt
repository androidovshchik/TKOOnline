package ru.iqsolution.tkoonline.services

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val serviceModule = Kodein.Module("service") {

    bind<AdminManager>() with singleton {
        AdminManager(instance())
    }
}