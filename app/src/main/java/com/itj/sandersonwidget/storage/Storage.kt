package com.itj.sandersonwidget.storage

interface Storage {

    fun store(items: List<String>)

    fun retrieve(): List<String>
}
