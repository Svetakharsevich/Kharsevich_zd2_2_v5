package com.example.komfort

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.komfort.databinding.ActivityListScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class List_screen : AppCompatActivity() {
    private val dataList = mutableListOf<Item>()

    private lateinit var binding: ActivityListScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        printInfo()

    }
    private fun printInfo() {
        val textList = findViewById<ListView>(R.id.listView)
        dataList.clear()

        val db = MainDb.getDb(this)

        db.getDao().getAllItem().asLiveData().observe(this) { list ->
            val updatedDataList = mutableListOf<Item>()
            list.forEach { item ->
                val text = "id ${item.id}, Имя ${item.name_sity} Профиль ${item.profile} \n"
                // Создайте новый элемент Item и добавьте его в dataList
                val newItem = Item(id = item.id, name_sity = item.name_sity, profile = item.profile)
                updatedDataList.add(newItem)
            }

            // После завершения цикла, обновите dataList
            dataList.clear()
            dataList.addAll(updatedDataList)

            // Создайте или обновите адаптер
            val itemAdapter = ItemAdapter(this, dataList,
                onDeleteClick = { itemToDelete ->
                    // Здесь выполняйте удаление элемента из базы данных и обновление списка
                    val db = MainDb.getDb(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            db.getDao().deleteItem(itemToDelete)
                            // После удаления, обновите список данных
                            printInfo()
                        } catch (e: Exception) {
                            // Обработка ошибки удаления, если произошла
                        }
                    }
                },
                onEditClick = { position ->
                    // Получаю рание данные
                    val db = MainDb.getDb(this)
                    var bdNewText: Array<String> = arrayOf("id", "name_sity", "profile")
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            bdNewText= arrayOf(position.id.toString(), position.name_sity, position.profile)
                        } catch (e: Exception) {
                            // Обработка ошибки удаления, если произошла
                        }
                    }
                    //изменения через AlertDiolog
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Измените данные")

                    // Создайте контейнер LinearLayout для размещения двух EditText
                    val layout = LinearLayout(this)
                    layout.orientation = LinearLayout.VERTICAL

                    // Создайте два EditText для ввода данных
                    val EditNumber = EditText(this)
                    val EditFakt = EditText(this)

                    // Установите хинты для EditText
                    EditNumber.hint = "Введите город"
                    EditFakt.hint = "Введите достопримечательность"
                    EditNumber.setText(bdNewText[1])
                    EditFakt.setText(bdNewText[2])
                    // Добавьте EditText к контейнеру
                    layout.addView(EditNumber)
                    layout.addView(EditFakt)

                    builder.setView(layout)

                    // Установите кнопку "OK" для сохранения данных
                    builder.setPositiveButton("OK") { dialog, _ ->
                        var nameTxt = EditNumber.text.toString()
                        var profileTxt = EditFakt.text.toString()


                        //запись в бд
                        //проверка
                        if(nameTxt.isEmpty()){
                            nameTxt = bdNewText[2];
                        }
                        if(profileTxt.isEmpty()){
                            profileTxt = bdNewText[3];
                        }
                        //сама запись
                        val item = Item(bdNewText[0].toInt(), nameTxt, profileTxt)
                        CoroutineScope(Dispatchers.IO).launch {
                            // Вызовите suspend-функцию updateItem в корутине
                            db.getDao().updateItem(item)
                        }
                        printInfo()

                    }

                    // Установите кнопку "Отмена" для закрытия диалога
                    builder.setNegativeButton("Отмена") { dialog, _ ->
                        dialog.cancel()
                    }

                    val dialog = builder.create()
                    dialog.show()
                    printInfo()

                })
            textList.adapter = itemAdapter

            // Уведомите адаптер о изменениях
            itemAdapter.notifyDataSetChanged()

        }
    }
    fun AddNewButton(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите данные")

        // Создайте контейнер LinearLayout для размещения двух EditText
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        // Создайте два EditText для ввода данных
        val EditNumber = EditText(this)
        val EditFakt = EditText(this)

        // Установите хинты для EditText
        EditNumber.hint = "Введите город"
        EditFakt.hint = "Введите достопримечательность"

        // Добавьте EditText к контейнеру
        layout.addView(EditNumber)
        layout.addView(EditFakt)

        builder.setView(layout)

        // Установите кнопку "OK" для сохранения данных
        builder.setPositiveButton("OK") { dialog, _ ->
            val nameTxt = EditNumber.text.toString()
            val profileTxt = EditFakt.text.toString()

            // Здесь можно обработать введенные данные (value1 и value2)
            //запись в бд
            if(nameTxt.length == 0 || profileTxt.length == 0){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Не сохранено")
                    .setMessage("Необходимо заполнить все поля")
                    .setPositiveButton("ОК") {
                            dialog, id ->  dialog.cancel()
                    }
                builder.create()
            }
            else{
                //сама запись
                val db = MainDb.getDb(this)
                val item = Item(null,  nameTxt, profileTxt)
                Thread{
                    db.getDao().insertItem(item)
                }.start()
                dialog.dismiss()
                printInfo()
            }

        }

        // Установите кнопку "Отмена" для закрытия диалога
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()

    }
    fun ButCleaAll(view: View) {
        val db = MainDb.getDb(this)
        CoroutineScope(Dispatchers.IO).launch {
            // Очистите все таблицы в базе данных
            db.clearAllTables()

            // После удаления данных, выполните операции обновления UI на главном потоке (если необходимо)
            withContext(Dispatchers.Main) {
                // Обновление UI, если необходимо
            }
        }
        printInfo()
    }
}