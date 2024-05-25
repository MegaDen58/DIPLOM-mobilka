package com.example.denistoptop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.denistoptop.dto.UserManager

class ToolbarButtonClickListener(private val context: Context, private val toolbar: Toolbar, private val activity: Activity) : View.OnClickListener {
    private var popupWindow: PopupWindow? = null

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cart -> {
                val intent = Intent(context, CartActivity::class.java)
                context.startActivity(intent)
                activity.overridePendingTransition(0, 0)

                clearAll()

                //Toast.makeText(context, "Нажато на корзину", Toast.LENGTH_SHORT).show()
            }
            R.id.favourites -> {
                val intent = Intent(context, FavouritesActivity::class.java)
                context.startActivity(intent)
                activity.overridePendingTransition(0, 0)

                clearAll()

                //Toast.makeText(context, "Нажато на избранное", Toast.LENGTH_SHORT).show()
            }

            R.id.history -> {
                val intent = Intent(context, HistoryActivity::class.java)
                context.startActivity(intent)
                activity.overridePendingTransition(0, 0)

                clearAll()

                //Toast.makeText(context, "Нажато на избранное", Toast.LENGTH_SHORT).show()
            }

            R.id.main -> {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                activity.overridePendingTransition(0, 0)

                clearAll()

                //Toast.makeText(context, "Нажато на основное", Toast.LENGTH_SHORT).show()
            }

            R.id.burgerMenu -> {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_menu_layout, null)

                // Инициализация элементов всплывающего меню
                val balanceText = popupView.findViewById<TextView>(R.id.balanceText)
                val replenishButton = popupView.findViewById<Button>(R.id.replenishButton)
                val catalogButton = popupView.findViewById<Button>(R.id.catalogButton)
                val cartButton = popupView.findViewById<Button>(R.id.cartButton)
                val exitButton = popupView.findViewById<Button>(R.id.exitButton)
                val createProduct = popupView.findViewById<Button>(R.id.createProduct)
                val orderButton = popupView.findViewById<Button>(R.id.orderButton)
                createProduct.isVisible = false

                val userInfo = UserManager.getUserInfo(context)
                val userRoles = userInfo?.roles?.toMutableList() ?: mutableListOf()

                if (userRoles.contains("ADMIN")) {
                    createProduct.isVisible = true
                }

                balanceText.text = "Баланс: ${UserManager.getUserInfo(this.context)!!.balance}₽"

                // Задание действий для кнопок
                replenishButton.setOnClickListener {
                    showReplenishDialog()
                }
                catalogButton.setOnClickListener {
                    // Действие при нажатии на кнопку "Каталог"
                }
                cartButton.setOnClickListener {
                    // Действие при нажатии на кнопку "Корзина"
                }
                exitButton.setOnClickListener {
                    UserManager.deleteUserInfo(context)

                    val intent = Intent(context, StartActivity::class.java)
                    context.startActivity(intent)
                    activity.overridePendingTransition(0, 0)

                    popupWindow?.dismiss() // Закрытие всплывающего окна
                    activity.finish()
                    clearAll()
                }
                createProduct.setOnClickListener{
                    val intent = Intent(context, CreateProductActivity::class.java)
                    context.startActivity(intent)
                    activity.overridePendingTransition(0, 0)

                    popupWindow?.dismiss() // Закрытие всплывающего окна
                }
                orderButton.setOnClickListener {
                    val intent = Intent(context, AdminOrdersActivity::class.java)
                    context.startActivity(intent)
                    activity.overridePendingTransition(0, 0)
                    GlobalVariables.adminButton = true
                    popupWindow?.dismiss() // Закрытие всплывающего окна
                }

                // Создание всплывающего окна
                val width = ViewGroup.LayoutParams.WRAP_CONTENT
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                val focusable = true
                popupWindow = PopupWindow(popupView, width, height, focusable)

                // Отображение всплывающего окна в указанной позиции
                popupWindow?.showAtLocation(view, Gravity.TOP or Gravity.START, 0, toolbar.height)
            }
        }
    }

    private fun clearAll() {
        val cartButton = toolbar.findViewById<ImageButton>(R.id.cart)
        cartButton.setBackgroundResource(R.drawable.cart)
        val favouritesButton = toolbar.findViewById<ImageButton>(R.id.favourites)
        favouritesButton.setBackgroundResource(R.drawable.noselectedheart)
    }
    private fun showReplenishDialog() {
        // Создаем и отображаем диалоговое окно
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_replenish, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.submit_button).setOnClickListener {
            val amountInput = dialogView.findViewById<EditText>(R.id.input_amount).text.toString()
            if (amountInput.isNotEmpty()) {
                val amount = amountInput.toIntOrNull()
                if (amount != null) {
                    // Переход на другое активити с переданными данными
                    val intent = Intent(context, WebViewActivity::class.java).apply {
                        putExtra("EXTRA_AMOUNT", amount)
                    }
                    context.startActivity(intent)
                    dialog.dismiss()
                } else {
                    // Обработка ошибки неверного ввода
                    dialogView.findViewById<EditText>(R.id.input_amount).error = "Введите корректное целое число"
                }
            } else {
                dialogView.findViewById<EditText>(R.id.input_amount).error = "Поле не должно быть пустым"
            }
        }

        dialog.show()
    }

}