package com.dakotatrauter.shoppinglist

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jsoup.Jsoup
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var numItems: TextView
    private lateinit var totalPrice: TextView
    private lateinit var addButton: FloatingActionButton
    private lateinit var listButton: Button
    private lateinit var itemName: String
    private lateinit var itemStyleNumber: String
    private lateinit var itemPrice: String
    private lateinit var itemImageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        numItems = findViewById(R.id.textView_numItems)
        totalPrice = findViewById(R.id.textView_totalPrice)
        addButton = findViewById(R.id.floatingActionButton)
        listButton = findViewById(R.id.button_itemList)

        // Get rid of Title Bar
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {}

        addButton.setOnClickListener {
            addToList()
        }

        listButton.setOnClickListener {
            openList()
        }

        // Enables Javascript on the webpage
        webView.settings.javaScriptEnabled = true

        // Allows webpage to store data on device - many images will not load without this
        webView.settings.domStorageEnabled = true

        // Set up the client for opening URL
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                retrieveHTML(url!!)
            }
        }

        // Load the URL
        webView.loadUrl("https://www.sportchek.ca/")
    }

    // Making sure total price and number of items are updated when navigating back to main activity
    override fun onResume() {
        super.onResume()
        if (ItemList.reloadNeeded) {
            updateTotalPrice()
            updateNumItems()
            ItemList.reloadNeeded = false
        }
    }

    private fun retrieveHTML(url: String) {
        thread{
            val doc = Jsoup.connect(url).get()

            val productInfo: String = doc.toString()
                .substringAfter("<script type=\"application/ld+json\">")
                .substringBefore("</script>")
                .substringAfter("\"@type\": \"Product\"")
                .substringAfter("name\": \"")
                .substringBefore("\"priceCurrency")

            // making sure user is only adding from a product page
            if (!productInfo.substringBefore("\"").equals("Sport Chek")) {
                itemName = productInfo.substringBefore("\"")
                    .replace("&#39;", "'")
                itemStyleNumber = productInfo.substringAfter("ID\": \"")
                    .substringBefore("\"")
                itemPrice = productInfo.substringAfter("price\": \"")
                    .substringBefore("\"")
                itemImageUrl = productInfo.substringAfter("image\": \"")
                    .substringBefore("\"")
            }
            else {
                itemName = ""
                itemStyleNumber = ""
                itemPrice = ""
                itemImageUrl = ""
            }
        }
    }

    private fun openList() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    private fun addToList() {
        if (itemName.isNotEmpty() && itemStyleNumber.isNotEmpty() && itemPrice.isNotEmpty()) {
            ItemList.items.add(Item(itemName, itemStyleNumber, itemPrice, itemImageUrl))
            ItemList.totalPrice += itemPrice.toDouble()
            updateTotalPrice()
            updateNumItems()
        }
    }

    private fun updateTotalPrice() {
        val integerPlaces: Int = ItemList.totalPrice.toString().indexOf('.')
        val decimalPlaces: Int = ItemList.totalPrice.toString().length - integerPlaces - 1

        if (ItemList.items.size == 0)
        {
            totalPrice.text = "Total: $0.00"
        }
        else if (decimalPlaces == 1) {
            totalPrice.text = "Total: $" + ItemList.totalPrice.toString() + "0"
        }
        else {
            totalPrice.text = "Total: $" + ItemList.totalPrice.toString()
                .substring(0, ItemList.totalPrice.toString().indexOf(".") + 3)
        }
    }

    private fun updateNumItems() {
        numItems.text = "Items: " + ItemList.items.size
    }
}
