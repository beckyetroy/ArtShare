package wit.mobileappca.artshare.ui.list

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_list.*
import wit.mobileappca.artshare.R
import wit.mobileappca.artshare.adapters.ArtAdapter
import wit.mobileappca.artshare.adapters.ArtListener
import wit.mobileappca.artshare.databinding.FragmentListBinding
import wit.mobileappca.artshare.main.MainApp
import wit.mobileappca.artshare.models.ArtModel
import wit.mobileappca.artshare.ui.create.CreateFragment

class ListFragment : Fragment(), ArtListener {

    lateinit var app: MainApp
    private var _fragBinding: FragmentListBinding? = null
    private val fragBinding get() = _fragBinding!!

    private lateinit var listViewModel: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.app_name)

        listViewModel =
            ViewModelProvider(this).get(ListViewModel::class.java)

        val fab: FloatingActionButton = fragBinding.fab
        fab.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToCreateFragment()
            findNavController().navigate(action)
        }

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

    /* TODO IN NEW COMMIT
    private fun render(donationsList: List<DonationModel>) {
        fragBinding.recyclerView.adapter = DonationAdapter(donationsList,this)
        if (donationsList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.donationsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.donationsNotFound.visibility = View.GONE
        }
    }
    */

    companion object {
        @JvmStatic
        fun newInstance() =
            ListFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
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
        listViewModel.load()
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
        fragBinding.recyclerView.adapter = ArtAdapter(arts, this)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged()
    }
}