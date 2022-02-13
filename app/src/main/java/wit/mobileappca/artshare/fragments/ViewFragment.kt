package wit.mobileappca.artshare.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_view.*
import org.wit.artshare.R
import org.wit.artshare.activities.ArtAdapter
import org.wit.artshare.activities.ArtListener
import org.wit.artshare.databinding.FragmentViewBinding
import org.wit.artshare.main.MainApp
import org.wit.artshare.models.ArtModel

class ViewFragment : Fragment(), ArtListener {

    lateinit var app: MainApp
    private var _fragBinding: FragmentViewBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentViewBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.app_name)

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.recyclerView.adapter = ArtAdapter(app.arts.findAll(), this)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged();

        fragBinding.artSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    //show the arts found using the search function (see ArtAdapter)
                    showArts(app.arts.search(newText))
                }
                return false
            }
        })
        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ViewFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_view, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onArtClick(art: ArtModel) {
        val args = Bundle()
        args.putParcelable("art_edit", art)
        val editFragment = CreateFragment()
        editFragment.arguments = args
        fragmentManager
            ?.beginTransaction()
            ?.add(R.id.nav_host_fragment, editFragment)
            ?.commit()
    }

    fun showArts(arts: List<ArtModel>) {
        //display all arts stored
        recyclerView.adapter = ArtAdapter(arts, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}