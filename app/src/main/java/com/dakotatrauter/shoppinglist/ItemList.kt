package com.dakotatrauter.shoppinglist

// Singleton class to store data and share it between activities
object ItemList {
    var items: ArrayList<Item> = arrayListOf()
    var totalPrice: Double = 0.00
    var reloadNeeded = false;
}
