package com.example.tiendaza.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendaza.data.model.Publicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartItem(
    val publicacion: Publicacion,
    var cantidad: Int = 1
)

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0)
    val totalPrice: StateFlow<Int> = _totalPrice.asStateFlow()

    fun addToCart(publicacion: Publicacion) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.publicacion.id == publicacion.id }

        if (existingItem != null) {
            existingItem.cantidad++
        } else {
            currentItems.add(CartItem(publicacion, 1))
        }

        _cartItems.value = currentItems
        calculateTotal()
    }

    fun removeFromCart(publicacionId: Long) {
        _cartItems.value = _cartItems.value.filter { it.publicacion.id != publicacionId }
        calculateTotal()
    }

    fun updateQuantity(publicacionId: Long, cantidad: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val item = currentItems.find { it.publicacion.id == publicacionId }

        if (item != null) {
            if (cantidad <= 0) {
                currentItems.remove(item)
            } else {
                item.cantidad = cantidad
            }
        }

        _cartItems.value = currentItems
        calculateTotal()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _totalPrice.value = 0
    }

    private fun calculateTotal() {
        _totalPrice.value = _cartItems.value.sumOf { it.publicacion.precio * it.cantidad }
    }

    fun getItemCount(): Int {
        return _cartItems.value.sumOf { it.cantidad }
    }
}