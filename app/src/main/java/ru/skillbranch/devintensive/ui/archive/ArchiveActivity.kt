package ru.skillbranch.devintensive.ui.archive


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ArchiveVievModel


class ArchiveActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: ArchiveVievModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        initToolbar()
        initViews()
        initViewModel()
    }


    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }
    private fun initViews() {
        title = "Архив чатов"
        chatAdapter = ChatAdapter{
            val snackbar = Snackbar.make(rv_archive_list, "Click on ${it.title}", Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(Utils.getColor(this,R.attr.colorSnackBarText))
            snackbarView.setBackgroundColor(Utils.getColor(this,R.attr.colorSnackBar))
            snackbar.show()
        }
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val touchCallback = ChatItemTouchHelperCallback(chatAdapter){
            val id = it.id
            viewModel.restoreFromArchive(it.id)
            val snackbar = Snackbar.make(rv_archive_list, "Восстановить чат ${it.title} из архива?", Snackbar.LENGTH_LONG)
                .setAction("ОТМЕНА") {viewModel.addToArchive(id)}
            val snackbarView = snackbar.view
            val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(Utils.getColor(this,R.attr.colorSnackBarText))
            snackbarView.setBackgroundColor(Utils.getColor(this,R.attr.colorSnackBar))
            snackbar.show()
        }
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_archive_list)

        with(rv_archive_list){
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            addItemDecoration(divider)
        }

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ArchiveVievModel::class.java)
        viewModel.getChatData().observe(this, Observer {chatAdapter.updateData(it) })
    }
}
