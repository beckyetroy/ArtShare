package org.wit.artshare.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.android.synthetic.main.activity_art_list.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivityForResult
import org.wit.artshare.R
import org.wit.artshare.databinding.ActivityArtListBinding
import org.wit.artshare.main.MainApp
import org.wit.artshare.models.ArtModel

class ArtListActivity : AppCompatActivity(), MovieListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityArtListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art_list)
        app = application as MainApp

        //enable action bar and set title
        toolbar.title = title
        setSupportActionBar(toolbar)

        //layout and populate for display
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        loadArts()

        registerRefreshCallback()


        movie_search.setOnQueryTextListener(object: SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    //show the arts found using the search function (see ArtAdapter)
                    showArts(app.arts.search(newText))
                }
                else {

                }
                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //display menu
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //start ArtActivity
            R.id.item_add -> startActivityForResult<ArtActivity>(0)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMovieClick(art: ArtModel) {
        //start ArtActivity with additional instructions
        startActivityForResult(intentFor<ArtActivity>().putExtra("movie_edit", art), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadArts()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadArts() }
    }

    private fun loadArts() {
        //call all arts stored
        showArts(app.arts.findAll())
    }

    fun showArts(arts: List<ArtModel>) {
        //display all arts stored
        recyclerView.adapter = ArtAdapter(arts, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}

