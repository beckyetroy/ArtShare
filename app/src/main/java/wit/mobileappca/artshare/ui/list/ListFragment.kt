package wit.mobileappca.artshare.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import wit.mobileappca.artshare.R
import wit.mobileappca.artshare.adapters.ArtAdapter
import wit.mobileappca.artshare.adapters.ArtClickListener
import wit.mobileappca.artshare.databinding.FragmentListBinding
import wit.mobileappca.artshare.helpers.createLoader
import wit.mobileappca.artshare.helpers.hideLoader
import wit.mobileappca.artshare.helpers.showLoader
import wit.mobileappca.artshare.models.ArtModel
import wit.mobileappca.artshare.ui.auth.LoggedInViewModel
import wit.mobileappca.artshare.utils.*

class ListFragment : Fragment(), ArtClickListener {

    private var _fragBinding: FragmentListBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader : AlertDialog
    private val listViewModel: ListViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.app_name)
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.fab.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToCreateFragment()
            findNavController().navigate(action)
        }
        showLoader(loader,"Downloading Art")
        listViewModel.observableArtsList.observe(viewLifecycleOwner, Observer {
                arts ->
            arts?.let {
                render(arts as ArrayList<ArtModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader,"Deleting Artwork")
                val adapter = fragBinding.recyclerView.adapter as ArtAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                listViewModel.delete(listViewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as ArtModel).uid!!)
                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)


        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onArtClick(viewHolder.itemView.tag as ArtModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged();

        fragBinding.artSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    //show the arts found using the search function (see ArtAdapter)
                    //showArts(app.arts.search(newText))
                }
                return false
            }
        })
        return root;
    }

    private fun render(artsList: List<ArtModel>) {
        fragBinding.recyclerView.adapter = ArtAdapter(artsList as MutableList<ArtModel>,this,
            listViewModel.readOnly.value!!)
        if (artsList.isEmpty()) {
            fragBinding.artSearch.visibility = View.GONE
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.artsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.artsNotFound.visibility = View.GONE
            fragBinding.artSearch.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ListFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)

        val item = menu.findItem(R.id.toggleArts) as MenuItem
        item.setActionView(R.layout.togglebutton_layout)
        val toggleArts: SwitchCompat = item.actionView.findViewById(R.id.toggleButton)
        toggleArts.isChecked = false

        toggleArts.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) listViewModel.loadAll()
                else listViewModel.load()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader,"Downloading Artwork")
            if(listViewModel.readOnly.value!!)
                listViewModel.loadAll()
            else
                listViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader,"Downloading Artwork")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                listViewModel.liveFirebaseUser.value = firebaseUser
                listViewModel.load()
            }
        })
    }

    override fun onArtClick(art: ArtModel) {
        val action = ListFragmentDirections.actionListFragmentToArtDetailFragment(art.uid!!)
        if(!listViewModel.readOnly.value!!)
            findNavController().navigate(action)
    }

    fun showArts(arts: List<ArtModel>) {
        //display all arts stored
        fragBinding.recyclerView.adapter = ArtAdapter(arts as MutableList<ArtModel>, this,
            listViewModel.readOnly.value!!)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged()
    }
}