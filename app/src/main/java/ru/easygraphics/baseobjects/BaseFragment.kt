package ru.easygraphics.baseobjects

import android.os.Bundle
import android.view.*
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.github.terrakok.cicerone.Router
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.MainActivity
import ru.easygraphics.R
import ru.easygraphics.databinding.FragmentChartsListBinding
import ru.easygraphics.databinding.FragmentGraphicBinding
import ru.easygraphics.databinding.FragmentSettingBinding
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.helpers.di.EmptyModule
import ru.easygraphics.mainWindow.ChartListScreen
import ru.easygraphics.settingwindow.SettingScreen

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment() {

    private val scope = getKoin().createScope<EmptyModule>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))

    private var _binding: VB? = null
    val binding get() = _binding!!
    private var menuCreated = false
    var inflater: LayoutInflater? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!menuCreated) {
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        _binding = inflate.invoke(inflater, container, false)
        showButtonBack(true)

        initAfterCreate()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //сохраняем отметку того, что меню уже проинициализировано
        outState.putBoolean(App.KEY_MENU_CREATED, menuCreated)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            //восстанавливаем отметку того, что меню уже проинициализировано
            menuCreated = it.getBoolean(App.KEY_MENU_CREATED)
        }
    }

    /**
     * Создать меню в ActionBar справа сверху. Если не нужно, то во фрагменте переопределить
     * данный метод с пустой реализацией.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.actionbar_menu_fragment, menu)
        afterCreateMenu(menu)
    }

    //обработка меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //кнопка сохранения
            R.id.menu_value_save -> saveData()

            //кнопка домой
            R.id.menu_home -> router.newRootScreen(ChartListScreen())

            //кнопка настроек
            R.id.menu_setting -> router.navigateTo(SettingScreen())

            //кнопка отмены или назад
            android.R.id.home -> router.exit()
        }
        return super.onOptionsItemSelected(item)
    }

    /** Скрыть все пункты меню */
    fun hideAllActionBarMenu() {

    }

    /** Дополнительная инициализация данных, которая запускается в [onCreateView] */
    open fun initAfterCreate() {
    }

    /** Реализация сохранения данных. В каждом фрагменте переопределять под свои нужды */
    open fun saveData() {
    }

    /**
     * Реализация кнопки отмены/назад слева сверху. Если во фрагменте она не нужна,
     * то переопрделить с параметром false
     */
    open fun showButtonBack(visible: Boolean) {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(visible)
    }

    /**
     * Задать заголовок окна
     * @param title Новое название окна
     */
    fun setTitle(title: String) {
        (activity as MainActivity).supportActionBar?.title = title
    }

    /**
     * Задать заголовок окна
     * @param resId Ссылка на строковый ресурс
     */
    fun setTitle(@StringRes resId: Int) {
        (activity as MainActivity).supportActionBar?.title = getString(resId)
    }

    /**
     * Дополнительная обработка меню.
     * @param menu [Menu] сформированное меню в ActionBar
     */
    private fun afterCreateMenu(menu: Menu) {
        //для всех окон по умолчанию скрываем меню перехода к настройкам
        menu.findItem(R.id.menu_setting).isVisible = false

        //меню для главного экрана
        if (_binding is FragmentChartsListBinding) {
            menu.findItem(R.id.menu_setting).isVisible = true
            menu.findItem(R.id.menu_value_save).isVisible = false
            menu.findItem(R.id.menu_home).isVisible = false
        }

        //меню для окна с графиком
        if (_binding is FragmentGraphicBinding) {
            menu.findItem(R.id.menu_value_save).isVisible = false
        }

        //меню для окна с настройками
        if (_binding is FragmentSettingBinding) {
            menu.findItem(R.id.menu_value_save).isVisible = false
            menu.findItem(R.id.menu_home).isVisible = false
        }
    }
}