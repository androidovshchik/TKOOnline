package ru.iqsolution.tkoonline.services

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

val serviceModule = Kodein.Module("service") {

    bind<AdminManager>() with provider {
        AdminManager(instance())
    }
}