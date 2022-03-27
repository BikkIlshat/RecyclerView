package com.example.recyclerview

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recyclerview.databinding.ItemUserBinding
import com.example.recyclerview.model.User

interface UserActionListener {

  fun onUserMove(user: User, moveBy: Int) // перемещение вверх или вниз

  fun onUserDelete(user: User) // удаление пользователя

  fun onUserDetails(user: User) // нажатие на какой-то элемент списка
}

class UsersAdapter(
  private val actionListener: UserActionListener
) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), View.OnClickListener {

  // список пользователей (по умолчанию будет пустым)
  var users: List<User> = emptyList()
    // для того чтобы мы смогли уведомить при каждом изминении значеничя users: List<User>
    // уведомить RecyclerView о том что нужно себя обновить
    set(newValue) {
      field = newValue
      notifyDataSetChanged()
    }


  override fun onClick(view: View) { // <- тут view никогда не равна null
    // тут будем пробовать вытащить пользователя
    val user = view.tag as User // <- так как тут вытаскивыаем пользователя из tag
    //это значит что этого пользователч где то нужно в этот tag положить (это делается в методе onBindViewHolder)
  when(view.id) {
    R.id.moreImageViewButton -> {
        showPopupMenu(view)
    }
    else -> {
      actionListener.onUserDetails(user)
    }
  }

  }


  //адаптер должен знать сколько элементов в списке
  // метод возвращает кол-во этих элементов
  override fun getItemCount(): Int = users.size


  //используется тогда когда RecyclerView хочет создать новый элемент списка
  /*
  в качестве парамента приходит parent откуда можем Context выдернуть
  viewType - используется тогда когда будет более 1 элемента в списке
   */
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = ItemUserBinding.inflate(inflater, parent, false)

    binding.root.setOnClickListener(this) // слушатель на нажатие элемента списка
    binding.moreImageViewButton.setOnClickListener(this) // слушатель на нажатие кнопки more
    //setOnClickListener(this)        this - потому что наш адаптер реалезует интерфейс  View.OnClickListener
    return UsersViewHolder(binding)
  }

  /*
  Используется для тогда чтобы обновить элемент списка
  position - по которому можем вытенуть элемент списка нашего пользователя
  holder - внутри которго находятся все наши вьюшки (item_user) соответственно мы их можем обновить
   */
  override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
    val user = users[position] // теперь этого пользователч нужжно отрисовать с помощью  holder
    with(holder.binding) {
      // на всех компонентах на которые пользователь может нажать мы должны этот "tag" проинициализировать
      holder.itemView.tag = user // пользователь может нажать на самом элементе списка
      moreImageViewButton.tag = user // пользователь может нажать на кнопку more
      userNameTextView.text = user.name
      userCompanyTextView.text = user.company
      if (user.photo.isNotBlank()) {
        Glide.with(photoImageView.context)
          .load(user.photo)
          .circleCrop()
          .placeholder(R.drawable.ic_user_avatar)
          .error(R.drawable.ic_user_avatar)
          .into(photoImageView)
      } else { // если у пользователя нет аватарки
        Glide.with(photoImageView.context).clear(photoImageView)
        photoImageView.setImageResource(R.drawable.ic_user_avatar)
      }
    }
  }

  private fun showPopupMenu(view: View) {
    val popupMenu = PopupMenu(view.context, view) // создали сам объект PopupMenu
    val context = view.context
    val user = view.tag as User
    val position = users.indexOfFirst { it.id == user.id }

    // Добавим действия в popupMenu типичный способ  добавления это  add
    popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
      isEnabled = position > 0
    }
    popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
      isEnabled = position < users.size - 1
    }
    popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove))

    // пункт меню на который пользователь нажал
    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        ID_MOVE_UP -> {
          actionListener.onUserMove(user, -1)
        }
        ID_MOVE_DOWN -> {
          actionListener.onUserMove(user, 1)
        }
        ID_REMOVE -> {
          actionListener.onUserDelete(user)
        }
      }
      return@setOnMenuItemClickListener true
    }
    popupMenu.show()
  }


  class UsersViewHolder(
    val binding: ItemUserBinding,
  ) : RecyclerView.ViewHolder(binding.root)

  // у каждого действия должны быть уникальные идентификаторы
  companion object {
    private const val ID_MOVE_UP = 1
    private const val ID_MOVE_DOWN = 2
    private const val ID_REMOVE = 3
  }
}