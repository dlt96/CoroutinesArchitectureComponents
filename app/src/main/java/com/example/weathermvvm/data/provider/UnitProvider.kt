package com.example.weathermvvm.data.provider

import com.example.weathermvvm.internal.UnitSystem

interface UnitProvider {
    fun getUnitSystem(): UnitSystem

}