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

class ToolbarButtonClickListener(private val context: Context, private val toolbar: Toolbar, private val activity: Activity) : View.OnClickListener {
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

                balanceText.setText("Баланс: ${GlobalVariables.user?.balance}₽")

                // Задание действий для кнопок
                replenishButton.setOnClickListener {
                    // Действие при нажатии на кнопку пополнения баланса
                }
                catalogButton.setOnClickListener {
                    // Действие при нажатии на кнопку "Каталог"
                }
                cartButton.setOnClickListener {
                    // Действие при нажатии на кнопку "Корзина"
                }

                // Создание всплывающего окна
                val width = ViewGroup.LayoutParams.WRAP_CONTENT
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                val focusable = true
                val popupWindow = PopupWindow(popupView, width, height, focusable)

                // Отображение всплывающего окна в указанной позиции
                popupWindow.showAtLocation(view, Gravity.TOP or Gravity.START, 0, toolbar.height)
            }
        }
    }

    private fun clearAll() {
        val cartButton = toolbar.findViewById<ImageButton>(R.id.cart)
        cartButton.setBackgroundResource(R.drawable.cart)
        val favouritesButton = toolbar.findViewById<ImageButton>(R.id.favourites)
        favouritesButton.setBackgroundResource(R.drawable.noselectedheart)
    }
}