package ru.easygraphics.settingwindow

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.databinding.FragmentSettingBinding
import ru.easygraphics.helpers.AlertDialogs
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.SettingState

class SettingFragment :
    BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate)
{
    private val scope = getKoin().createScope<SettingFragment>()
    private val viewModel: SettingViewModel = scope.get(qualifier = named(Scopes.SETTING_VIEW_MODEL))
    private var alertDialogLoading: AlertDialog? = null

    //запуск окна проверки разрешений для импорта данных
    private val requestImportPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) { selectFileImport.launch(arrayOf("application/json")) }
        }

    //выбор файла для импорта
    private val selectFileImport =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            viewModel.importGraphics(requireActivity().contentResolver, uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(R.string.title_setting)

        viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        //формируем список настроек
        viewModel.getItemSettings(requireContext())
    }

    /**
     * Обработка состояний.
     * @param state полученное состояние от viewModel.
     */
    private fun renderData(state: BaseState) {
        when (state) {
            //загрузка основной информации
            is BaseState.LoadingRoot -> visibleData(isLoad = true)

            //получен список настроек
            is SettingState.Success -> {
                showSettingItems(state.data)
            }

            //запущен процесс импорта или экспорта
            is SettingState.ProcessImportExport -> inflater?.let {
                alertDialogLoading = AlertDialogs.createLoading(requireContext(), it)
                alertDialogLoading?.show()
            }

            //импорт завершен
            is SettingState.ImportSuccess -> {
                AlertDialogs.createMessage(requireContext(), getString(R.string.import_success)).show()
                alertDialogLoading?.cancel()
            }

            //какая-то ошибка
            is BaseState.ErrorState -> {
                AlertDialogs.createMessage(requireContext(), state.text).show()
                alertDialogLoading?.cancel()
            }
        }
    }

    /**
     * Показать сформированный список настроек.
     * @param data [List<[SettingItemType]>] список элементов настроек.
     * */
    private fun showSettingItems(data: List<SettingItemType>) {
        val adapterData = SettingAdapter(data, object: SettingAdapterListener {
            override fun exportGraphics() {  }

            //Открыть файл для загрузки данных
            override fun importGraphics() {
                //проверяем есть ли права на чтение файлов
                requestImportPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        })

        with(binding.settingList) {
            adapter = adapterData
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            visibleData(isLoad = false)
        }
    }

    /**
     * Показать или скрыть данные на экране.
     * @param isLoad true - нужно показать крутилку, а все остальное скрыть, false - наоборот
     */
    private fun visibleData(isLoad: Boolean) {
        with(binding) {
            if (isLoad) {
                progressBar.visibility = View.VISIBLE
                settingList.visibility = View.GONE
            }
            else {
                progressBar.visibility = View.GONE
                settingList.visibility = View.VISIBLE

            }
        }
    }
}