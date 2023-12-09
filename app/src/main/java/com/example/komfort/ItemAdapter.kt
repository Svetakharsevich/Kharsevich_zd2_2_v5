package com.example.komfort

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

class ItemAdapter(
    context: Context,
    private val itemList: List<Item>,
    private val onDeleteClick: (Item) -> Unit,
    private val onEditClick: (Item) -> Unit
) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.activity_static_search, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.titleTextView),
                view.findViewById(R.id.descriptionTextView),
                view.findViewById(R.id.editButton),
                view.findViewById(R.id.deleteButton)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = itemList[position]
        holder.titleTextView.text = item.name_sity // Отображаем "Имя" в заголовке
        holder.descriptionTextView.text = item.profile // отображаем профиль

        // Обработка нажатий на кнопку "Редактировать"
        holder.editButton.setOnClickListener {
            onEditClick(item)
        }

        // Обработка нажатий на кнопку "Удалить"
        holder.deleteButton.setOnClickListener {
            if (itemList.isNotEmpty()) {
                onDeleteClick(item)
            }
        }
        return view
    }

    private class ViewHolder(
        val titleTextView: TextView,
        val descriptionTextView: TextView,
        val editButton: ImageButton,
        val deleteButton: ImageButton
    )
}